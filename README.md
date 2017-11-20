API 网关项目
=======

本项目是采用了Spring Boot及Spring Cloud Zuul技术。

## 1. 功能说明

本项目实现了API网关的功能，包括：权限、后台，没有采用注册中心，只是一个简单的服务反向代理。

		架构说明：
		
		客户端  -->  API网关  -->  应用服务
		
## 2. 权限配置

### 2.1. Shiro配置

Shiro配置文件：META-INF/spring-auth/spring-shiro.xml。

		Shiro配置filters说明：
		accctl   : 用于页面登录认证，采用Cookie形式。
		accip    : 用于服务访问认证，允许IP访问，配置文件：META-INF/config/access-ip.properties。
		acczuul  : 用于ZUUL服务访问认证，认证方法，HttpHelper.validateIpMd5(ip, request.getHeader("zuul"))。
		accauth  : 用于用户名密码认证，配置文件：META-INF/config/authorization.properties，auth.simpleauth.enable必须为true，如果为false需要使用到权限系统，或配置远程调用服务，详见：META-INF/spring-rpc-consumer/rpc-consumer-systemauth.xml。
		traceid  : 用于添加TRACE ID，跟踪调用过程。

### 2.2. 认证授权配置

项目不带有认证授权系统，认证授权可以通过三种方式实现：

1. 配置META-INF/config/authorization.properties，设置auth.simpleauth.enable=true。
2. 当auth.simpleauth.enable=false，实现Spring Bean为defaultSystemApplication，接口为com.foreveross.common.application.SystemApplication，实现Spring Bean为defaultAuthorizationApplication，接口为com.foreveross.common.application.AuthorizationApplication，自行加载权限数据。
3. 当auth.simpleauth.enable=false，开启HTTP-RPC调用，META-INF/spring-rpc-consumer/rpc-consumer-systemauth.xml，调用远程的权限实现。

## 3. 运行

1. 项目编译：mvn clean package
2. 项目运行：java -jar foss-qdp-api-gateway-project-1.0.0.war
3. 个性配置：java -jar -Dspring.config.location=application.properties foss-qdp-api-gateway-project-1.0.0.war

应用配置文件：config/application.properties。

