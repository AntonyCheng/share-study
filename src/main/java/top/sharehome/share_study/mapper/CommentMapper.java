package top.sharehome.share_study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.sharehome.share_study.model.entity.Comment;

/**
 * 评论交流Mapper
 *
 * @author AntonyCheng
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}