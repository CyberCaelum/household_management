package org.cybercaelum.household_management.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author CyberCaelum
 * @version 1.0
 * @description: 注册web层相关组件
 * @date 2025/10/18 下午4:27
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    /**
     * @description 用户接口分组
     * @author CyberCaelum
     * @date 下午5:12 2025/10/18
     * @return org.springdoc.core.models.GroupedOpenApi
     **/
    @Bean
    public GroupedOpenApi userApi(){
        return GroupedOpenApi.builder()
                .group("用户接口")
                //扫描指定的文件夹
                .packagesToScan("org.sybercaelum.household_management.controller.user")
                .build();
    }
    /**
     * @description API信息配置
     * @author CyberCaelum
     * @date 下午5:29 2025/10/18
     * @return io.swagger.v3.oas.models.OpenAPI
     **/
    @Bean
    public OpenAPI skyTakeoutOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("家政")
                        .version("1.0")
                        .description("家政接口文档"));
    }
}
