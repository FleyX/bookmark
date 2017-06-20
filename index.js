const 
    http=require('http'),
    fs=require('fs'),
    path=require('path'),
    mime=require('mime'),
    chatServer=require('./lib/chat_server');

var cache={};//缓存静态文件内容
//发送错误响应
function send404(response){
    response.writeHead(404,{'Content-Type':'text/plain'});
    response.write('Error 4.4:文件未找到。');
    response.end();
}
//发送文件内容
function sendFile(response,filePath,fileContents){
    response.writeHead(
        200,
        {"content-Type":mime.lookup(path.basename(filePath))}
    );
    response.end(fileContents);
}
//查找文件
function serveStatic(response,cache,absPath){
    if(cache[absPath]){
        sendFile(response,absPath,cache[absPath]);
    }else{
        fs.exists(absPath,function(exists){
            if(exists){
                fs.readFile(absPath,function(err,data){
                    if(err){
                        send404(response);
                    }else{
                        cache[absPath]=data;
                        sendFile(response,absPath,data);
                    }
                });
            }else{
                send404(response);
            }
        });
    }
}


//入口
var server=http.createServer(function(request,response){
    var filePath=false;
    console.log(`new request for ${request.url}`);
    if(request.url==='/'){
        filePath='public/index.html';
    }else{
        filePath='public'+request.url;
    }

    var absPath='./'+filePath;
    serveStatic(response,cache,absPath);
});
server.listen(3000,function(){
    console.log("the server is listening on prot 3000.");
});
chatServer.listen(server); //websocket服务也绑定到该端口上