package top.sharehome.share_study.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.sharehome.share_study.common.jackson_mapper.JacksonObjectMapper;

import java.util.List;

/**
 * web mvc 配置类
 *
 * @author AntonyCheng
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 添加消息转换器，
     *
     * @param converters 转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        // 设置对象转换器，底层使用Jackson将Java对象转为Json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        // 将上面的消息转换器对象追加到MVC框架的转换器集合中，所以尽量靠前，有限使用我们自己的转换器
        converters.add(0, messageConverter);
    }
}
