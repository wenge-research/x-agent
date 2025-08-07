## pom.xml依赖
```xml
    <dependencies>

    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-lang3</artifactId>
    </dependency>

    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>

    <dependency>
        <groupId>com.alibaba.fastjson2</groupId>
        <artifactId>fastjson2</artifactId>
    </dependency>

    <dependency>
        <groupId>cn.hutool</groupId>
        <artifactId>hutool-all</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>

    <dependency>
        <groupId>com.github.ulisesbocchio</groupId>
        <artifactId>jasypt-spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-micro-spring-boot-starter</artifactId>
    </dependency>

    <dependency>
        <groupId>com.github.xiaoymin</groupId>
        <artifactId>knife4j-spring-ui</artifactId>
    </dependency>

    <dependency>
        <groupId>com.alibaba</groupId>
        <artifactId>transmittable-thread-local</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
</dependencies>

```
## 配置文件
```yaml

appframe:
  # 开启公共模块
  common:
    enable: true
  page:
    # 开启分页控制
    enable: true
    # pagesize最大值
    maxPageSize: 201
    # 忽略最大值限制的api
    maxPageSizeIgnoreApi: getApplicationInfoList
  log:
    enable: true
    paramEnable: true
    resultEnable: true
  jasypt:
    enable: true
    salt: cHJpY2U=cHJpY2U=
    encryptor:
      algorithm: PBEWITHHMACSHA512ANDAES_256
      password: Szwg&2023
  cipher:
    enable: true
    md5Key: fsfwef
    whiterApi: 

```