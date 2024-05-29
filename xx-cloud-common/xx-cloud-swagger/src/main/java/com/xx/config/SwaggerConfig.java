package com.xx.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * swagger 接口文档
 *
 * @author Agao
 * @date 2024/2/8 22:56
 */
@EnableKnife4j
@EnableOpenApi
@Configuration
public class SwaggerConfig {
  @Value("${xx-cloud.swagger.enable:false}")
  private boolean enable;

  @Bean
  public Docket createRestApi() {
    return new Docket(DocumentationType.OAS_30)
        .apiInfo(apiInfo())
        // 是否开启
        .enable(enable)
        .select()
        // 扫描的路径包
        .apis(RequestHandlerSelectors.basePackage("com.xx"))
        // 指定路径处理PathSelectors.any()代表所有的路径
        .paths(PathSelectors.any())
        .build()
        .pathMapping("/");
  }

  private ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title("xx-cloud在线接口文档-swagger")
        .description("springboot2.6.14 | swagger3.0")
        // 作者信息
        .contact(new Contact("Agao", "xx-cloud.com", "agaozijing@gmail.com"))
        .version("1.0.0")
        .build();
  }

  /**
   * 解决swagger3 启动报空指针问题 https://developer.aliyun.com/article/950787
   * https://github.com/springfox/springfox/issues/3462#issuecomment-1010721223
   * https://stackoverflow.com/questions/70036953/spring-boot-2-6-0-spring-fox-3-failed-to-start-bean-documentationpluginsboo
   *
   * @return
   */
  @Bean
  public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(
      WebEndpointsSupplier webEndpointsSupplier,
      ServletEndpointsSupplier servletEndpointsSupplier,
      ControllerEndpointsSupplier controllerEndpointsSupplier,
      EndpointMediaTypes endpointMediaTypes,
      CorsEndpointProperties corsProperties,
      WebEndpointProperties webEndpointProperties,
      Environment environment) {
    List<ExposableEndpoint<?>> allEndpoints = new ArrayList();
    Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
    allEndpoints.addAll(webEndpoints);
    allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
    allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
    String basePath = webEndpointProperties.getBasePath();
    EndpointMapping endpointMapping = new EndpointMapping(basePath);
    boolean shouldRegisterLinksMapping =
        this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
    return new WebMvcEndpointHandlerMapping(
        endpointMapping,
        webEndpoints,
        endpointMediaTypes,
        corsProperties.toCorsConfiguration(),
        new EndpointLinksResolver(allEndpoints, basePath),
        shouldRegisterLinksMapping,
        null);
  }

  private boolean shouldRegisterLinksMapping(
      WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
    return webEndpointProperties.getDiscovery().isEnabled()
        && (StringUtils.hasText(basePath)
            || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
  }
}
