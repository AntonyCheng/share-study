package top.sharehome.share_study;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;

import java.util.TimeZone;

/**
 * ShareStudyApplication启动类
 *
 * @author AntonyCheng
 */
@SpringBootApplication
@MapperScan("top.sharehome.share_study.mapper")
@Slf4j
@ComponentScan(basePackages = "top.sharehome")
@EnableTransactionManagement
@ServletComponentScan
@EnableOpenApi
@EnableCaching
public class ShareStudyApplication {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(ShareStudyApplication.class, args);
    }
}
