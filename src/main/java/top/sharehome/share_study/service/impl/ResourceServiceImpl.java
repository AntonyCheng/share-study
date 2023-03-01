package top.sharehome.share_study.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeFileException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeTransactionException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.mapper.CommentMapper;
import top.sharehome.share_study.mapper.ResourceMapper;
import top.sharehome.share_study.mapper.TeacherMapper;
import top.sharehome.share_study.model.dto.*;
import top.sharehome.share_study.model.entity.Comment;
import top.sharehome.share_study.model.entity.Resource;
import top.sharehome.share_study.model.entity.Teacher;
import top.sharehome.share_study.model.vo.ResourcePageVo;
import top.sharehome.share_study.model.vo.ResourceUpdateVo;
import top.sharehome.share_study.service.ResourceService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 教学资料ServiceImpl
 *
 * @author AntonyCheng
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {
    @javax.annotation.Resource
    private ResourceMapper resourceMapper;

    @javax.annotation.Resource
    private TeacherMapper teacherMapper;

    @javax.annotation.Resource
    private CommentMapper commentMapper;

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void download(HttpServletResponse response) {
        try {
            // 设置下载信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("教学资料信息", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 查询课程分类表所有的数据
            List<Resource> resourceList = resourceMapper.selectList(null);
            EasyExcelFactory.write(response.getOutputStream(), Resource.class)
                    .sheet("教学资料数据")
                    .doWrite(resourceList);
        } catch (UnsupportedEncodingException e) {
            throw new CustomizeFileException(R.failure(RCodeEnum.EXCEL_EXPORT_FAILED), "导出Excel时文件编码异常");
        } catch (IOException e) {
            throw new CustomizeFileException(R.failure(RCodeEnum.EXCEL_EXPORT_FAILED), "文件写入时，响应流发生异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void delete(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非管理员不能进行删除操作");
        }

        LambdaQueryWrapper<Resource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceLambdaQueryWrapper.eq(Resource::getId, id);

        Resource selectResult = resourceMapper.selectOne(resourceLambdaQueryWrapper);
        if (selectResult == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "教学资料不存在，不需要进行下一步操作");
        }

        Teacher targetTeacher = teacherMapper.selectById(selectResult.getBelong());
        if (targetTeacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该教学资料的老师不存在");
        }

        if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId())
                && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE)
                && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员没有权限在此删除其他管理员和超级管理员的教学资料");
        }

        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getResource, id);
        commentMapper.delete(commentLambdaQueryWrapper);

        int deleteResult = resourceMapper.delete(resourceLambdaQueryWrapper);

        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "教学资料数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteBatch(List<Long> ids, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非管理员不能进行删除操作");
        }

        ids.forEach(id -> {
            Resource selectResult = resourceMapper.selectById(id);
            if (selectResult == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "教学资料不存在，不需要进行下一步操作");
            }

            Teacher targetTeacher = teacherMapper.selectById(selectResult.getBelong());
            if (targetTeacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该教学资料的老师不存在");
            }

            if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId())
                    && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE)
                    && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员没有权限在此删除其他管理员和超级管理员的教学资料");
            }
        });

        ids.forEach(id -> {
            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            commentLambdaQueryWrapper.eq(Comment::getResource, id);
            commentMapper.delete(commentLambdaQueryWrapper);
        });

        int deleteResult = resourceMapper.deleteBatchIds(ids);
        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "教学资料数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public ResourceGetDto get(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非管理员不能进行删除操作");
        }

        LambdaQueryWrapper<Resource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceLambdaQueryWrapper.eq(Resource::getId, id);

        Resource selectResult = resourceMapper.selectOne(resourceLambdaQueryWrapper);
        if (selectResult == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "教学资料不存在，不需要进行下一步操作");
        }

        Teacher targetTeacher = teacherMapper.selectById(selectResult.getBelong());
        if (targetTeacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该教学资料的老师不存在");
        }

        if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId())
                && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE)
                && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员没有权限在此回显其他管理员和超级管理员的教学资料");
        }

        ResourceGetDto resourceGetDto = new ResourceGetDto();
        resourceGetDto.setId(selectResult.getId());
        resourceGetDto.setStatus(selectResult.getStatus());

        return resourceGetDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void updateResource(ResourceUpdateVo resourceUpdateVo, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非管理员不能进行删除操作");
        }

        Resource resultFromDatabase = resourceMapper.selectById(resourceUpdateVo.getId());
        if (resultFromDatabase == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "教学资料不存在，不需要进行下一步操作");
        }
        if (Objects.equals(resourceUpdateVo.getStatus(), resultFromDatabase.getStatus())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.THE_UPDATE_DATA_IS_THE_SAME_AS_THE_BACKGROUND_DATA), "更新数据和库中数据相同");
        }

        Teacher targetTeacher = teacherMapper.selectById(resultFromDatabase.getBelong());
        if (targetTeacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该教学资料的老师不存在");
        }

        if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId())
                && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE)
                && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员没有权限在此修改其他管理员和超级管理员的教学资料");
        }

        resultFromDatabase.setStatus(resourceUpdateVo.getStatus());

        int updateResult = resourceMapper.updateById(resultFromDatabase);

        // 判断数据库插入结果
        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改教学资料失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Page<ResourcePageDto> pageResource(Integer current, Integer pageSize, ResourcePageVo resourcePageVo) {
        Page<Resource> page = new Page<>(current, pageSize);
        Page<ResourcePageDto> returnResult = new Page<>(current, pageSize);

        if (resourcePageVo == null) {
            this.page(page);
            BeanUtils.copyProperties(page, returnResult, "records");
            List<ResourcePageDto> pageDtoList = page.getRecords().stream().map(record -> {
                ResourcePageDto resourcePageDto = new ResourcePageDto();
                BeanUtils.copyProperties(record, resourcePageDto);
                LambdaQueryWrapper<Teacher> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
                resourceLambdaQueryWrapper.eq(Teacher::getId, record.getBelong());
                Teacher teacher = teacherMapper.selectOne(resourceLambdaQueryWrapper);
                if (teacher == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "教学资料所属教师不存在");
                }
                resourcePageDto.setBelongName(teacher.getName());
                return resourcePageDto;
            }).collect(Collectors.toList());
            returnResult.setRecords(pageDtoList);
            return returnResult;
        }

        LambdaQueryWrapper<Resource> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!StringUtils.isEmpty(resourcePageVo.getInfo()), Resource::getInfo, resourcePageVo.getInfo())
                .like(!StringUtils.isEmpty(resourcePageVo.getName()), Resource::getName, resourcePageVo.getName())
                .like(!ObjectUtils.isEmpty(resourcePageVo.getStatus()), Resource::getStatus, resourcePageVo.getStatus())
                .orderByAsc(Resource::getCreateTime);
        this.page(page, lambdaQueryWrapper);
        BeanUtils.copyProperties(page, returnResult, "records");

        String belongName = resourcePageVo.getBelongName();
        List<Long> teacherIds = new ArrayList<>();
        if (!StringUtils.isEmpty(belongName)) {
            LambdaQueryWrapper<Teacher> belongNameLambdaQueryWrapper = new LambdaQueryWrapper<>();
            belongNameLambdaQueryWrapper.like(Teacher::getName, belongName);
            List<Teacher> teachers = teacherMapper.selectList(belongNameLambdaQueryWrapper);
            teacherIds = teachers.stream().map(Teacher::getId).collect(Collectors.toList());
        }
        List<Long> finalTeacherIds = teacherIds;

        if (finalTeacherIds.isEmpty()) {
            page.setRecords(new ArrayList<>());
        }

        List<ResourcePageDto> pageDtoList = page.getRecords().stream().map(record -> {
            if (!finalTeacherIds.isEmpty() && !finalTeacherIds.contains(record.getBelong())) {
                return null;
            }
            ResourcePageDto resourcePageDto = new ResourcePageDto();
            BeanUtils.copyProperties(record, resourcePageDto);
            LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teacherLambdaQueryWrapper.eq(Teacher::getId, record.getBelong());
            Teacher teacher = teacherMapper.selectOne(teacherLambdaQueryWrapper);
            if (teacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "教学资料所属教师不存在");
            }
            resourcePageDto.setBelongName(teacher.getName());
            return resourcePageDto;
        }).collect(Collectors.toList());
        pageDtoList.removeIf(Objects::isNull);
        returnResult.setTotal(pageDtoList.size());
        returnResult.setRecords(pageDtoList);
        return returnResult;
    }
}
