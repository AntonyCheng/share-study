package top.sharehome.share_study.common.exception_handler.customize;

import lombok.Data;
import top.sharehome.share_study.common.response.R;

/**
 * 自定义文件错误异常类
 *
 * @author AntonyCheng
 */
@Data
public class CustomizeFileException extends RuntimeException {
    private String description;
    private R failure;

    public <T> CustomizeFileException(R<T> failure) {
        this.failure = failure;
        this.description = "";
    }

    public <T> CustomizeFileException(R<T> failure, String description) {
        this.failure = failure;
        this.description = description;
    }
}
