package top.sharehome.share_study.common.meta_handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动字段填充器
 *
 * @author AntonyCheng
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 需要处理的字段名——updateTime
     */
    public static final String UPDATE_TIME = "updateTime";

    /**
     * 需要处理的字段名——createTime
     */
    public static final String CREATE_TIME = "createTime";

    /**
     * 需要处理的字段名——isDeleted
     */
    public static final String IS_DELETED = "isDeleted";

    /**
     * 需要处理的字段名——read
     */
    public static final String READ = "read";

    /**
     * 需要处理的字段名——status
     */
    public static final String STATUS = "status";

    /**
     * 需要处理的字段名——role
     */
    public static final String ROLE = "role";

    /**
     * 需要处理的字段名——score
     */
    public static final String SCORE = "score";

    /**
     * 需要处理的字段名——messageTotal
     */
    public static final String MESSAGE_TOTAL = "messageTotal";

    /**
     * 需要处理的字段名——messageRead
     */
    public static final String MESSAGE_READ = "messageRead";

    /**
     * 插入时自动填充的字段
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject.hasSetter(UPDATE_TIME)) {
            metaObject.setValue(UPDATE_TIME, LocalDateTime.now());
        }

        if (metaObject.hasSetter(CREATE_TIME)) {
            metaObject.setValue(CREATE_TIME, LocalDateTime.now());
        }

        if (metaObject.hasSetter(IS_DELETED)) {
            metaObject.setValue(IS_DELETED, 0);
        }

        if (metaObject.hasSetter(READ)) {
            metaObject.setValue(READ, 0);
        }

        if (metaObject.hasSetter(STATUS)) {
            metaObject.setValue(STATUS, 0);
        }

        if (metaObject.hasSetter(ROLE)) {
            metaObject.setValue(ROLE, 0);
        }

        if (metaObject.hasSetter(MESSAGE_TOTAL)) {
            metaObject.setValue(MESSAGE_TOTAL, 0L);
        }

        if (metaObject.hasSetter(MESSAGE_READ)) {
            metaObject.setValue(MESSAGE_READ, 0L);
        }

        if (metaObject.hasSetter(SCORE)) {
            metaObject.setValue(SCORE, 0L);
        }
    }

    /**
     * 更新时自动填充的字段
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject.hasSetter(UPDATE_TIME)) {
            metaObject.setValue(UPDATE_TIME, LocalDateTime.now());
        }
    }
}
