#!/bin/bash
# 本地调试启动脚本：先杀占用端口的进程，再先后启动后端(8088)、前端(4531)
set -e

ROOT_DIR="$(cd "$(dirname "$0")" && pwd)"
BACKEND_PORT=8088
FRONTEND_PORT=4531
BACKEND_URL="http://localhost:${BACKEND_PORT}/bookmark/api"
FRONTEND_URL="http://localhost:${FRONTEND_PORT}"
READY_TIMEOUT=60

# 直接杀死占用指定端口的进程
kill_port() {
    local port=$1
    local pids=""
    if command -v lsof >/dev/null 2>&1; then
        pids=$(lsof -ti:"$port" 2>/dev/null || true)
    elif command -v fuser >/dev/null 2>&1; then
        pids=$(fuser "$port"/tcp 2>/dev/null || true)
    fi
    pids=$(echo "$pids" | tr -s ' ' '\n' | sort -u | grep -E '^[0-9]+$' || true)
    if [ -z "$pids" ]; then
        echo "[端口 $port] 无占用"
        return
    fi
    for pid in $pids; do
        echo "[端口 $port] 杀死进程: $(ps -p "$pid" -o pid=,cmd= 2>/dev/null || echo "pid=$pid")"
        kill -9 "$pid" 2>/dev/null || true
    done
}

# 轮询等待 HTTP 服务就绪（任意响应即视为就绪）
wait_http() {
    local name=$1 url=$2 log=$3 pid=$4
    echo "[$name] 等待服务就绪: $url"
    for ((i = 1; i <= READY_TIMEOUT; i++)); do
        if ! kill -0 "$pid" 2>/dev/null; then
            echo "[$name] 进程已退出，启动失败，日志末尾如下:"
            tail -n 30 "$log"
            exit 1
        fi
        if curl -s -o /dev/null -m 2 "$url"; then
            echo "[$name] 服务已就绪（${i}s）"
            return
        fi
        sleep 1
    done
    echo "[$name] ${READY_TIMEOUT}s 内未就绪，日志末尾如下:"
    tail -n 30 "$log"
    exit 1
}

echo "==> 1/4 检查端口占用"
kill_port $BACKEND_PORT
kill_port $FRONTEND_PORT

echo "==> 2/4 启动后端（端口 $BACKEND_PORT，日志 $ROOT_DIR/app.log）"
cd "$ROOT_DIR/bookMarkService"
if ! ls web/target/*.jar >/dev/null 2>&1; then
    echo "[后端] 首次运行，先执行 mvn install -DskipTests"
    mvn install -DskipTests
fi
nohup mvn spring-boot:run -pl web >"$ROOT_DIR/app.log" 2>&1 &
BACKEND_PID=$!
echo "[后端] 已启动，pid=$BACKEND_PID"
wait_http "后端" "$BACKEND_URL" "$ROOT_DIR/app.log" "$BACKEND_PID"

echo "==> 3/4 启动前端（端口 $FRONTEND_PORT，日志 $ROOT_DIR/front.log）"
cd "$ROOT_DIR/bookmark_front"
if [ ! -d node_modules ]; then
    echo "[前端] node_modules 缺失，先执行 pnpm install"
    pnpm install
fi
nohup pnpm dev >"$ROOT_DIR/front.log" 2>&1 &
FRONTEND_PID=$!
echo "[前端] 已启动，pid=$FRONTEND_PID"
wait_http "前端" "$FRONTEND_URL" "$ROOT_DIR/front.log" "$FRONTEND_PID"

echo "==> 4/4 全部启动完成"
echo "前端访问地址: $FRONTEND_URL"
echo "查看后端日志: tail -f $ROOT_DIR/app.log"
echo "查看前端日志: tail -f $ROOT_DIR/front.log"
