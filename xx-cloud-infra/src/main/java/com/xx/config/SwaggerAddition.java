package com.xx.config;

import com.google.common.collect.Sets;
import java.util.List;
import org.assertj.core.util.Lists;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;

/**
 * 配置额外的接口 eg：security登录接口
 *
 * @author Agao
 * @date 2024/2/15 17:31
 */
@Component
public class SwaggerAddition implements ApiListingScannerPlugin {
  @Override
  public List<ApiDescription> apply(DocumentationContext context) {
    // 登录接口
    // 1.定义参数
    RequestParameter username =
        new RequestParameterBuilder().name("username").description("邮箱/用户名").required(true).build();
    RequestParameter password =
        new RequestParameterBuilder().name("password").description("密码").required(true).build();
    // 2.接口的每种请求方式(GET/POST...)为一个 Operation
    Operation loginOperation =
        new OperationBuilder(new CachingOperationNameGenerator())
            .method(HttpMethod.POST)
            .summary("登录")
            .tags(Sets.newHashSet("登录认证接口"))
            .responses(Sets.newHashSet(new ResponseBuilder().code("200").description("OK").build()))
            .consumes(Sets.newHashSet(MediaType.MULTIPART_FORM_DATA_VALUE))
            .produces(Sets.newHashSet(MediaType.APPLICATION_JSON_VALUE))
            .requestParameters(Lists.newArrayList(username, password))
            .build();
    // 3.每个接口路径对应一个 ApiDescription
    ApiDescription loginApi =
        new ApiDescription(
            null, "/api/login", null, "登录", Lists.newArrayList(loginOperation), false);

    // 登出接口
    Operation logoutOperation =
        new OperationBuilder(new CachingOperationNameGenerator())
            .method(HttpMethod.GET)
            .summary("登出")
            .notes("退出登录")
            .tags(Sets.newHashSet("登录认证接口"))
            .responses(Sets.newHashSet(new ResponseBuilder().code("200").description("OK").build()))
            .build();
    ApiDescription logoutApi =
        new ApiDescription(
            null, "/api/logout", null, "注销", Lists.newArrayList(logoutOperation), false);

    context.getTags().add(new Tag("登录认证接口", "登录、登出、验证码..."));

    return Lists.newArrayList(loginApi, logoutApi);
  }

  @Override
  public boolean supports(DocumentationType documentationType) {
    return DocumentationType.OAS_30.equals(documentationType);
  }
}
