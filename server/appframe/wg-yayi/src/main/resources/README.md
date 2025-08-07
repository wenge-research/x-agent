```yaml
server:
  tomcat:
    threads:
      max: 1000
      min-spare: 100
    accept-count: 1000
    max-connections: 10000
    accesslog:
      directory: /app/logs/access
  servlet:
    context-path: /
    session:
      cookie:
        name: OAUTH2SESSION
        path: /
      timeout: 10800
spring:
  profiles:
    active: dev
  application:
    name: yayi-model-application
    cnName: 雅意大模型
  servlet:
    multipart:
      max-file-size: 200MB
      max-request-size: 200MB
  mvc:
    pathmatch:
      matching-strategy: ANT_PATH_MATCHER
  main:
    allow-bean-definition-overriding: true
    allow-circular-references: true
logging:
  level:
    root: info
  config: classpath:logback-core.xml
mybatis-plus:
  mapper-locations: classpath:/mappers/*.xml
  configuration:
        log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
#    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl

appframe:
  deploy:
    enable: true
    gateway:
    # 配置根路径
    rootPath:
    #      配置登录用户名
    userName: xxxxx
    #    配置登录密码
    password: xxxx


```