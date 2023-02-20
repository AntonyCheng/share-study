package top.sharehome.share_study.common.exception_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeFileException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeTransactionException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;

/**
 * 全局异常处理器
 *
 * @author AntonyCheng
 */
@ResponseBody
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String UNKNOWN_EXCEPTION_MESSAGE = "系统出现未知错误，全局异常拦截器已拦截";

    /**
     * 处理全局异常
     *
     * @param exception 全局异常
     * @return 返回处理结果
     */
    @ExceptionHandler(Exception.class)
    public R globalExceptionHandler(Exception exception) {
        exception.printStackTrace();
        log.warn("GlobalExceptionHandler:{},Description:{}", exception.getClass(), UNKNOWN_EXCEPTION_MESSAGE);
        return R.failure(RCodeEnum.SYSTEM_UNKNOWN_EXCEPTION);
    }

    /**
     * 处理自定义返回异常
     *
     * @param exception 自定义返回异常
     * @return 返回处理结果
     */
    @ExceptionHandler(value = {CustomizeReturnException.class})
    public R returnExceptionHandler(CustomizeReturnException exception) {
        exception.printStackTrace();
        log.warn("CustomizeReturnException:{},Description:{}", CustomizeReturnException.class, exception.getDescription());
        return exception.getFailure();
    }

    /**
     * 处理自定义事物异常
     *
     * @param exception 自定义事物异常
     * @return 返回处理结果
     */
    @ExceptionHandler(value = {CustomizeTransactionException.class})
    public R<String> returnTransactionExceptionHandler(CustomizeTransactionException exception) {
        exception.printStackTrace();
        log.warn("CustomizeTransactionException:{},Description:{}", CustomizeReturnException.class, exception.getDescription());
        return exception.getFailure();
    }

    /**
     * 处理自定义文件异常
     *
     * @param exception 自定义文件异常
     * @return 返回处理结果
     */
    @ExceptionHandler(value = {CustomizeFileException.class})
    public R returnFileExceptionHandler(CustomizeFileException exception) {
        exception.printStackTrace();
        log.warn("CustomizeFileException:{},Description:{}", CustomizeReturnException.class, exception.getDescription());
        return exception.getFailure();
    }
}
