package com.fanxb.bookmark.business.bookmark.service.impl;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.*;
import cn.hutool.core.util.HashUtil;
import com.alibaba.fastjson.JSON;
import com.fanxb.bookmark.business.api.UserApi;
import com.fanxb.bookmark.business.bookmark.constant.FileConstant;
import com.fanxb.bookmark.business.bookmark.dao.BookmarkDao;
import com.fanxb.bookmark.business.bookmark.dao.HostIconDao;
import com.fanxb.bookmark.business.bookmark.entity.BookmarkEs;
import com.fanxb.bookmark.business.bookmark.entity.MoveNodeBody;
import com.fanxb.bookmark.business.bookmark.entity.redis.BookmarkDeleteMessage;
import com.fanxb.bookmark.business.bookmark.entity.redis.VisitNumPlus;
import com.fanxb.bookmark.business.bookmark.service.BookmarkService;
import com.fanxb.bookmark.business.bookmark.service.PinYinService;
import com.fanxb.bookmark.common.constant.CommonConstant;
import com.fanxb.bookmark.common.constant.EsConstant;
import com.fanxb.bookmark.common.constant.RedisConstant;
import com.fanxb.bookmark.common.entity.po.Bookmark;
import com.fanxb.bookmark.common.exception.CustomException;
import com.fanxb.bookmark.common.util.*;
import com.mysql.cj.conf.url.SingleConnectionUrl;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.print.Book;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 类功能简述：
 * 类功能详述：
 *
 * @author fanxb
 */
@Service
@Slf4j
public class BookmarkServiceImpl implements BookmarkService {
    @Value("${urlIconAddress}")
    private String urlIconAddress;

    private final BookmarkDao bookmarkDao;
    private final PinYinService pinYinService;
    private final UserApi userApi;
    private final EsUtil esUtil;
    private final HostIconDao hostIconDao;

