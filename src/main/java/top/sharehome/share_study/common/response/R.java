package top.sharehome.share_study.common.response;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一返回值
 *
 * @param <T> 返回类型
 * @author AntonyCheng
 */
@Data
@ApiModel(value = "全局统一返回结果")
public class R<T> implements Serializable {
    /**
     * 响应码
     */
    private String code;

    /**
     * 响应体，要求该响应体数据可被序列化
     */
    private T data;

    /**
     * 响应信息
     */
    private String message;

    /**
     * 动态数据
     */
    private Map<String, Object> map = new HashMap<>();

    /**
     * 允许添加动态数据，请在返回前将添加的数据处理好（格式序列化等）
     */
    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    /**
     * 响应成功返回的响应体
     *
     * @param data 响应体
     * @param <T>  响应体类型
     * @return 统一返回类型
     */
    public static <T> R<T> success(T data) {
        R<T> r = new R<>();
        r.setCode(RCodeEnum.SUCCESS.getCode());
        r.setData(data);
        r.setMessage(RCodeEnum.SUCCESS.getMessage());
        return r;
    }

    /**
     * 相应成功返回的对应响应信息
     *
     * @param message 响应信息
     * @return 统一返回类型
     */
    public static R<String> success(String message) {
        R<String> r = new R<>();
        r.setCode(RCodeEnum.SUCCESS.getCode());
        r.setData(null);
        r.setMessage(message);
        return r;
    }

    /**
     * 响应成功返回的响应体和对应响应信息
     *
     * @param data    响应体
     * @param message 响应信息
     * @param <T>     响应体类型
     * @return 统一返回类型
     */
    public static <T> R<T> success(T data, String message) {
        R<T> r = new R<>();
        r.setCode(RCodeEnum.SUCCESS.getCode());
        r.setData(data);
        r.setMessage(message);
        return r;
    }

    /**
     * 相应失败返回的响应码和响应信息（推荐，按照规定枚举返回信息）
     *
     * @param codeEnum 响应枚举
     * @param <T>      响应体类型
     * @return 统一返回类型
     */
    public static <T> R<T> failure(RCodeEnum codeEnum) {
        R<T> r = new R<>();
        r.setCode(codeEnum.getCode());
        r.setData(null);
        r.setMessage(codeEnum.getMessage());
        return r;
    }

    /**
     * 相应失败返回的响应码和响应信息（不推荐，自定义返回信息，不便于统一管理）
     *
     * @param code    自定义响应码
     * @param message 自定义响应信息
     * @param <T>     响应体类型
     * @return 统一返回类型
     */
    public static <T> R<T> failure(String code, String message) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(null);
        r.setMessage(message);
        return r;
    }
}
