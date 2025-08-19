<p align="center"><img src= "https://dibrain.wenge.com/wg-agent-manage-uat/static/img/logo-new.81fbf2b9.png" alt="x-agent" width="200" /></p>
<h3 align="center">Open-source platform for building enterprise-grade agents</h3>
<h3 align="center">​All-in-One Enterprise Agent Development Platform</h3>
<p align="center">
  <a href="https://www.gnu.org/licenses/gpl-3.0.html#license-text"><img src="https://img.shields.io/badge/license-GPL3.0-blue" alt="License: GPL v3"></a>
  <a href="https://github.com/1Panel-dev/maxkb/releases/latest"><img src="https://img.shields.io/badge/release-V1.0.1-blue" alt="Latest release"></a>
</p>

<hr/>
<p>
智川X-Agent是中科闻歌推出的一站式企业智能体开发平台，帮助企业零代码快速构建AI应用。智川X-Agent基于封装大模型、知识库、工作流等复杂技术模块为可视化组件，用户通过简单的拖拽和配置可搭建符合业务需求的AI应用。智川X-Agent平台支持多种大模型（如雅意、文心一言等），提供知识库管理、工作流编排、应用发布等功能，满足政务、金融、媒体等多行业需求，助力企业实现AI应用的极速落地与高效迭代，加速AI普惠化。
</p>
<hr/>
## 快速开始

通过docker 镜像启动 x-agent:

```bash
docker volume create agent-x-data
docker volume create agent-x-cicd
docker pull ccr.ccs.tencentyun.com/wenge/agent-x:agent-x_no_bge_250815_05
docker run  -d --restart=always -p 80:80 -p 443:443 -p 8848:8848 -p 3306:3306 -p 6379:6379 -p 9200:9200 -p 9000:9000 -p 9001:9001 -e IP_ADDR="127.0.0.1:80" -v agent-x-data:/u01/isi -v agent-x-cicd:/app/agent/server  --name agent-x  ccr.ccs.tencentyun.com/wenge/agent-x:agent-x_no_bge_250815_05
```
访问管理后台 `http://127.0.0.1:80/wg-agent-manage/#/appmanage` 

- 账号: agent-x
- 密码: 04p9xa0gAE*%&Op8


## 访问 nacos
> http://127.0.0.1:8848/nacos/
> - 账号: nacos
> - 密码: k2j210w5CKKO!&Wh0

## 连接数据 mysql
> 127.0.0.1:3306
> - 账号: root
> - 密码: 2ievD%GBA6
> - 主库：smart_customer_agent

## 访问minio
> http://127.0.0.1:9000
> - 账号: admin
> - 密码: 6838BHE%%C472


## 启动算法服务

```bash
docker pull ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_v2
mkdir -p /u01/isi/code_sdk
#下载 算法配置文件包并上传到服务器指定目录，配置文件包在目录：/config/Agent_X.zip
unzip Agent_X.zip
```

### 1.启动向量模型
```bash
docker run -d -v /u01/isi/code_sdk/Embedding_model/config.yml:/app/config.yml -v /u01/isi/code_sdk/Embedding_model/main.py:/app/main.py  -p 10822:8080 ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_v2
```

### 2.工作流代码节点
```bash
docker run -d -v /u01/isi/code_sdk/Code_node/main.py:/app/main.py  -p 1216:8080 ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_v2
```

### 3.智能问数（NL2SQL）
```bash
docker run -d -v /u01/isi/code_sdk/Nl2sql/config.yaml:/app/config.yaml -v /u01/isi/code_sdk/Nl2sql/main.py:/app/main.py  -p 1025:8080 ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_v2
```

### 4.MCP
```bash
docker run -d -v /u01/isi/code_sdk/MCP/config.yml:/app/config.yml -v /u01/isi/code_sdk/MCP/main.py:/app/main.py  -p 4011:8080  ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_v2
```

### 5.本地自定义安装 mcp
```bash
docker run -d --name algorithm-local-mcp --net host -v /u01/isi/code_sdk/Auto_mcp/config.yml:/app/config.yml -v /u01/isi/code_sdk/Auto_mcp/main.py:/app/main.py  -v  /u01/isi/code_sdk/Auto_mcp/mcp_file:/app/mcp_file ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_v2
```

### 6.MCP_nl2sql
```bash
docker run -d -v /u01/isi/code_sdk/mcp_sql/config.yaml:/app/config.yaml -v /u01/isi/code_sdk/mcp_sql/main.py:/app/main.py  -p 4016:8080 ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_v2
```

```bash
docker pull ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_chrome_v2
```

