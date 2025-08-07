@echo off
:: 输出中文时不会乱码
chcp 65001 >null


echo 执行deepseek_r1_1_5b
echo 检查必要环境...


set docker_version=ccr.ccs.tencentyun.com/wenge/agent-llm:deepseek_r1_1_5b
set container_name=agent_x_deepseek_r1_1_5b

:: 创建 es 目录
::  if not exist "C:\agent-x\data" (
::      mkdir "C:\agent-x\data"
::  ) else (
::      echo 目录 "C:\agent-x\data" 已经存在，无需创建。
::  )


:: 检测是否有 docker 环境
docker version >nul 2>&1

REM 根据上一条命令的返回值判断是否安装了Docker
if %errorlevel% equ 0 (
    echo 检查到已安装Docker环境
) else (
        echo 请安装Docker，Docker【官网安装】：https://docs.docker.com/desktop/setup/install/windows-install/
        echo 请安装Docker，Docker【社区安装】：https://blog.csdn.net/QQ1817117243/article/details/139879440
    pause
    exit /b
)


:: 检测镜像是否存在
for /f "tokens=*" %%i in ('docker images -q %docker_version%') do set "image_id=%%i"

if defined image_id (
    echo ===============镜像  %docker_version% 已存在，跳过拉取...===============
) else (
    echo ===============镜像不存在，开始拉取...===============
    docker pull  %docker_version%
    echo ===============拉取镜像命令执行结果 %errorlevel%===============
    if %errorlevel% neq 0 (
        echo ===============镜像拉取失败===============
        pause
        exit /b %errorlevel%
    )
)


:: 检查容器是否存在
for /f "tokens=*" %%j in ('docker ps -a -q --filter "name=%container_name%"') do set "container_id=%%j"

if defined container_id (
        echo ===============容器 %container_name% 已存在，先停止容器...===============
        docker stop %container_name%
        echo ===============停止容器命令执行结果 %errorlevel%===============
        docker rm %container_name%
        echo ===============删除容器命令执行结果 %errorlevel%===============
)

 ::  创建网络
docker network create agent-x-network

:: 启动容器，这里使用-d参数以守护进程模式运行容器，你可以根据需要修改容器名和其他参数
docker run --restart=always -d  --gpus=all  -p 11434:11434 --network agent-x-network  --name %container_name%  -e OLLAMA_HOST=0.0.0.0:11434  %docker_version%

if %errorlevel% equ 0 (
        echo ===============容器启动成功...===============
) else (
        echo ===============容器启动失败...===============
)

pause