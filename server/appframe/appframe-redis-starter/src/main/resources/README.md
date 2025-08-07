```yaml

spring:
  redis:
    database: 0
    host: 127.0.0.1
    port: 6379
    timeout: 6000ms
    lettuce:
      pool:
        max-active: 1000  #连接池最大连接数（使用负值表示没有限制）
        max-idle: 10 # 连接池中的最大空闲连接
        min-idle: 5 # 连接池中的最小空闲连接
        max-wait: -1 # 连接池最大阻塞等待时间（使用负值表示没有限制）
    password: xxxx

appframe:
  limit:
    count: 50
    period: 2
    enable: true
    prefix: smart-price-server
```

``jave

	/**
	 * 删除应用信息
	 */
	@PostMapping("/deleteApplicationInfo")
	@RedisLimit(period = 50, count = 1, enable = true, prefix = "delete", limitType = LimitTypeEnum.URI)
	public Result deleteApplicationInfo(@RequestBody List<String> idList) {
		return applicationInfoService.deleteApplicationInfo(idList);
	}

````