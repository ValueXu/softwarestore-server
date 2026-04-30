package cn.jxufe.valuexu.softwarestoreserver.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public OpenAPI productApi() {
        return new OpenAPI()
                .info(buildInfo());
    }

    private Info buildInfo() {
        return new Info()
                .title("软件下载平台")
                .description("基于关联规则挖掘的软件下载平台")
                .version("1.0");
    }
}
