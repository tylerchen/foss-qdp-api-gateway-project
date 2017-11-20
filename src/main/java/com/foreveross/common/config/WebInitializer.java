///*******************************************************************************
// * Copyright (c) Oct 22, 2017 @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a>.
// * All rights reserved.
// *
// * Contributors:
// *     <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> - initial API and implementation
// ******************************************************************************/
//package com.foreveross.common.boot;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Properties;
//
//import javax.servlet.FilterRegistration;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRegistration;
//import javax.servlet.ServletRegistration.Dynamic;
//import javax.servlet.annotation.HandlesTypes;
//
//import org.apache.cxf.Bus;
//import org.apache.cxf.bus.spring.SpringBus;
//import org.apache.cxf.endpoint.Server;
//import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
//import org.apache.cxf.transport.servlet.CXFServlet;
//import org.iff.infra.util.MapHelper;
//import org.iff.infra.util.freemarker.ExFreeMarkerConfiguration;
//import org.iff.infra.util.freemarker.SpringFreeMarkerConfigurer;
//import org.springframework.boot.Banner.Mode;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.freemarker.FreeMarkerAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
//import org.springframework.boot.builder.SpringApplicationBuilder;
//import org.springframework.boot.web.servlet.ServletRegistrationBean;
//import org.springframework.boot.web.support.SpringBootServletInitializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.context.annotation.ImportResource;
//import org.springframework.context.annotation.PropertySource;
//import org.springframework.context.annotation.PropertySources;
//import org.springframework.core.annotation.Order;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.web.WebApplicationInitializer;
//import org.springframework.web.context.ContextLoaderListener;
//import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
//import org.springframework.web.context.support.XmlWebApplicationContext;
//import org.springframework.web.filter.CharacterEncodingFilter;
//import org.springframework.web.filter.DelegatingFilterProxy;
//import org.springframework.web.multipart.MultipartResolver;
//import org.springframework.web.multipart.commons.CommonsMultipartResolver;
//import org.springframework.web.servlet.DispatcherServlet;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
//import org.springframework.web.servlet.view.InternalResourceViewResolver;
//import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
//
//import com.alibaba.druid.support.http.StatViewServlet;
//import com.foreveross.common.util.SimpleMappingExceptionResolver;
//import com.foreveross.common.web.SpringFreeMarkerView;
//
////1）WebAppInitializer <---> web.xml(部署描述符文件)（配置 org.springframework.web.servlet.DispatcherServlet（指定dispatcherServletContext.xml）,
////配置CharacterEncodingFilter等filter,
////配置应用范围内的初始化参数context-param，指定applicationContext.xml和applicationContext-shiro.xml的位置）
////2）AppConfig.class <---> applicationContext.xml（配置dataSource，redis，jdbcTemplate等）Appconfig中不能写@Value
////3）WebConfig.class <----> spring的配置文件dispatherServletContent.xml（配置component-scan，interceptor，内容协商，视图解析等configureContentNegotiation、contentNegotiatingViewResolver）
////4）mvc:resources -- addResourceHandlers //静态资源解析(重写方法)
////mvc:view-controller -- addViewControllers// 解析视图(重写方法)
////CommonsMultipartResolver //文件上传表单的视图解析器
///**
// * https://docs.spring.io/spring-boot/docs/current/reference/html/howto-traditional-deployment.html#howto-convert-an-existing-application-to-spring-boot
// * @author <a href="mailto:iffiff1@gmail.com">Tyler Chen</a> 
// * @since Oct 22, 2017
// */
//@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class,
//		DataSourceTransactionManagerAutoConfiguration.class, FreeMarkerAutoConfiguration.class })
//@Import({ AppConfig.class, MvcConfig.class })
//@Order(1) //第一启动顺序
//@HandlesTypes(WebApplicationInitializer.class) //可能是标识这个是一个web上下文初始化的类（在springboot中该类需要在WebMvcConfigurerAdapter（可自定义继承该类）中加载调用）
//public class BootApplication extends SpringBootServletInitializer implements WebApplicationInitializer {
//
//	public static void main(String[] args) throws Exception {
//		SpringApplication.run(BootApplication.class, args);
//	}
//
//	@Override
//	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
//		// Customize the application or call application.sources(...) to add sources
//		// Since our example is itself a @Configuration class (via @SpringBootApplication)
//		// we actually don't need to override this method.
//		return application.sources(BootApplication.class).bannerMode(Mode.OFF);
//	}
//
//	//	@Bean
//	//	public ServletRegistrationBean statViewServlet() {
//	//		ServletRegistrationBean reg = new ServletRegistrationBean();
//	//		reg.setServlet(new StatViewServlet());
//	//		reg.addUrlMappings("/druid/*");
//	//		return reg;
//	//	}
//
//	@Override
//	public void onStartup(ServletContext servletContext) throws ServletException {
//		XmlWebApplicationContext appContext = new XmlWebApplicationContext();
//		{
//			appContext.setConfigLocation("classpath:META-INF/spring/root.xml");
//			// Manage the lifecycle of the root application context
//			servletContext.addListener(new ContextLoaderListener(appContext));
//		}
//		{
//			FilterRegistration.Dynamic filter = servletContext.addFilter("encodingFilter",
//					new CharacterEncodingFilter("UTF-8"));
//			filter.addMappingForUrlPatterns(null, false, "/*");
//		}
//		{
//			DelegatingFilterProxy shiroFilter = new DelegatingFilterProxy();
//			shiroFilter.setTargetFilterLifecycle(true);
//			FilterRegistration.Dynamic filter = servletContext.addFilter("shiroFilter", shiroFilter);
//			filter.addMappingForUrlPatterns(null, false, "/*");
//		}
//		XmlWebApplicationContext webContext = new XmlWebApplicationContext();
//		{
//			webContext.setConfigLocation("classpath:META-INF/spring-web/spring-servlet.xml");
//		}
//		{
//			ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher",
//					new DispatcherServlet(webContext));
//			dispatcher.addMapping("/*");
//			dispatcher.setLoadOnStartup(1);
//		}
//	}
//
//	//	@Bean
//	//	public CharacterEncodingFilter characterEncodingFilter() {
//	//		CharacterEncodingFilter filter = new CharacterEncodingFilter();
//	//		filter.setEncoding("UTF-8");
//	//		filter.setForceEncoding(true);
//	//		return filter;
//	//	}
//	//
//	//	@Bean
//	//	public DelegatingFilterProxy shiroFilter() {
//	//		DelegatingFilterProxy filter = new DelegatingFilterProxy();
//	//		filter.setTargetFilterLifecycle(true);
//	//		return filter;
//	//	}
//	//
//	//	@Bean
//	//	public DispatcherServlet dispatcherServlet() {
//	//		DispatcherServlet servlet = new DispatcherServlet();
//	//		servlet.setDispatchOptionsRequest(true);
//	//		return servlet;
//	//	}
//	//
//	//	@Bean
//	//	public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
//	//		ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);
//	//		registration.addUrlMappings("/*");
//	//		registration.setLoadOnStartup(1);
//	//		return registration;
//	//	}
//
//	@Bean
//	public ServletRegistrationBean cxfServlet() {
//		return new ServletRegistrationBean(new CXFServlet(), "/webservice/*");
//	}
//
//	@Bean(name = Bus.DEFAULT_BUS_ID)
//	public SpringBus springBus() {
//		return new SpringBus();
//	}
//}
