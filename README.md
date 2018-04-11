
**j-id** 提供 依托redis/zookeeper确定机器序号，基于[snowflake](https://github.com/twitter/snowflake)算法的分布式ID生成器。


### Maven: ###

```xml
<dependency>
    <groupId>net.jrouter</groupId>
    <artifactId>j-id</artifactId>
    <version>1.1</version>
</dependency>
```
### spring-boot配置: ###

Sample [application.properties](https://github.com/innjj/j-id/blob/master/src/test/resources/application.properties)

```properties
#id service properties (IdServiceProperties)
# default: true
distributed.id.enableLocalFileStorager=false
# default: /distributed.id
distributed.id.localFile=localhost
# default: distributed.id
distributed.id.redisHashKey=distributed.id
# default: distributed.id
distributed.id.zkPath=distributed.id
#default: redis, optional: (redis/zookeeper/local)
distributed.id.generatorType=redis
#distributed.id.generatorType=zookeeper
```