    @Autowired
    public BookmarkServiceImpl(BookmarkDao bookmarkDao, PinYinService pinYinService, UserApi userApi, EsUtil esUtil, HostIconDao hostIconDao) {
        this.bookmarkDao = bookmarkDao;
        this.pinYinService = pinYinService;
        this.userApi = userApi;
        this.esUtil = esUtil;
        this.hostIconDao = hostIconDao;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void parseBookmarkFile(int userId, MultipartFile file, String path) throws Exception {
        List<Bookmark> bookmarks = new ArrayList<>();
        //获取当前层sort最大值
        Integer sortBase = bookmarkDao.selectMaxSort(userId, path);
        if (sortBase == null) {
            sortBase = 0;
        }
        if (file.getOriginalFilename().endsWith(".db3")) {
            //处理db文件
            readFromOneEnv(bookmarks, userId, file, path, sortBase);
        } else {
            InputStream stream = file.getInputStream();
            Document doc = Jsoup.parse(stream, "utf-8", "");
            Elements elements = doc.select("html>body>dl>dt");
            for (int i = 0, length = elements.size(); i < length; i++) {
                dealBookmark(userId, elements.get(i), path, sortBase + i, bookmarks);
            }
        }

        //每一千条处理插入一次,批量更新搜索字段
        List<Bookmark> tempList = new ArrayList<>(1000);
        for (int i = 0; i < bookmarks.size(); i++) {
            tempList.add(bookmarks.get(i));
            if (tempList.size() == 1000 || i == bookmarks.size() - 1) {
                tempList = pinYinService.changeBookmarks(tempList);
                bookmarkDao.updateSearchKeyBatch(tempList);
                tempList.clear();
            }
        }
        userApi.versionPlus(userId);

        //异步更新icon
        ThreadPoolUtil.execute(() -> {
            updateUserBookmarkIcon(userId);
            userApi.versionPlus(userId);
        });
    }

    /**
     * Description: 处理html节点，解析出文件夹和书签
     *
     * @param ele  待处理节点
     * @param path 节点路径，不包含自身
     * @param sort 当前层级中的排序序号
     * @author fanxb
     */
    private void dealBookmark(int userId, Element ele, String path, int sort, List<Bookmark> bookmarks) {
        if (!DT.equalsIgnoreCase(ele.tagName())) {
            return;
        }
        Element first = ele.child(0);
        if (A.equalsIgnoreCase(first.tagName())) {
            //说明为链接
            Bookmark node = new Bookmark(userId, path, first.ownText(), first.attr("href"), ""
                    , Long.parseLong(first.attr("add_date")) * 1000, sort);
            //存入数据库
            insertOne(node);
            bookmarks.add(node);
        } else {
            //说明为文件夹
            Bookmark node = new Bookmark(userId, path, first.ownText(), Long.parseLong(first.attr("add_date")) * 1000, sort);
            Integer sortBase = 0;
            //同名文件夹将会合并
            if (insertOne(node)) {
                sortBase = bookmarkDao.selectMaxSort(node.getUserId(), path);
                if (sortBase == null) {
                    sortBase = 0;
                }
            }
            String childPath = path + "." + node.getBookmarkId();
            Elements children = ele.child(1).children();
            for (int i = 0, size = children.size(); i < size; i++) {
                dealBookmark(userId, children.get(i), childPath, sortBase + i + 1, bookmarks);
            }
        }
    }

    /**
     * 处理oneenv的导出
     *
     * @param bookmarks 书签列表
     * @param userId    用户id
     * @param file      file
     * @param path      path
     * @param sort      sort
     */
    private void readFromOneEnv(List<Bookmark> bookmarks, int userId, MultipartFile file, String path, int sort) {
        String filePath = CommonConstant.fileSavePath + "/files/" + IdUtil.simpleUUID() + ".db3";
        try {
            file.transferTo(FileUtil.newFile(filePath));
            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + filePath)) {
                Statement stat = conn.createStatement();
                ResultSet rs = stat.executeQuery("select * from on_categorys");
                Map<Long, Bookmark> folderMap = new HashMap<>();
                Map<Long, Integer> childSortBaseMap = new HashMap<>();
                while (rs.next()) {
                    long addTime = rs.getLong("add_time");
                    Bookmark folder = new Bookmark(userId, path, StrUtil.nullToEmpty(rs.getString("name")), addTime == 0 ? System.currentTimeMillis() : addTime * 1000, sort++);
                    int childSortBase = 0;
                    if (insertOne(folder)) {
                        childSortBase = ObjectUtil.defaultIfNull(bookmarkDao.selectMaxSort(userId, path), 0);
                    }
                    long id = rs.getLong("id");
                    folderMap.put(id, folder);
                    childSortBaseMap.put(id, childSortBase);
                }
                rs.close();
                rs = stat.executeQuery("select * from on_links");
                while (rs.next()) {
                    long fId = rs.getLong("fid");
                    long addTime = rs.getLong("add_time");
                    int tempSort = childSortBaseMap.get(fId);
                    childSortBaseMap.put(fId, tempSort + 1);
                    Bookmark folder = folderMap.get(fId);
                    String curPath = folder == null ? "" : folder.getPath() + "." + folder.getBookmarkId();
                    Bookmark bookmark = new Bookmark(userId, curPath, StrUtil.nullToEmpty(rs.getString("title"))
                            , StrUtil.nullToEmpty(rs.getString("url")), "", addTime == 0 ? System.currentTimeMillis() : addTime * 1000, tempSort);
                    bookmarks.add(bookmark);
                    insertOne(bookmark);
                }
                rs.close();
                stat.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Description: 插入一条书签，如果已经存在同名书签将跳过
     *
     * @param node node
     * @return boolean 如果已经存在返回true，否则false
     * @author fanxb
     */
    private boolean insertOne(Bookmark node) {
        //先根据name,userId,parentId获取此节点id
        Integer id = bookmarkDao.selectIdByUserIdAndNameAndPath(node.getUserId(), node.getName(), node.getPath());
        if (id == null) {
            bookmarkDao.insertOne(node);
            return false;
        } else {
            node.setBookmarkId(id);
            return true;
        }
    }


    @Override
    public Map<String, List<Bookmark>> getOneBookmarkTree(int userId) {
        List<Bookmark> list = bookmarkDao.getListByUserId(userId);
        Map<String, List<Bookmark>> map = new HashMap<>(50);
        list.forEach(item -> {
            map.computeIfAbsent(item.getPath(), k -> new ArrayList<>());
            map.get(item.getPath()).add(item);
        });
        return map;
    }


    @Override
    public List<Bookmark> getBookmarkListByPath(int userId, String path) {
        return bookmarkDao.getListByUserIdAndPath(userId, path);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(int userId, List<String> pathList, List<Integer> bookmarkIdList) {
        //所有要删除的书签id
        Set<String> set = new HashSet<>();
        for (String path : pathList) {
            Integer id = Integer.parseInt(ArrayUtil.reverse(path.split("\\."))[0]);
            set.addAll(bookmarkDao.getChildrenBookmarkId(userId, path).stream().map(String::valueOf).collect(Collectors.toSet()));
            //删除此文件夹所有的子节点
            bookmarkDao.deleteUserFolder(userId, path);
            bookmarkIdList.add(id);
        }
        if (bookmarkIdList.size() > 0) {
            bookmarkDao.deleteUserBookmark(userId, bookmarkIdList);
            set.addAll(bookmarkIdList.stream().map(String::valueOf).collect(Collectors.toSet()));
        }
        RedisUtil.addToMq(RedisConstant.BOOKMARK_DELETE_ES, new BookmarkDeleteMessage(userId, set));
        userApi.versionPlus(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Bookmark addOne(Bookmark bookmark) {
        int userId = UserContextHolder.get().getUserId();
        Integer sort = bookmarkDao.selectMaxSort(userId, bookmark.getPath());
        bookmark.setSort(sort == null ? 1 : sort + 1);
        bookmark.setUserId(userId);
        bookmark.setCreateTime(System.currentTimeMillis());
        bookmark.setAddTime(bookmark.getCreateTime());
        bookmark.setIcon(getIconPath(bookmark.getUrl(), bookmark.getIcon(), bookmark.getIconUrl()));
        //文件夹和书签都建立搜索key
        pinYinService.changeBookmark(bookmark);
        bookmarkDao.insertOne(bookmark);
        userApi.versionPlus(userId);
        return bookmark;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String updateOne(int userId, Bookmark bookmark) {
        bookmark.setUserId(userId);
        if (bookmark.getType() == 0) {
            pinYinService.changeBookmark(bookmark);
            bookmark.setIcon(getIconPath(bookmark.getUrl(), null, null));
        }
        bookmarkDao.editBookmark(bookmark);
        userApi.versionPlus(userId);
        return bookmark.getIcon();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveNode(int userId, MoveNodeBody body) {
        if (body.getSort() == -1) {
            Integer max = bookmarkDao.selectMaxSort(userId, body.getTargetPath());
            body.setSort(max == null ? 1 : max);
        } else {
            //更新目标节点的sort
            bookmarkDao.sortPlus(userId, body.getTargetPath(), body.getSort());
        }
        //如果目标位置和当前位置不在一个层级中需要更新子节点的path
        if (!body.getTargetPath().equals(body.getSourcePath())) {
            bookmarkDao.updateChildrenPath(userId, body.getSourcePath() + "." + body.getBookmarkId()
                    , body.getTargetPath() + "." + body.getBookmarkId());
        }
        //更新被移动节点的path和sort
        bookmarkDao.updatePathAndSort(userId, body.getBookmarkId(), body.getTargetPath(), body.getSort());
        userApi.versionPlus(userId);
    }

    @Override
    public List<BookmarkEs> searchUserBookmark(int userId, String context) {
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.must(QueryBuilders.termQuery("userId", userId));
        boolQueryBuilder.must(QueryBuilders.multiMatchQuery(context, "name", "url"));
        SearchSourceBuilder builder = new SearchSourceBuilder();
        builder.size(5);
        builder.query(boolQueryBuilder);
        return esUtil.search(EsConstant.BOOKMARK_INDEX, builder, BookmarkEs.class);
    }

    @Override
    public void visitNumPlus(int id) {
        VisitNumPlus item = new VisitNumPlus(UserContextHolder.get().getUserId(), id);
        RedisUtil.addToMq(RedisConstant.BOOKMARK_VISIT_NUM_PLUS, JSON.toJSONString(item));
    }

    @Override
    public List<Bookmark> userPopular(int num) {
        return bookmarkDao.selectPopular(UserContextHolder.get().getUserId(), num);
    }

    @Override
    public void updateUserBookmarkIcon(int userId) {
        log.info("开始更新:{}", userId);
        int size = 100;
        int start = 0;
        List<Bookmark> deal;
        while (!(deal = bookmarkDao.selectUserNoIcon(userId, start, size)).isEmpty()) {
            start += size;
            deal.forEach(item -> {
                String icon = getIconPath(item.getUrl(), null, null);
                if (StrUtil.isNotEmpty(icon)) {
                    bookmarkDao.updateIcon(item.getBookmarkId(), icon);
                }
            });
        }
        userApi.versionPlus(userId);
    }

    @Override
    public Set<String> dealBadBookmark(boolean delete, int userId) {
        List<Bookmark> bookmarks = bookmarkDao.selectBookmarkIdPathByUserId(userId);
        Set<Integer> idSet = new HashSet<>(bookmarks.size());
        bookmarks.forEach(item -> idSet.add(item.getBookmarkId()));
        Set<String> resPath = new HashSet<>();
        bookmarks.forEach(item -> {
            if (StrUtil.isEmpty(item.getPath())) {
                return;
            }
            String parentId = item.getPath().substring(item.getPath().lastIndexOf(".") + 1);
            if (!idSet.contains(Integer.valueOf(parentId))) {
                resPath.add(item.getPath());
            }
        });
        if (delete && resPath.size() > 0) {
            resPath.forEach(item -> bookmarkDao.deleteUserFolder(userId, item));
            userApi.versionPlus(userId);
        }
        return resPath;
    }

    /**
     * 获取icon,通过网络获取，或者从base64还原
     *
     * @param url     url
     * @param icon    icon
     * @param iconUrl iconUrl
     * @return {@link String}
     * @author fanxb
     */
    private String getIconPath(String url, String icon, String iconUrl) {
        String host;
        try {
            URL urlObj = new URL(url);
            host = urlObj.getAuthority();
        } catch (Exception e) {
            log.warn("url无法解析出domain:{}", url);
            return "";
        }
        if (StrUtil.isNotBlank(icon)) {
            //优先从base64还原出图片
            try {
                byte[] b = Base64Decoder.decode(icon.substring(icon.indexOf(",") + 1));
                String iconPath = saveToFile(iconUrl, host, b);
                hostIconDao.deleteByHost(host);
                hostIconDao.insert(host, iconPath);
                return iconPath;
            } catch (Exception e) {
                log.error("解析base64获取icon故障:{}", iconUrl, e);
            }
        }

        String iconPath = hostIconDao.selectByHost(host);
        if (iconPath != null) {
            return iconPath;
        }
        //再根据url解析
        iconPath = saveFile(host, urlIconAddress + "/icon?url=" + host + "&size=16..128..256");
        if (StrUtil.isNotEmpty(iconPath)) {
            hostIconDao.insert(host, iconPath);
        }
        return iconPath;
    }

    /**
     * 保存文件到icon路径
     *
     * @param host host
     * @param url  url
     * @return {@link String}
     * @author FleyX
     */
    private String saveFile(String host, String url) {
        try (Response res = HttpUtil.getClient(false).newCall(new Request.Builder().url(url)
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/100.0.4896.75 Safari/537.36 Edg/100.0.1185.36")
                .get().build()).execute()) {
            assert res.body() != null;
            if (!HttpUtil.checkIsOk(res.code())) {
                throw new CustomException("请求错误:" + res.code());
            }
            byte[] data = res.body().byteStream().readAllBytes();
            if (data.length > 0) {
                String iconUrl = new URL(res.request().url().toString()).getPath();
                return saveToFile(iconUrl, host, data);
            } else {
                log.info("未获取到icon:{}", url);
            }
        } catch (Exception e) {
            log.error("url获取icon故障:{}", url, e);
        }
        return "";
    }


    /**
     * 保存到文件中
     *
     * @param iconUrl icon文件名
     * @param host    host
     * @param b       数据
     * @return {@link String}
     * @author FleyX
     */
    private String saveToFile(String iconUrl, String host, byte[] b) {
        String fileName = host.replace(":", ".") + iconUrl.substring(iconUrl.lastIndexOf("."));
        String filePath = Paths.get(FileConstant.FAVICON_PATH, host.replace("www", "").replaceAll("\\.", "").substring(0, 2), fileName).toString();
        FileUtil.writeBytes(b, Paths.get(CommonConstant.fileSavePath, filePath).toString());
        return File.separator + filePath;
    }
}
