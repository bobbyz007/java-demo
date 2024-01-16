# How to set up
以Windows为例， 如果在Linux环境也可以使用Docker启动。

## 数据库 MySQL
安装MySQL，参考官方文档，此处略。  
创建非root用户：
```
create user 'mysql'@'%' identified by 'mysql';
GRANT ALL PRIVILEGES ON *.* TO 'mysql'@'%' WITH GRANT OPTION;
```
登录：
```
 mysql -umysql -p
```

## 注册中心 Nacos
此处以独立本地模式启动nacos，方便快速调试问题。如果在生产环境部署需要配置集群模式。
1. 下载nacos-server-2.3.0.zip， 访问官网下载：https://github.com/alibaba/nacos/releases ，如果访问不了github，则通过sourceforge下载
   https://sourceforge.net/projects/nacos.mirror/files/
2. 登录MySQL服务器，解压缩nacos下载包，执行mysql-schema.sql，初始化nacos数据库表
   ```
   create database nacos_config;
   use nacos_config;

   source D:\workspace\opensource\nacos-server-2.3.0\nacos\conf\mysql-schema.sql
   ```
3. 修改nacos的配置文件中有关数据库配置： conf/application.properties
   ```shell
   #*************** Config Module Related Configurations ***************#
   ### If use MySQL as datasource:
   ### Deprecated configuration property, it is recommended to use `spring.sql.init.platform` replaced.
   spring.datasource.platform=mysql
   # spring.sql.init.platform=mysql
   
   ### Count of DB:
   db.num=1
   
   ### Connect URL of DB:
   db.url.0=jdbc:mysql://127.0.0.1:3306/nacos_config?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
   db.user.0=mysql
   db.password.0=mysql
   ```
4. 以独立模式启动nacos
   ```
   PS D:\workspace\opensource\nacos-server-2.3.0\nacos> .\bin\startup.cmd -m standalone
   ```
   控制台打印启动成功信息：
   ```shell
   2024-01-15 16:48:30,326 INFO Tomcat started on port(s): 8848 (http) with context path '/nacos'
   
   2024-01-15 16:48:30,395 INFO Nacos started successfully in stand alone mode. use embedded storage
   ```
   访问nacos控制台： http://localhost:8848/nacos

## 注册中心 ZooKeeper
1. 下载apache-zookeeper-3.8.3-bin.tar.gz， 访问官网下载：https://zookeeper.apache.org/releases.html
2. 进入配置目录apache-zookeeper-3.8.3-bin\conf， 拷贝zoo_sample.cfg并改名为 zoo.cfg作为默认的配置文件，修改dataDir配置
   ```shell
   dataDir=D:\workspace\opensource\apache-zookeeper-3.8.3-bin\data
   ```
3. 启动和连接server
   ```shell
   .\bin\zkServer.cmd
   
   # 连接zk
   .\bin\zkCli.cmd -server localhost:2181
   ```

