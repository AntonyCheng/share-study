package top.sharehome.share_study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeTransactionException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.mapper.CollegeMapper;
import top.sharehome.share_study.mapper.ResourceMapper;
import top.sharehome.share_study.mapper.TagMapper;
import top.sharehome.share_study.mapper.TeacherMapper;
import top.sharehome.share_study.model.dto.tag.TagGetDto;
import top.sharehome.share_study.model.dto.tag.TagPageDto;
import top.sharehome.share_study.model.dto.teacher.TeacherLoginDto;
import top.sharehome.share_study.model.entity.College;
import top.sharehome.share_study.model.entity.Resource;
import top.sharehome.share_study.model.entity.Tag;
import top.sharehome.share_study.model.vo.tag.TagAddVo;
import top.sharehome.share_study.model.vo.tag.TagPageVo;
import top.sharehome.share_study.model.vo.tag.TagUpdateVo;
import top.sharehome.share_study.service.TagService;
import top.sharehome.share_study.utils.object.ObjectDataUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 资料标签ServiceImpl
 *
 * @author AntonyCheng
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    @javax.annotation.Resource
    private TagMapper tagMapper;

    @javax.annotation.Resource
    private CollegeMapper collegeMapper;

    @javax.annotation.Resource
    private ResourceMapper resourceMapper;

    @javax.annotation.Resource
    private TeacherMapper teacherMapper;

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void add(TagAddVo tagAddVo) {
        if (ObjectUtils.isEmpty(collegeMapper.selectById(tagAddVo.getBelong()))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS), "该学院不存在");
        }
        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.eq(Tag::getBelong, tagAddVo.getBelong())
                .eq(Tag::getName, tagAddVo.getName());
        Long resultFromDatabase = tagMapper.selectCount(tagLambdaQueryWrapper);
        if (resultFromDatabase != 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_ALREADY_EXISTS), "数据库中已经包含该资料标签：" + tagAddVo.getName());
        }

        Tag tag = new Tag();
        BeanUtils.copyProperties(tagAddVo, tag);

        int insertResult = tagMapper.insert(tag);

        if (insertResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_ADDITION_FAILED), "添加资料标签失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void delete(Long id) {
        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.eq(Tag::getId, id);
        Tag selectResult = tagMapper.selectOne(tagLambdaQueryWrapper);
        if (ObjectUtils.isEmpty(selectResult)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TAG_NOT_EXISTS), "资料标签不存在，不需要进行下一步操作");
        }

        Long tagId = selectResult.getId();
        LambdaQueryWrapper<Resource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceLambdaQueryWrapper.like(Resource::getTags, String.valueOf(tagId));
        Long selectCount = resourceMapper.selectCount(resourceLambdaQueryWrapper);
        if (selectCount != 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TAG_BIND_RESOURCE), "标签下还绑定着教学资源，无法删除");
        }

        int deleteResult = tagMapper.delete(tagLambdaQueryWrapper);

        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "资料标签数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteBatch(List<Long> ids) {
        ids.forEach(id -> {
            Tag selectResult = tagMapper.selectById(id);
            if (ObjectUtils.isEmpty(selectResult)) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TAG_NOT_EXISTS), "资料标签不存在，不需要进行下一步操作");
            }

            Long tagId = selectResult.getId();
            LambdaQueryWrapper<Resource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resourceLambdaQueryWrapper.like(Resource::getTags, String.valueOf(tagId));
            Long selectCount = resourceMapper.selectCount(resourceLambdaQueryWrapper);
            if (selectCount != 0) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TAG_BIND_RESOURCE), "标签下还绑定着教学资源，无法删除");
            }
        });
        this.removeBatchByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public TagGetDto get(Long id) {
        Tag selectResult = tagMapper.selectById(id);
        if (ObjectUtils.isEmpty(selectResult)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TAG_NOT_EXISTS), "资料标签不存在，不需要进行下一步操作");
        }

        LambdaQueryWrapper<College> collegeLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collegeLambdaQueryWrapper.eq(College::getId, selectResult.getBelong());
        College selectCollege = collegeMapper.selectOne(collegeLambdaQueryWrapper);
        if (ObjectUtils.isEmpty(selectCollege)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS), "该学校不存在");
        }
        TagGetDto tagGetDto = new TagGetDto();
        tagGetDto.setId(selectResult.getId());
        tagGetDto.setBelong(selectCollege.getName());
        tagGetDto.setName(selectResult.getName());

        return tagGetDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void updateTag(TagUpdateVo tagUpdateVo) {
        Tag selectResult = tagMapper.selectById(tagUpdateVo.getId());
        if (ObjectUtils.isEmpty(selectResult)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TAG_NOT_EXISTS), "资料标签不存在，不需要进行下一步操作");
        }

        if (selectResult.getName().equals(tagUpdateVo.getName())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_HAS_NOT_CHANGED_BEFORE_THE_MODIFICATION), "数据修改前后未发生变化");
        }

        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.eq(Tag::getId, tagUpdateVo.getId())
                .and(condition -> {
                    condition.eq(Tag::getName, tagUpdateVo.getName());
                });
        Tag checkTag = tagMapper.selectOne(tagLambdaQueryWrapper);
        if (ObjectUtils.isNotEmpty(checkTag)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DUPLICATION_OF_INSTITUTIONS), "资料标签重复");
        }

        Tag tag = new Tag();
        tag.setId(selectResult.getId());
        tag.setName(tagUpdateVo.getName());

        int updateResult = tagMapper.updateById(tag);

        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "资料标签修改失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Page<TagPageDto> pageTag(Integer current, Integer pageSize, HttpServletRequest request, TagPageVo tagPageVo) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，管理员未登录");
        }

        // 创建原始分页数据以及返回分页数据
        Page<Tag> page = new Page<>(current, pageSize);
        Page<TagPageDto> returnResult = new Page<>(current, pageSize);

        LambdaQueryWrapper<Tag> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .orderByDesc(Tag::getCreateTime);

        // 当不存在模糊查询时的分页操作
        if (ObjectDataUtil.isAllObjectDataEmpty(tagPageVo)) {
            this.page(page, lambdaQueryWrapper);
            BeanUtils.copyProperties(page, returnResult, "records");
            List<TagPageDto> pageDtoList = page.getRecords().stream().map(record -> {
                TagPageDto tagPageDto = new TagPageDto();
                College selectCollege = collegeMapper.selectById(record.getBelong());
                BeanUtils.copyProperties(record, tagPageDto, "belong");
                tagPageDto.setBelong(selectCollege.getName());
                return tagPageDto;
            }).collect(Collectors.toList());
            returnResult.setRecords(pageDtoList);
            return returnResult;
        }

        if (ObjectUtils.isNotEmpty(tagPageVo.getBelong()) && ObjectUtils.isEmpty(collegeMapper.selectById(tagPageVo.getBelong()))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS));
        }

        lambdaQueryWrapper
                .eq(ObjectUtils.isNotEmpty(tagPageVo.getBelong()), Tag::getBelong, tagPageVo.getBelong())
                .like(StringUtils.isNotEmpty(tagPageVo.getName()), Tag::getName, tagPageVo.getName());

        this.page(page, lambdaQueryWrapper);
        BeanUtils.copyProperties(page, returnResult, "records");
        List<TagPageDto> pageDtoList = page.getRecords().stream().map(record -> {
            TagPageDto tagPageDto = new TagPageDto();
            BeanUtils.copyProperties(record, tagPageDto);
            tagPageDto.setBelong(collegeMapper.selectById(record.getBelong()).getName());
            return tagPageDto;
        }).collect(Collectors.toList());
        returnResult.setRecords(pageDtoList);
        return returnResult;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public List<TagGetDto> listTag(HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，用户未登录");
        }
        Long belong = teacherMapper.selectById(teacherLoginDto.getId()).getBelong();
        LambdaQueryWrapper<Tag> tagLambdaQueryWrapper = new LambdaQueryWrapper<>();
        tagLambdaQueryWrapper.eq(Tag::getBelong,belong);

        return tagMapper.selectList(tagLambdaQueryWrapper).stream().map(tag -> {
            TagGetDto tagGetDto = new TagGetDto();
            tagGetDto.setId(tag.getId());
            tagGetDto.setName(tag.getName());
            tagGetDto.setBelong(collegeMapper.selectById(tag.getBelong()).getName());
            return tagGetDto;
        }).collect(Collectors.toList());
    }
}
