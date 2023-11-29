package top.sharehome.share_study.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.sharehome.share_study.model.dto.tag.TagGetDto;
import top.sharehome.share_study.model.dto.tag.TagPageDto;
import top.sharehome.share_study.model.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;
import top.sharehome.share_study.model.vo.tag.TagAddVo;
import top.sharehome.share_study.model.vo.tag.TagPageVo;
import top.sharehome.share_study.model.vo.tag.TagUpdateVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 资料标签Service
 *
 * @author AntonyCheng
 */
public interface TagService extends IService<Tag> {

    /**
     * 添加资料标签
     *
     * @param tagAddVo 添加资料标签Vo
     */
    void add(TagAddVo tagAddVo);

    /**
     * 删除资料标签
     *
     * @param id 资料标签ID
     */
    void delete(Long id);

    /**
     * 批量删除资料标签
     *
     * @param ids 资料标签ID列表
     */
    void deleteBatch(List<Long> ids);

    /**
     * 获取单个资料标签接口，用于修改信息时的回显（s）
     *
     * @param id 资料标签ID
     * @return 返回回显结果
     */
    TagGetDto get(Long id);

    /**
     * 修改资料标签接口（s）
     *
     * @param tagUpdateVo 资料标签修改Vo对象
     */
    void updateTag(TagUpdateVo tagUpdateVo);

    /**
     * 资料标签分页查询接口（可以做到模糊查询）（s）
     *
     * @param current   当前页
     * @param pageSize  页面条数
     * @param request   获取session对象
     * @param tagPageVo 资料标签分页Vo对象
     * @return 返回分页结果
     */
    Page<TagPageDto> pageTag(Integer current, Integer pageSize, HttpServletRequest request, TagPageVo tagPageVo);

    /**
     * 资料标签ID和对应名称的List（s/a/u）
     *
     * @param request 获取session对象
     * @return 资料标签List
     */
    List<TagGetDto> listTag(HttpServletRequest request);
}
