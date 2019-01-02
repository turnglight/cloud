## Spring actuator

### 应用配置类

+ 获取应用程序中加载的应用配置、 环境变量、 自动化配置报告等与SpringBoot应用密切相关的配置类信息 

#### autoconfig

+ 自动化配置报告
  + positiveMatches中返回的是条件匹配成功的自动化配置
  + negativeMatches中返回的事条件匹配不成功的自动华配置

#### beans

+ 该端点用来获取应用上下文中创建的所有Bean
  + bean
  + scope
  + type: bean的java类型
  + resource：class文件的具体路径
  + dependencies: 依赖的bean名称

#### configprops

+ 该端点用来获取应用中配置的属性信息报告

#### env

+ 用来获取应用所有可用的环境属性报告，包括环境变量、JVM属性、应用的配置属性、命令行中的参数

#### mappings

+ 该端点用来返回所有Spring MVC的控制器映射关系报告 

#### info

+ 该端点用来返回 一些应用自定义的信息 



### 度量指标类

+ 获取应用程序运行过程中用于监控的度量指标， 比如内存信息、 线程池信息、 HTTP请求统计等 

#### metrics

+ 该端点用来返回当前应用的各类重要度量指标，比如内存信息、线程信息、垃圾回收信息 
  + processors：处理器数量
  + uptime、instance.uptime: 运行时间
  + systemload.average: 系统平均负载
  + mem.*： 内存概要
  + httpsessions.*: tomcat的容器会话使用情况

#### health



### 操作控制类

+ 提供了对应用的关闭等操作类功能 

## 服务治理: Spring Cloud Eureka

#### what

- 服务治理可以说是微服务架构中最为核心和基础的模块，它主要用来实现各个微服务实例的自动化注册与发现。

#### why

- 为什么微服务架构需要服务治理

  ```markdown
  随着微服务应用不断增多，人工维护需要花销大量的时间成本，极易产生错误。
  ```

#### how

- 围绕着服务注册与服务发现机制来完成对微服务应用实例的自动化管理

### 搭建注册中心@EnableEurekaService

- **Eureka服务端** 

  ~~~java
  @EnableEurekaServer
  @SpringBootApplication
  public class CloudServerApplication {
      public static void main(String[] args) {
          new SpringApplicationBuilder(CloudServerApplication.class).web(true).run(args);
      }
  }
  ~~~

- **服务中心配置**

  ~~~yaml
  server:
    port: 1111
  
  eureka:
    instance:
      hostname: localhost
    client:
      registerWithEureka: false
      fetchRegistry: false
      serviceUrl:
        defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
  ~~~


- **eureka.client.register-with-eureka**由于该应用为注册中心，所以设置为false，代表不向注册中心注册自己
- **eureka.client.fetch-registry**由于注册中心的职责就是维护服务实例，它并不需要去检索服务，所以也设置为false

### 注册提供服务@EnableDiscoveryClient

- **Eureka客户端** 

  ~~~java
  @EnableDiscoveryClient
  @SpringBootApplication
  public class Application{
      public static void main(String[] args){
          SpringBootApplication.run(Application.class, args);
      }
  }
  
  @RestController
  public class HelloController{
      @Autowired
      private DiscoveryClient client;
      
      @GetMapping(value="/hello")
      public String index(){
          ServiceInstance instance = client.getLocalServiceInstance();
          return "Hello World";
      }
  }
  ~~~

+ **服务注册配置**

  ~~~yaml
  spring:
    application:
      name: hello-service
  
  eureka:
    client:
      serviceUrl:
        defaultZone: http://localhost:1111/eureka
  ~~~



**注**：在主类中通过加载@EnableDiscoveryClient注解，激活Eureka中的DiscoveryClient实现

#### 高可用注册中心

+ 单节点的服务注册中心如果发生故障，整个应用将无法使用

