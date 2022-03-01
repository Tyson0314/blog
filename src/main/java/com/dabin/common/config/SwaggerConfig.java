package com.dabin.common.config;


import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

/**
 * @className SwaggerConfig
 * @description swagger相关配置
 * @author 大彬
 * @date 2021-11-17 22:35
 **/

@EnableSwagger2
@Configuration
public class SwaggerConfig {

        @Bean
        public Docket createRestApi() {
            ParameterBuilder tokenPar = new ParameterBuilder();
            List<springfox.documentation.service.Parameter> pars = new ArrayList<>();
            tokenPar.name("token").description("令牌")
                    .modelRef(new ModelRef("string")).parameterType("query").required(false).build();
            pars.add(tokenPar.build());
            return new Docket(DocumentationType.SWAGGER_2)
                    .apiInfo(apiInfo())
                    .select()
                    .apis(RequestHandlerSelectors.basePackage("com.dabin"))
                    .paths(PathSelectors.any())
                    .build().globalOperationParameters(globalParameters())  ;
        }

    private List<Parameter> globalParameters() {
        ParameterBuilder builder = new ParameterBuilder()
                .description("用户凭证")
                .name("Authentication")
                .modelRef(new ModelRef("string"))
                .parameterType("header");
        return Lists.newArrayList(builder.build());
    }

        @SuppressWarnings("deprecation")
        private ApiInfo apiInfo() {
            return new ApiInfoBuilder()
                    .title("大彬的博客")
                    .description("日常记录")
                    .version("1.0")
                    .build();
        }

    }
