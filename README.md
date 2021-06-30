# fno-RPC-Framework

## 介绍

fno-RPC-Framework 是一个基于Netty+Nacos 实现的 RPC 框架，其中，网络传输部分，采用了java原生BIO方式以及Netty方式，可通过SocketServer（SocketClient）或NettyServer（NettyClient）进行使用，同时，采用多种序列化方式（Google Protobuf，Kryo，Json...）,可通过注解进行配置。

## 架构

## 							<img src="D:\javaProjects\fno-RPC-Framework\img\架构图.jpg" alt="架构图" style="zoom: 50%;" />

Registry：使用Nacos作为注册中心

Provider：服务提供者，向注册中心注册服务

Consumer：服务消费者，通过向注册中心发起请求，得到所需服务的服务提供者地址，之后向对应服务提供者发起调用请求

## 项目模块

- **roc-api** —— RPC框架通用接口
- **rpc-common** —— 实体类、工具类、枚举类等
- **rpc-core** —— 框架的核心实现
- **test-client** —— 测试用服务消费端
- **test-server** —— 测试用服务提供端

## 传输协议

```
+---------------+---------------+-----------------+-------------+
|  Magic Number |  Package Type | Serializer Type | Data Length |
|    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
+---------------+---------------+-----------------+-------------+
|                          Data Bytes                           |
|                   Length: ${Data Length}                      |
+---------------------------------------------------------------+
```

Magic Number：魔数，定义为0xCAFEBABE，表示该包为RPC传输协议包

Package Type：传输包类型，表示该报为请求/响应

Serializer Type：表示该包传输数据所采用的序列化类型

Data Length：数据长度

Data Bytes：具体传输数据

## 使用示例

### 1.服务提供者（Netty方式）

```java
//注解，扫描目录下定义的接口实现类，实现自动注册服务
@ServiceScan
public class TestNettyServer {
    public static void main(String[] args) {
        RpcServer server = new NettyServer(8885);
        server.start();
    }
}
```



### 2.服务消费者（Netty方式）

```java
//消费者端配置注解，支持对负载均衡算法，及序列化方式的配置
@ReferenceConfig(loadBalance = "roundRobin", serializer = "KRYO")
public class TestNettyClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService proxy = rpcClientProxy.getProxy(HelloService.class);
        String s = proxy.hello(new HelloObject(1, "test example01"));
        System.out.println(s);
    }
}
```

