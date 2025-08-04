# 雅意介绍

# ![image](https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/r4mlQmJDNPRBlxow/img/da99ba79-6c68-422e-b7c1-6d4d82cbf7d6.jpeg)

## 定义

雅意（YAYI），是中科闻歌推出的依托于大规模语言模型的企业级应用构建平台，具备从海量数据和大规模知识中理解、学习和生成的能力，支持与人、数据库、离线文档、在线网页等多种数据形式交互，提供包括多源数据接入、文档解析、阅读理解、大模型训练与管理等功能，可快速对接企业数据并一键生成面向企业的大模型专属应用服务，整个过程只需要几分钟。

## 核心功能

### YAYIChat

#### Prompt 模版

用户点击左侧 Prompt 卡片，Prompt 就会同步在输入框内，用户可以调整 Prompt 中建议的参数（高亮的块即推荐修改的文本），基于场景的需求输入关键词即可获取优质的指令发送给雅意。

![image](https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/r4mlQmJDNPRBlxow/img/015dcc5d-5734-4fe4-bd95-f1ce3ca94876.jpg)

#### Prompt 检索

输入框中输入"/"唤醒检索服务。继续输入关键词即可快速检索 Prompt 模版，上下键切换，回车即可选中指令。友好的交互加速用户使用雅意。

![image](https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/r4mlQmJDNPRBlxow/img/2ccd7c5f-170a-4a36-bb03-f160bdf94eed.jpg)

#### Prompt 构建

对于有构造 Prompt 需求的用户，提供模版编辑功能，输入模版添加参数，添加示例后即可发布到平台上。发布成功后，用户可以在 YAYIChat 首页按分类找到 Prompt 模版并引用。（此能力会着步开放给用户，且支持在线调试，帮助用户构建专属的技能应用）

## YAYIFile

### 答案可溯源

雅意的所有答案都会标注文档来源及页面出处，用户可以点击来源文件的名称或者点击页码一键溯源至原文位置，确保答案精准有依据。

![image](https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/r4mlQmJDNPRBlxow/img/f7e99675-969e-493a-b4b2-458f5a189bd9.jpg)

### 复杂文档解析

用户可以创建并管理文档知识库，当前版本支持 pdf/docx/doc/txt 文档，YAYIFile 精准识别文档段乱，处理复杂排版、内容跨页等情况，确保原始数据精确性；支持对合并单元格、复杂表格等情况精准识别，确保传递准确信息。

### 全库提问

在文档上传成功并解析完成，系统会基于文档内容生成推荐问推荐给用户。若用户需要指令助手，输入“/”就可以唤醒信息抽取、摘要总结、提炼大纲指令用于使用。

### 推荐问

在用户文档上传成功并解析完成，系统会基于文档内容引导提问，并基于答案拓展问题，比如用户@某篇文档并发送，未输入Prompt，系统也会基于该篇文档总结推荐问，主动引导用户了解这个文档。

![image](https://alidocs.oss-cn-zhangjiakou.aliyuncs.com/res/r4mlQmJDNPRBlxow/img/8a40a3fb-5d5e-40cc-93f4-4fc75888cca5.jpg)

### 单文档问答

用户可以基于单篇文档做信息抽取、摘要总结或提炼大纲等指令，若用户在左侧列表中点选某个文档，即可预览该文档；也可以通过输入“@”+关键词的方式检索文档后进行问答。