package top.sharehome.share_study.common.exception_handler.customize;

import lombok.Data;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;

/**
 * 自定义事物错误异常类
 *
 * @author AntonyCheng
 */
@Data
public class CustomizeTransactionException extends RuntimeException {

    private final String description = "数据库事物处理出现错误，回滚到该异常";

    private final R<String> failure = R.failure(RCodeEnum.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE);

}
