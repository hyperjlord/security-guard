package com.example.protect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
@EnableEurekaClient
public class protectProvideApplication {

	public static void main(String[] args) {
		SpringApplication.run(protectProvideApplication.class, args);

	}
//	@Bean
//	public CorsFilter corsFilter() {
//
//
//		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//
//
//		CorsConfiguration corsConfiguration = new CorsConfiguration();
//
//
//		corsConfiguration.addAllowedOrigin("*");
//
//
//		corsConfiguration.addAllowedHeader("*");
//
//
//		corsConfiguration.addAllowedMethod("*");
//
//
//		source.registerCorsConfiguration("/**", corsConfiguration);
//
//
//		return new CorsFilter(source);
//
//
//	}

	//加入以下类可以解决跨域
//	@Configuration
//	public class MyMvcConfig {
//
//		@Bean
//		public WebMvcConfigurer webMvcConfigurer() {
//			return new WebMvcConfigurer() {
//
//				//配置跨域
//				@Override
//				public void addCorsMappings(CorsRegistry registry) {
//					registry.addMapping("/**")     //允许的路径
//							.allowedMethods("*")     //允许的方法
//							.allowedOrigins("*")       //允许的网站
//							.allowedHeaders("*")     //允许的请求头
//							.allowCredentials(true)
//							.maxAge(3600);
//				}
//			};
//
//		}
//
//	}
}
