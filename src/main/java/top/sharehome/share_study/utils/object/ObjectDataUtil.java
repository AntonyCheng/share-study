package top.sharehome.share_study.utils.object;

import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * 对象操作工具类
 *
 * @author AntonyCheng
 */
public class ObjectDataUtil {
    public static boolean isAllObjectDataEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            field.setAccessible(true);
            Object fieldValue = null;
            try {
                fieldValue = field.get(obj);
            } catch (IllegalAccessException e) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.PARAMETER_FORMAT_MISMATCH));
            }
            if (fieldValue == null) {
                continue;
            }
            if (fieldValue instanceof CharSequence && ((CharSequence) fieldValue).length() == 0) {
                continue;
            }
            if (fieldValue instanceof Collection && ((Collection<?>) fieldValue).isEmpty()) {
                continue;
            }
            if (fieldValue.getClass().isArray() && Array.getLength(fieldValue) == 0) {
                continue;
            }
            if (fieldValue instanceof Map<?, ?> && ((Map<?, ?>) fieldValue).isEmpty()) {
                continue;
            }
            return false;
        }
        return true;
    }
}
