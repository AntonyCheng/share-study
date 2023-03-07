package top.sharehome.share_study.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import top.sharehome.share_study.mapper.CollegeMapper;
import top.sharehome.share_study.mapper.CommentMapper;
import top.sharehome.share_study.mapper.ResourceMapper;
import top.sharehome.share_study.mapper.TeacherMapper;
import top.sharehome.share_study.model.dto.*;
import top.sharehome.share_study.model.entity.College;
import top.sharehome.share_study.model.entity.Comment;
import top.sharehome.share_study.model.entity.Resource;
import top.sharehome.share_study.model.entity.Teacher;
import top.sharehome.share_study.model.vo.CommentPageVo;
import top.sharehome.share_study.model.vo.CommentUpdateVo;
import top.sharehome.share_study.service.CommentService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 评论交流ServiceImpl
 *
 * @author AntonyCheng
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    @javax.annotation.Resource
    private CommentMapper commentMapper;

    @javax.annotation.Resource
    private TeacherMapper teacherMapper;

    @javax.annotation.Resource
    private ResourceMapper resourceMapper;

    @javax.annotation.Resource
    private CollegeMapper collegeMapper;

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void download(HttpServletResponse response) {
        try {
            // 设置下载信息
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
            String fileName = URLEncoder.encode("评论交流数据", "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 查询评论交流分类表所有的数据
            List<Comment> resourceList = commentMapper.selectList(null);
            EasyExcelFactory.write(response.getOutputStream(), Comment.class)
                    .sheet("评论交流数据")
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

        LambdaQueryWrapper<Comment> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceLambdaQueryWrapper.eq(Comment::getId, id);

        Comment selectResult = commentMapper.selectOne(resourceLambdaQueryWrapper);
        if (selectResult == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.COMMENT_NOT_EXISTS), "交流评论不存在，不需要进行下一步操作");
        }

        Teacher targetTeacher = teacherMapper.selectById(selectResult.getBelong());
        if (targetTeacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该交流评论的老师不存在");
        }

        Resource targetResource = resourceMapper.selectById(selectResult.getResource());
        if (targetResource == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "该交流评论所在的教学资料不存在");
        }

        if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId())
                && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE)
                && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员没有权限在此删除其他管理员和超级管理员的交流评论");
        }

        int deleteResult = commentMapper.delete(resourceLambdaQueryWrapper);

        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "交流评论数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
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
            Comment selectResult = commentMapper.selectById(id);
            if (selectResult == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "交流评论不存在，不需要进行下一步操作");
            }

            Teacher targetTeacher = teacherMapper.selectById(selectResult.getBelong());
            if (targetTeacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该交流评论的老师不存在");
            }

            Resource targetResource = resourceMapper.selectById(selectResult.getResource());
            if (targetResource == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "该交流评论所在的教学资料不存在");
            }

            if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId())
                    && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE)
                    && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员没有权限在此删除其他管理员和超级管理员的交流评论");
            }
        });

        int deleteResult = resourceMapper.deleteBatchIds(ids);
        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "交流评论数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public CommentGetDto get(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非管理员不能进行删除操作");
        }

        LambdaQueryWrapper<Comment> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceLambdaQueryWrapper.eq(Comment::getId, id);

        Comment selectResult = commentMapper.selectOne(resourceLambdaQueryWrapper);
        if (selectResult == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "评论交流不存在，不需要进行下一步操作");
        }

        Teacher targetTeacher = teacherMapper.selectById(selectResult.getBelong());
        if (targetTeacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该教学资料的老师不存在");
        }

        Resource targetResource = resourceMapper.selectById(selectResult.getResource());
        if (targetResource == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "该评论交流所属教学资料不存在");
        }

        if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId())
                && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE)
                && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员没有权限在此回显其他管理员和超级管理员的教学资料");
        }

        CommentGetDto commentGetDto = new CommentGetDto();
        commentGetDto.setId(selectResult.getId());
        commentGetDto.setStatus(selectResult.getStatus());

        return commentGetDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void updateComment(CommentUpdateVo commentUpdateVo, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非管理员不能进行删除操作");
        }

        Comment resultFromDatabase = commentMapper.selectById(commentUpdateVo.getId());
        if (resultFromDatabase == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "交流评论不存在，不需要进行下一步操作");
        }
        if (Objects.equals(commentUpdateVo.getStatus(), resultFromDatabase.getStatus())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.THE_UPDATE_DATA_IS_THE_SAME_AS_THE_BACKGROUND_DATA), "更新数据和库中数据相同");
        }

        Teacher targetTeacher = teacherMapper.selectById(resultFromDatabase.getBelong());
        if (targetTeacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该交流评论的老师不存在");
        }

        Resource targetResource = resourceMapper.selectById(resultFromDatabase.getResource());
        if (targetResource == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "交流评论所在的教学资料不存在");
        }

        if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId())
                && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE)
                && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员没有权限在此修改其他管理员和超级管理员的交流评论");
        }

        resultFromDatabase.setStatus(commentUpdateVo.getStatus());

        int updateResult = commentMapper.updateById(resultFromDatabase);

        // 判断数据库插入结果
        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改交流评论失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Page<CommentPageDto> pageComment(Integer current, Integer pageSize, CommentPageVo commentPageVo) {
        Page<Comment> page = new Page<>(current, pageSize);
        Page<CommentPageDto> returnResult = new Page<>(current, pageSize);

        if (commentPageVo == null) {
            this.page(page);
            BeanUtils.copyProperties(page, returnResult, "records");
            List<CommentPageDto> pageDtoList = page.getRecords().stream().map(comment -> {
                CommentPageDto commentPageDto = new CommentPageDto();
                BeanUtils.copyProperties(comment, commentPageDto);

                LambdaQueryWrapper<Teacher> belongLambdaQueryWrapper = new LambdaQueryWrapper<>();
                belongLambdaQueryWrapper.eq(Teacher::getId, comment.getBelong());
                Teacher belong = teacherMapper.selectOne(belongLambdaQueryWrapper);
                if (belong == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发送者不存在");
                }
                commentPageDto.setBelongName(belong.getName());

                LambdaQueryWrapper<Teacher> sendLambdaQueryWrapper = new LambdaQueryWrapper<>();
                sendLambdaQueryWrapper.eq(Teacher::getId, comment.getSend());
                Teacher send = teacherMapper.selectOne(sendLambdaQueryWrapper);
                if (send == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "接收者不存在");
                }
                commentPageDto.setSendName(send.getName());

                LambdaQueryWrapper<Resource> resourceQueryWrapper = new LambdaQueryWrapper<>();
                resourceQueryWrapper.eq(Resource::getId, comment.getResource());
                Resource resource = resourceMapper.selectOne(resourceQueryWrapper);
                if (resource == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "教学资料不存在");
                }
                commentPageDto.setResourceName(resource.getName());

                return commentPageDto;
            }).collect(Collectors.toList());
            returnResult.setRecords(pageDtoList);
            return returnResult;
        }

        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .like(!StringUtils.isEmpty(commentPageVo.getContent()), Comment::getContent, commentPageVo.getContent())
                .like(!ObjectUtils.isEmpty(commentPageVo.getReadStatus()), Comment::getReadStatus, commentPageVo.getReadStatus())
                .like(!ObjectUtils.isEmpty(commentPageVo.getStatus()), Comment::getStatus, commentPageVo.getStatus())
                .orderByAsc(Comment::getCreateTime);
        this.page(page, lambdaQueryWrapper);
        BeanUtils.copyProperties(page, returnResult, "records");

        String belongName = commentPageVo.getBelongName();
        List<Long> belongIds = null;
        if (!StringUtils.isEmpty(belongName)) {
            LambdaQueryWrapper<Teacher> belongNameLambdaQueryWrapper = new LambdaQueryWrapper<>();
            belongNameLambdaQueryWrapper.like(Teacher::getName, belongName);
            List<Teacher> teachers = teacherMapper.selectList(belongNameLambdaQueryWrapper);
            belongIds = teachers.stream().map(Teacher::getId).collect(Collectors.toList());
        }
        String sendName = commentPageVo.getSendName();
        List<Long> sendIds = null;
        if (!StringUtils.isEmpty(sendName)) {
            LambdaQueryWrapper<Teacher> sendNameLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sendNameLambdaQueryWrapper.like(Teacher::getName, sendName);
            List<Teacher> teachers = teacherMapper.selectList(sendNameLambdaQueryWrapper);
            sendIds = teachers.stream().map(Teacher::getId).collect(Collectors.toList());
        }
        String resourceName = commentPageVo.getResourceName();
        List<Long> resourceIds = null;
        if (!StringUtils.isEmpty(resourceName)) {
            LambdaQueryWrapper<Resource> resourceNameLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resourceNameLambdaQueryWrapper.like(Resource::getName, resourceName);
            List<Resource> teachers = resourceMapper.selectList(resourceNameLambdaQueryWrapper);
            resourceIds = teachers.stream().map(Resource::getId).collect(Collectors.toList());
        }


        if (belongIds != null && belongIds.isEmpty()) {
            page.setRecords(new ArrayList<>());
        }
        if (sendIds != null && sendIds.isEmpty()) {
            page.setRecords(new ArrayList<>());
        }
        if (resourceIds != null && resourceIds.isEmpty()) {
            page.setRecords(new ArrayList<>());
        }

        List<Long> finalBelongIds = belongIds;
        List<Long> finalSendIds = sendIds;
        List<Long> finalResourceIds = resourceIds;

        List<CommentPageDto> pageDtoList = page.getRecords().stream().map(comment -> {
            if (finalBelongIds != null && !finalBelongIds.isEmpty() && !finalBelongIds.contains(comment.getBelong())) {
                return null;
            }
            if (finalSendIds != null && !finalSendIds.isEmpty() && !finalSendIds.contains(comment.getSend())) {
                return null;
            }
            if (finalResourceIds != null && !finalResourceIds.isEmpty() && !finalResourceIds.contains(comment.getResource())) {
                return null;
            }

            CommentPageDto commentPageDto = new CommentPageDto();
            BeanUtils.copyProperties(comment, commentPageDto);

            LambdaQueryWrapper<Teacher> belongLambdaQueryWrapper = new LambdaQueryWrapper<>();
            belongLambdaQueryWrapper.eq(Teacher::getId, comment.getBelong());
            Teacher belongTeacher = teacherMapper.selectOne(belongLambdaQueryWrapper);
            if (belongTeacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS));
            }
            commentPageDto.setBelongName(belongTeacher.getName());

            LambdaQueryWrapper<Teacher> sendLambdaQueryWrapper = new LambdaQueryWrapper<>();
            sendLambdaQueryWrapper.eq(Teacher::getId, comment.getSend());
            Teacher sendTeacher = teacherMapper.selectOne(sendLambdaQueryWrapper);
            if (sendTeacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS));
            }
            commentPageDto.setSendName(sendTeacher.getName());

            LambdaQueryWrapper<Resource> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
            resourceLambdaQueryWrapper.eq(Resource::getId, comment.getResource());
            Resource resource = resourceMapper.selectOne(resourceLambdaQueryWrapper);
            if (resource == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS));
            }
            commentPageDto.setResourceName(resource.getName());

            return commentPageDto;
        }).collect(Collectors.toList());
        pageDtoList.removeIf(Objects::isNull);
        returnResult.setTotal(pageDtoList.size());
        returnResult.setRecords(pageDtoList);
        return returnResult;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Page<UserCommentPageDto> getUserCommentPage(HttpServletRequest request, Integer current, Integer pageSize) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，普通用户未登录");
        }
        Page<Comment> page = new Page<>(current, pageSize);
        Page<UserCommentPageDto> returnResult = new Page<>(current, pageSize);
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(Comment::getSend, teacherLoginDto.getId())
                .ne(Comment::getReadStatus, 2)
                .orderByAsc(Comment::getCreateTime);

        this.page(page, lambdaQueryWrapper);
        BeanUtils.copyProperties(page, returnResult, "records");
        List<UserCommentPageDto> pageDtoList = page.getRecords().stream().map(comment -> {
            Teacher teacher = teacherMapper.selectById(comment.getBelong());
            if (teacher == null) {
                return null;
            }
            Resource resource = resourceMapper.selectById(comment.getResource());
            UserCommentPageDto userCommentPageDto = new UserCommentPageDto();
            userCommentPageDto.setId(comment.getId());
            userCommentPageDto.setCreateTime(LocalDateTime.now());
            userCommentPageDto.setBelongId(comment.getBelong());
            userCommentPageDto.setBelongName(teacher.getName());
            userCommentPageDto.setResourceId(comment.getResource());
            userCommentPageDto.setResourceName(resource.getName());
            userCommentPageDto.setReadStatus(comment.getReadStatus());
            userCommentPageDto.setStatus(comment.getStatus());
            if (comment.getStatus() == 0) {
                userCommentPageDto.setContent(comment.getContent());
            } else {
                userCommentPageDto.setContent("该内容已经被封禁");
            }
            return userCommentPageDto;
        }).collect(Collectors.toList());
        pageDtoList.removeIf(Objects::isNull);
        returnResult.setTotal(pageDtoList.size());
        returnResult.setRecords(pageDtoList);
        return returnResult;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteUserComment(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，普通用户未登录");
        }
        Comment resultFromDatabase = commentMapper.selectById(id);
        if (!Objects.equals(teacherLoginDto.getId(), resultFromDatabase.getSend())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "普通用户不能删除其他用户收到的信息");
        }

        if (Objects.equals(resultFromDatabase.getReadStatus(), 2)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "该消息记录已经被删");
        }

        LambdaUpdateWrapper<Comment> commentLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        commentLambdaUpdateWrapper
                .eq(Comment::getId, id)
                .set(Comment::getStatus, 2);
        int updateResult = commentMapper.update(null, commentLambdaUpdateWrapper);

        // 判断数据库插入结果
        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "删除交流评论失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteUserCommentBatch(HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，普通用户未登录");
        }

        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getSend, teacherLoginDto.getId());
        List<Comment> comments = commentMapper.selectList(commentLambdaQueryWrapper);

        if (comments.isEmpty()) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "该用户没有任何消息");
        }

        List<Long> commentIdList = comments.stream().map(Comment::getId).collect(Collectors.toList());
        int updateResult = commentMapper.deleteBatchIds(commentIdList);

        // 判断数据库插入结果
        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "删除交流评论失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    public Page<PostCommentPageDto> pageResourceComment(Long id, Integer current, Integer pageSize, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，普通用户未登录");
        }
        Page<Comment> page = new Page<>(current, pageSize);
        Page<PostCommentPageDto> returnResult = new Page<>(current, pageSize);
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(Comment::getResource, id)
                .orderByDesc(Comment::getCreateTime);

        this.page(page, lambdaQueryWrapper);
        BeanUtils.copyProperties(page, returnResult, "records");
        List<PostCommentPageDto> pageDtoList = page.getRecords().stream().map(comment -> {
            if (comment.getStatus() == 1) {
                return null;
            }
            Resource resource = resourceMapper.selectById(id);
            if (resource == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "教学资料不存在");
            }
            Teacher belongTeacher = teacherMapper.selectById(comment.getBelong());
            if (belongTeacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发送评论的老师不存在");
            }
            College belongCollege = collegeMapper.selectById(belongTeacher.getBelong());
            if (belongCollege == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS), "发送者的学校不存在");
            }
            Teacher sendTeacher = teacherMapper.selectById(comment.getSend());
            if (sendTeacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "接收评论的老师不存在");
            }
            College sendCollege = collegeMapper.selectById(sendTeacher.getBelong());
            if (sendCollege == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS), "接收者的学校不存在");
            }

            PostCommentPageDto postCommentPageDto = new PostCommentPageDto();
            postCommentPageDto.setResourceId(resource.getId());
            postCommentPageDto.setBelong(belongTeacher.getId());
            postCommentPageDto.setBelongName(belongTeacher.getName());
            postCommentPageDto.setBelongAvatarUrl(belongTeacher.getAvatar());
            postCommentPageDto.setBelongCollege(belongCollege.getName());
            postCommentPageDto.setSend(sendTeacher.getId());
            postCommentPageDto.setSendName(sendTeacher.getName());
            postCommentPageDto.setSendAvatarUrl(sendTeacher.getAvatar());
            postCommentPageDto.setSendCollege(sendCollege.getName());
            postCommentPageDto.setCommentId(comment.getId());
            postCommentPageDto.setCommentContent(comment.getContent());
            postCommentPageDto.setCommentOssUrl(comment.getUrl());
            postCommentPageDto.setCommentStatus(comment.getStatus());

            return postCommentPageDto;
        }).collect(Collectors.toList());
        pageDtoList.removeIf(Objects::isNull);
        returnResult.setTotal(pageDtoList.size());
        returnResult.setRecords(pageDtoList);
        return returnResult;
    }
}