### 7.单网页内容爬取
```bash
docker run -d -v /u01/isi/code_sdk/URL_analysis/config.yml:/app/config.yml -v /u01/isi/code_sdk/URL_analysis/main.py:/app/main.py  -p 9007:8080 ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_chrome_v2
```

### 8.网页截图
```bash
docker run -d -v /u01/isi/code_sdk/URL_to_img/config.yml:/app/config.yml -v /u01/isi/code_sdk/URL_to_img/main.py:/app/main.py  -p 5028:8080 ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_chrome_v2
```

## yayi算法
### 9.重排序服务
```bash
docker pull ccr.ccs.tencentyun.com/wenge/agent-x:reranker.v1
docker run -d -p 9098:8080 --restart=always --name reranker ccr.ccs.tencentyun.com/wenge/agent-x:reranker.v1
```

### 10.文档智能解析
```bash
docker pull ccr.ccs.tencentyun.com/wenge/agent-x:contentparse.v4.7
docker run -d -p 9099:8080 --restart=always  --name content_parse ccr.ccs.tencentyun.com/wenge/agent-x:contentparse.v4.7
```

### 11.文档切片
```bash
docker pull ccr.ccs.tencentyun.com/wenge/agent-x:doc_answer_noes_nosql.v1.2.8-build2503143-encrypted
docker run -d -p 9097:8080 --restart=always -v /u01/isi/code_sdk/doc_answer_config/config.yml:/code/config/config.yml  --name yayi-plugin-doc-answer-250623 ccr.ccs.tencentyun.com/wenge/agent-x:doc_answer_noes_nosql.v1.2.8-build2503143-encrypted
```

---
> **注意**：如果默认服务的 ip 和端口有变动，请调整对应的配置项！
### nacos配置项
```yaml
appframe:
  yayi:
    # 10.文档智能解析
    contentparsingnewversion:
      uri: http://172.17.0.0.1:9099/analysis
    # 9.重排序服务
    rearrange:
      uri: http://172.17.0.0.1:9098/analysis
    # 11.文档切片
    knowledgesplit:
      uri: http://172.17.0.1:9097/analysis
# 网页快照
screenshot:
  # 图片上传
  uploadUrl: http://172.17.0.1:80/smart-agent-api/wos/file/upload
  # 8.网页截图
  api: http://172.17.0.1:5028/capture-screenshot

# mcp服务api
mcp:
  # 4.MCP
  serviceApi: http://172.17.0.1:4011/service
  # 4.MCP
  queryApi: http://172.17.0.1:4011/query
  # 5.本地自定义安装 mcp
  buildMcpApi: http://172.17.0.1:4011/deploy_service
  # 3.智能问数（NL2SQL）
  textToSqlSse: http://172.17.0.1:1025/get_answer_text2sql

workflow:
  default:
    # 2.工作流代码节点
    codeApi: http://172.17.0.1:1216/execute
    # 开始节点的改写时使用的大模型 id（llm_info表的 model_id字段）
    startRewriteModelId: 87026c3464664ad49a8b622ec719fa70
```
### MYSQL配置
```mysql
-- 1.启动向量模型
use smart_customer_agent;
update smart_customer_agent.dense_vector set uri='http://172.17.0.1:10822/analysis' where code = 'local_bge_768';
```

## 技术栈

- 前端： vue
-  后端： java
-  算法： python
-  数据库： mysql
- 中间件：elasticsearch,redis,minio,nginx