+ EurekaServer的高可用实际上就是将自己作为服务向其他服务注册中心注册自己

  在***搭建注册中心***的配置中，有以下配置

  ~~~yaml
  eureka:
    instance:
      hostname: localhost
    client:
      registerWithEureka: false
      fetchRegistry: false
  ~~~

  registerWithEureka属性表示不向注册中心注册自己

  ##### 多注册中心相互注册

  + 创建**application-peer1.yaml**

    ~~~yaml
    spring:
      application:
        name: eureka-server
    server:
      port: 1111
    eureka:
      instance:
        hostname:peer1
      client:
        serviceUrl:
          defaultZone: http://peer2:1112/eureka/
    ~~~

  + 创建**application-peer2.yaml**

    ~~~yaml
    spring:
      application:
        hostname: eurake-server
    
    server:
      port: 1112
    
    eureka:
      instance:
        hostname: peer2
      client:
        serviceUrl:
          defaultZone: http://peer1:1111/eureka/
    ~~~

  ##### 启动两个注册中心服务

  + 需要修改本地ip路由映射，C:\Windows\System32\drivers\etc\hosts
    + 如果不想使用主机名来定义注册中心的地址，也可以使用IP地址的形式

      + eureka.instance.prefer-ip-address=true，该值默认为false

        ~~~xml
        127.0.0.1	peer1
        127.0.0.1	peer2
        ~~~

  + 启动服务

    ~~~xml
    java -jar cloud-server-0.0.1.jar --spring.profiles.active=peer1
    java -jar cloud-server-0.0.1.jar --spring.profiles.active=peer2
    ~~~

#### 服务注册集群

+ 修改服务的spring配置文件

  ~~~yaml
  eureka:
    client:
      serviceUrl:
        defaultZone: http://peer1:1111/eureka/,http://peer2:1112/eureka/
  ~~~

  此时启动服务，可以看到服务在peer1和peer2中均有注册，其中只要有一个注册中心能正常使用，不可以保持高可用。

### 服务发现与消费

- **发现服务**由Eurake的客户端完成
- **消费服务**由Ribbon的完成

#### Ribbon

+ 是一个基于http与tcp的客户端负载均衡器
  + 可以通过客户端中设置的**ribbonServerList**服务端列表去轮询访问达到负载均衡的作用
+ ribbon与eureka联合使用
  + Ribbon的服务实例清单RibbonServerList会被DiscoveryEnabledNIWSServerList重写，扩展成从Eureka中获取服务实例
  + Iping也会被NIWSDiscoveryPing取代，它将职责委托给Eureka来确定服务端是否启动

## 客户端负载均衡：Spring Cloud Ribbon

#### what

+ Spring Cloud Ribbon 是一个基于 HTTP 和 TCP 的客户端负载均衡工具，它基于 Netflix Ribbon 实现。通过 Spring Cloud 的封装， 可以让我们轻松地将面向服务的 REST 模板请求自动转换成客户端负载均衡的服务调用

#### why

+ 负载均衡在系统架构中是一个非常重要， 并且是不得不去实施的内容。 因为负载均衡是对系统的**高可用**、 **网络压力**的缓解和**处理能力**扩容的重要手段之一。 我们通常所说的负载均衡都指的是服务端负载均衡， 其中分为**硬件负载**均衡和**软件负载**均衡。 硬件负载均衡主要通过在服务器节点之间安装专门用于负载均衡的设备，比如 F5 等；***而软件负载均衡则是通过在服务器上安装一 些具有均衡负载功能或模块的软件来完成请求分发工作***， 比如Nginx 等。 不论采用硬件负载均衡还是软件负载均衡， 只要是服务端负载均衡都能以类似下图的架构方式构建起来：

#### how

+ 硬件负载均衡的设备或是软件负载均衡的软件模块都会维护一 个下挂可用的服务端清单，通过***心跳检测***来剔除故障的服务端节点以保证清单中都是可以正常访问的服务端节点。当客户端发送请求到负载均衡设备的时候， 该设备按某种算法（比如线性轮询、 按权重负载、 按流量负载等）从维护的可用服务端清单中取出 一 台服务端的地址， 然后进行转发。

+  同服务端负载均衡的架构类似， 在客户端负载均衡中也需要心跳去维护服务端清单的健康性， 只是这个步骤需要与服务注册中心配合完成。 在Spring Cloud实现的服务治理框架中， 默认会创建针对各个服务治理框架的Ribbon自动化整合配置， 比如Eureka 中的 org.springframework.cloud.netflix.ribbon.eureka. RibbonEurekaAutoConfiguration , Consul中的org.springframework.cloud.consul.discovery. RibbonConsulAuto-Configuration。

  ~~~markdown
  通过Spring Cloud Ribbon的封装， 我们在微服务架构中使用客户端负载均衡调用非常
  简单， 只需要如下两步：
      > 服务提供者只需要启动多个服务实例并注册到一个注册中心或是多个相关联的服务
      注册中心。
      > 服务消费者直接通过调用被 @LoadBalanced 注解修饰过的 RestTemplate 来实现面
      向服务的接口调用。
  这样，我们就可以将服务提供者的高可用以及服务消费者的负载均衡调用一起实现了。
  ~~~

### RestTemplate

#### `loadBalanced`

