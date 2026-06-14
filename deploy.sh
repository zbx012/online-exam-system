#!/bin/bash
# 在线考试系统 — 一键部署脚本
# 用法: bash deploy.sh

set -e
PROJECT_DIR="/opt/exam"
cd "$PROJECT_DIR"

echo "=================================="
echo "  在线考试系统 — 自动部署脚本"
echo "=================================="

# 1. 拉最新代码
echo "[1/4] 拉取最新代码..."
git pull

# 2. 构建后端
echo "[2/4] 构建后端..."
cd "$PROJECT_DIR/backend"
mvn package -DskipTests -s settings-docker.xml -q
echo "  后端打包完成"

# 3. 构建前端
echo "[3/4] 构建前端..."
cd "$PROJECT_DIR/frontend"
npm run build --silent 2>/dev/null
echo "  前端打包完成"

# 4. 重启后端
echo "[4/4] 重启后端服务..."
kill $(pgrep -f "exam.*jar") 2>/dev/null || true
sleep 1
export SPRING_PROFILES_ACTIVE=local
nohup java -Xms128m -Xmx256m -jar "$PROJECT_DIR/backend/target/"*.jar \
    > "$PROJECT_DIR/app.log" 2>&1 &

sleep 3
if pgrep -f "exam.*jar" > /dev/null; then
    echo ""
    echo "✅ 部署完成！"
    echo "   访问地址: http://$(curl -s ifconfig.me 2>/dev/null || echo '你的IP')"
else
    echo "❌ 启动失败，查看日志: tail -50 $PROJECT_DIR/app.log"
fi