### docker-compose.yml
```yaml

version: '3.8'
services:
  # ========== 主服务：x-agent ==========
  agent-x:
    image: ccr.ccs.tencentyun.com/wenge/agent-x:agent-x_no_bge_250815_05
    container_name: agent-x
    restart: always
    ports:
      - "80:80"     # 替换为浏览器将要访问的端口，与IP_ADDR参数的端口一致
      - "443:443"	# https
      - "8848:8848" # Nacos
      - "3306:3306" # MySQL
      - "6379:6379" # Redis
      - "9200:9200" # Elasticsearch
      - "9000:9000" # MinIO
      - "9001:9001" # MinIO
    environment:
      - IP_ADDR=127.0.0.1:80 # 替换为浏览器将要访问的 ip 和端口
    volumes:
      - agent-x-data:/u01/isi
      - agent-x-cicd:/app/agent/server

  # ========== 算法服务 ==========
  # 1. 向量模型
  algorithm-vector:
    image: ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_v2
    container_name: algorithm-vector
    restart: always
    ports:
      - "10822:8080"  # 默认端口10822，如果默认端口10822有变动，请修改 mysql 的配置项:use smart_customer_agent; update smart_customer_agent.dense_vector set uri='http://172.17.0.1:10822/analysis' where code = 'local_bge_768';
    volumes:
      - ./code_sdk/Embedding_model/config.yml:/app/config.yml
      - ./code_sdk/Embedding_model/main.py:/app/main.py

  # 2. 工作流代码节点
  algorithm-code-node:
    image: ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_v2
    container_name: algorithm-code-node
    restart: always
    ports:
      - "1216:8080"  #默认端口1216，如果默认端口1216有变动，请修改 nacos 的配置项: workflow.default.codeApi: http://172.17.0.1:1216/execute
    volumes:
      - ./code_sdk/Code_node/main.py:/app/main.py

  # 3. 智能问数（NL2SQL）
  algorithm-nl2sql:
    image: ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_v2
    container_name: algorithm-nl2sql
    restart: always
    ports:
      - "1025:8080" #默认端口1025，如果默认端口1025有变动，请修改 nacos 的配置项: mcp.textToSqlSse: http://172.17.0.1:1025/get_answer_text2sql
    volumes:
      - ./code_sdk/Nl2sql/config.yaml:/app/config.yaml
      - ./code_sdk/Nl2sql/main.py:/app/main.py

  # 4. MCP
  algorithm-mcp:
    image: ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_v2
    container_name: algorithm-mcp
    restart: always
    ports:
      - "4011:8080" #默认端口4011，如果默认端口4011有变动，请修改 nacos 的配置项: mcp.buildMcpApi: http://172.17.0.1:4011/deploy_service
    volumes:
      - ./code_sdk/MCP/config.yml:/app/config.yml
      - ./code_sdk/MCP/main.py:/app/main.py

  # 5. 本地自定义 MCP (使用 host 网络)
  algorithm-local-mcp:
    image: ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_v2
    container_name: algorithm-local-mcp
    restart: always
    network_mode: host  # 使用宿主机网络
    volumes:
      - ./code_sdk/Auto_mcp/config.yml:/app/config.yml
      - ./code_sdk/Auto_mcp/main.py:/app/main.py
      - ./code_sdk/Auto_mcp/mcp_file:/app/mcp_file

  # 8. 网页截图
  algorithm-url-to-img:
    image: ccr.ccs.tencentyun.com/wenge/agent-x:algorithm_chrome_v2
    container_name: algorithm-url-to-img
    restart: always
    ports:
      - "5028:8080" #默认端口5028，如果默认端口5028有变动，请修改 nacos 的配置项: screenshot.api: http://172.17.0.1:5028/capture-screenshot
    volumes:
      - ./code_sdk/URL_to_img/config.yml:/app/config.yml
      - ./code_sdk/URL_to_img/main.py:/app/main.py

  # 9. 重排序服务 (yayi)
  algorithm-reranker:
    image: ccr.ccs.tencentyun.com/wenge/agent-x:reranker.v1
    container_name: algorithm-reranker
    restart: always
    ports:
      - "9098:8080" #默认端口9098，如果默认端口9098有变动，请修改 nacos 的配置项: appframe.yayi.rearrange.uri: http://172.17.0.0.1:9098/analysis

  # 10. 文档智能解析 (yayi)
  algorithm-content-parse:
    image: ccr.ccs.tencentyun.com/wenge/agent-x:contentparse.v4.7
    container_name: algorithm-content-parse
    restart: always
    ports:
      - "9099:8080" #默认端口9099，如果默认端口9099有变动，请修改 nacos 的配置项: appframe.yayi.contentparsingnewversion.uri: http://172.17.0.0.1:9099/analysis

  doc_answer_noes:
    image: ccr.ccs.tencentyun.com/wenge/agent-x:doc_answer_noes_nosql.v1.2.8-build2503143-encrypted
    container_name: doc_answer_noes
    restart: always
    volumes:
      - /u01/isi/code_sdk/doc_answer_config/config.yml:/code/config/config.yml
    ports:
      - "9097:8080" #默认端口9097，如果默认端口9097有变动，请修改 nacos 的配置项: appframe.yayi.knowledgesplit.uri: http://172.17.0.0.1:9097/analysis

# ========== 数据卷定义 ==========
volumes:
  agent-x-data:
    external: true  # 对应你之前创建的 docker volume create agent-x-data
  agent-x-cicd:
    external: true  # 对应你之前创建的 docker volume create agent-x-cicd

```


### 相关脚本
```shell
# 启动所有服务
docker-compose up -d
# 查看运行状态
docker-compose ps
# 查看日志
docker-compose logs -f [服务名]
```

## 联系工作人员
<img src="./worker.png" alt="图片描述" width="200" />
