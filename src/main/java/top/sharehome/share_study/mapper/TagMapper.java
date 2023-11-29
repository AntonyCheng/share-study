package top.sharehome.share_study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.sharehome.share_study.model.entity.Tag;

/**
 * 资料标签Mapper
 *
 * @author AntonyCheng
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {
}