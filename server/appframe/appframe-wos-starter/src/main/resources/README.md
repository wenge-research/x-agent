## pom依赖
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>

    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>

    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
    </dependency>

    <dependency>
        <groupId>io.minio</groupId>
        <artifactId>minio</artifactId>
    </dependency>
</dependencies>
```

## 引入文件工具
```java
    @Autowired
    private WosUtil wosUtil;
```

## yml配置
```yaml


appframe:
  minio:
    # 是否自动化配置wos
    enable: true
    # minio的服务地址
    endpoint: http://10.97.1.10:9002
    # minio的登录账号
    accessKey: xxxxxx
    # minio的登录密码
    secretKey: xxxxxxx
    # minio的桶
    bucket: xxxxx
    # 网关，可以直接下载文件的链接网关
    gateway: /xxxxxx
```

## 接口说明
### 单文件上传
> **POST http://localhost:8080/wos/file/upload**
>> 参数
>>> file: 附件  
>>> filePath: 文件目录  
>>> rename: true

***
### 多件上传
> **POST http://localhost:8080/wos/file/uploadBatch**
>> 参数
>>> fileList: 附件列表  
>>> filePath: 文件目录  
>>> rename: true

***

### 下载文件
> **GET http://localhost:8080/wos/file/download**
>> 参数
>>> fileKey: 文件名称  

***
### 删除单文件
> **POST http://localhost:8080/wos/file/delete**
>> 参数
>>> {  
>>>  &emsp;#包括文件目录，例如[/dir/2a5fa31f3a.jpg]  
>>>  &emsp; "fileKey": "文件名称"       
>>> }  

***
### 删除多文件
> **POST http://localhost:8080/wos/file/deleteBatch**
>> 参数
>>> {  
>>> &emsp;#包括文件目录，例如[/dir/2a5fa31f3a.jpg]  
>>> &emsp;"fileKeyList": [       
>>> &emsp;&emsp;&emsp;"文件名称列表"  
>>> &emsp;&emsp;]  
>>> }  

***


