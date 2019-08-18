
**j-id** 提供 依托redis/zookeeper确定机器序号，基于[snowflake](https://github.com/twitter/snowflake)算法的分布式ID生成器。

● require [jdk 1.8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html)

● require [slf4j](http://www.slf4j.org/download.html)

◇ [changelog](https://github.com/tripluo/j-id/blob/master/src/main/resources/changelog.txt)

### Maven: ###

```xml
<dependency>
    <groupId>net.jrouter</groupId>
    <artifactId>j-id</artifactId>
    <version>1.4</version>
</dependency>
```
### spring-boot配置: ###

Sample [application.properties](https://github.com/tripluo/j-id/blob/master/src/test/resources/application.properties)

```properties
#id service properties (IdServiceProperties)
# default: true
net.jrouter.id.enable-local-file-storager=false
# default: /distributed.id
net.jrouter.id.local-file=localhost
# default: distributed.id
net.jrouter.id.redis-hash-key=distributed.id
# default: distributed.id
net.jrouter.id.zk-path=distributed.id
#default: redis, optional: (redis/zookeeper/local/manual)
net.jrouter.id.generator-type=redis
#default: 0, effective when "generatorType=manual"
net.jrouter.id.manual-worker-id=100
```
