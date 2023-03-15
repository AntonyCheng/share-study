package top.sharehome.share_study.service.impl;

import com.alibaba.excel.EasyExcelFactory;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.client.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeFileException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeTransactionException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.config.FabricGatewayConfig;
import top.sharehome.share_study.mapper.*;
import top.sharehome.share_study.model.dto.*;
import top.sharehome.share_study.model.entity.*;
import top.sharehome.share_study.model.vo.*;
import top.sharehome.share_study.service.FileOssService;
import top.sharehome.share_study.service.ResourceService;
import top.sharehome.share_study.utils.FabricGatewayUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.tomcat.util.codec.binary.StringUtils.newStringUtf8;

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

    @javax.annotation.Resource
    private CollegeMapper collegeMapper;

    @javax.annotation.Resource
    private FileOssService fileOssService;

    @javax.annotation.Resource
    private CollectMapper collectMapper;

    @javax.annotation.Resource
    FabricGatewayUtil fabricGatewayUtil;

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
            // 查询教学资料分类表所有的数据
            List<Resource> resourceList = resourceMapper.selectList(null);
            EasyExcelFactory.write(response.getOutputStream(), Resource.class).sheet("教学资料数据").doWrite(resourceList);
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

        if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId()) && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE) && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员没有权限在此删除其他管理员和超级管理员的教学资料");
        }

        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getResource, id);
        commentMapper.delete(commentLambdaQueryWrapper);

        LambdaQueryWrapper<Collect> collectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectLambdaQueryWrapper.eq(Collect::getResource, id);
        collectMapper.delete(collectLambdaQueryWrapper);

        int deleteResult = resourceMapper.delete(resourceLambdaQueryWrapper);

        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "教学资料数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        fileOssService.delete(selectResult.getUrl());

        //删除链操作


    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteBatch(List<Long> ids, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非管理员不能进行删除操作");
        }

        List<String> urls = ids.stream().map(id -> {
            Resource selectResult = resourceMapper.selectById(id);
            if (selectResult == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "教学资料不存在，不需要进行下一步操作");
            }

            Teacher targetTeacher = teacherMapper.selectById(selectResult.getBelong());
            if (targetTeacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该教学资料的老师不存在");
            }

            if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId()) && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE) && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员没有权限在此删除其他管理员和超级管理员的教学资料");
            }

            return selectResult.getUrl();
        }).collect(Collectors.toList());

        ids.forEach(id -> {
            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            commentLambdaQueryWrapper.eq(Comment::getResource, id);
            commentMapper.delete(commentLambdaQueryWrapper);

            LambdaQueryWrapper<Collect> collectLambdaQueryWrapper = new LambdaQueryWrapper<>();
            collectLambdaQueryWrapper.eq(Collect::getResource, id);
            collectMapper.delete(collectLambdaQueryWrapper);
        });

        int deleteResult = resourceMapper.deleteBatchIds(ids);
        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "教学资料数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        urls.forEach(url -> {
            fileOssService.delete(url);
        });
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public ResourceGetDto getResource(Long id, HttpServletRequest request) {
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

        if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId()) && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE) && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
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

        if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId()) && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE) && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员没有权限在此修改其他管理员和超级管理员的教学资料");
        }

        resultFromDatabase.setStatus(resourceUpdateVo.getStatus());

        int updateResult = resourceMapper.updateById(resultFromDatabase);

        // 判断数据库插入结果
        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改教学资料失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        //管理员修改链//////////////////////////////////////////////////////////////////
        if(teacherLoginDto.getRole()==1){
            try {
                Contract contract = fabricGatewayUtil.getContract();
                String operateName = "管理员:" + teacherLoginDto.getName() + "(ID=" + teacherLoginDto.getId() + ")" + "执行修改资料操作";
                contract.newProposal("updateResource")
                        .addArguments(String.valueOf(resultFromDatabase.getId()), operateName, String.valueOf(resultFromDatabase.getBelong()),
                                resultFromDatabase.getName(), resultFromDatabase.getInfo(), resultFromDatabase.getUrl(), String.valueOf(resultFromDatabase.getScore()), String.valueOf(resultFromDatabase.getStatus()),
                                String.valueOf(resultFromDatabase.getCreateTime()), String.valueOf(resultFromDatabase.getUpdateTime()), String.valueOf(resultFromDatabase.getIsDeleted()))
                        .build()
                        .endorse()
                        .submitAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (teacherLoginDto.getRole()==2){
            try {
                Contract contract = fabricGatewayUtil.getContract();
                String operateName = "超级管理员:" + teacherLoginDto.getName() + "(ID=" + teacherLoginDto.getId() + ")" + "执行修改资料操作";
                contract.newProposal("updateResource")
                        .addArguments(String.valueOf(resultFromDatabase.getId()), operateName, String.valueOf(resultFromDatabase.getBelong()),
                                resultFromDatabase.getName(), resultFromDatabase.getInfo(), resultFromDatabase.getUrl(), String.valueOf(resultFromDatabase.getScore()), String.valueOf(resultFromDatabase.getStatus()),
                                String.valueOf(resultFromDatabase.getCreateTime()), String.valueOf(resultFromDatabase.getUpdateTime()), String.valueOf(resultFromDatabase.getIsDeleted()))
                        .build()
                        .endorse()
                        .submitAsync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Page<ResourcePageDto> pageResource(Integer current, Integer pageSize, ResourcePageVo resourcePageVo) {
        Page<Resource> page = new Page<>(current, pageSize);
        Page<ResourcePageDto> returnResult = new Page<>(current, pageSize);
        LambdaQueryWrapper<Resource> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Resource::getCreateTime);
        if (resourcePageVo == null) {
            this.page(page, lambdaQueryWrapper);
            BeanUtils.copyProperties(page, returnResult, "records");
            List<ResourcePageDto> pageDtoList = page.getRecords().stream().map(resource -> {
                ResourcePageDto resourcePageDto = new ResourcePageDto();
                BeanUtils.copyProperties(resource, resourcePageDto);
                LambdaQueryWrapper<Teacher> resourceLambdaQueryWrapper = new LambdaQueryWrapper<>();
                resourceLambdaQueryWrapper.eq(Teacher::getId, resource.getBelong());
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

        lambdaQueryWrapper.like(!StringUtils.isEmpty(resourcePageVo.getInfo()), Resource::getInfo, resourcePageVo.getInfo()).like(!StringUtils.isEmpty(resourcePageVo.getName()), Resource::getName, resourcePageVo.getName()).like(!ObjectUtils.isEmpty(resourcePageVo.getStatus()), Resource::getStatus, resourcePageVo.getStatus());
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

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Page<PostPageDto> getUserResourcePage(Long id, Integer current, Integer pageSize, HttpServletRequest request, UserResourcePageVo userResourcePageVo) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，普通用户未登录");
        }
        Page<Resource> page = new Page<>(current, pageSize);
        Page<PostPageDto> returnResult = new Page<>(current, pageSize);
        LambdaQueryWrapper<Resource> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Resource::getBelong, id).orderByAsc(Resource::getCreateTime);

        if (userResourcePageVo == null) {
            this.page(page, lambdaQueryWrapper);
            BeanUtils.copyProperties(page, returnResult, "records");
            List<PostPageDto> pageDtoList = page.getRecords().stream().map(resource -> {
                Integer status = resource.getStatus();
                if (!Objects.equals(teacherLoginDto.getId(), id) && status == 1) {
                    return null;
                }
                PostPageDto userResourcePageDto = new PostPageDto();

                Teacher teacher = teacherMapper.selectById(resource.getBelong());
                if (teacher == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS));
                }
                userResourcePageDto.setUserId(teacher.getId());
                userResourcePageDto.setUserName(teacher.getName());
                userResourcePageDto.setUserAvatarUrl(teacher.getAvatar());
                College college = collegeMapper.selectById(teacher.getBelong());
                if (college == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS));
                }
                userResourcePageDto.setCollegeName(college.getName());

                userResourcePageDto.setResourceStatus(status);
                userResourcePageDto.setResourceId(resource.getId());
                userResourcePageDto.setResourceName(resource.getName());
                if (status == 0) {
                    userResourcePageDto.setResourceInfo(resource.getInfo());
                    userResourcePageDto.setResourceScore(resource.getScore());
                    userResourcePageDto.setResourceUrl(resource.getUrl());
                }
                LambdaUpdateWrapper<Collect> collectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                collectLambdaUpdateWrapper.eq(Collect::getBelong, teacherLoginDto.getId());
                List<Collect> collectList = collectMapper.selectList(collectLambdaUpdateWrapper);
                List<Long> resourceIds = collectList.stream().map(Collect::getResource).collect(Collectors.toList());
                userResourcePageDto.setCollectStatus(resourceIds.contains(resource.getId()) ? 1 : 0);
                userResourcePageDto.setCreateTime(resource.getCreateTime());
                LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                commentLambdaQueryWrapper.eq(Comment::getResource, resource.getId());
                Integer commentCount = Math.toIntExact(commentMapper.selectCount(commentLambdaQueryWrapper));
                userResourcePageDto.setCommentCount(commentCount);

                return userResourcePageDto;
            }).collect(Collectors.toList());
            pageDtoList.removeIf(Objects::isNull);
            returnResult.setTotal(pageDtoList.size());
            returnResult.setRecords(pageDtoList);
            return returnResult;
        }

        lambdaQueryWrapper.like(!StringUtils.isEmpty(userResourcePageVo.getResourceName()), Resource::getName, userResourcePageVo.getResourceName()).like(!StringUtils.isEmpty(userResourcePageVo.getResourceInfo()), Resource::getInfo, userResourcePageVo.getResourceInfo());

        this.page(page, lambdaQueryWrapper);
        BeanUtils.copyProperties(page, returnResult, "records");
        List<PostPageDto> pageDtoList = page.getRecords().stream().map(resource -> {
            Integer status = resource.getStatus();
            if (!Objects.equals(teacherLoginDto.getId(), id) && status == 1) {
                return null;
            }
            PostPageDto userResourcePageDto = new PostPageDto();

            Teacher teacher = teacherMapper.selectById(resource.getBelong());
            if (teacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS));
            }
            userResourcePageDto.setUserId(teacher.getId());
            userResourcePageDto.setUserName(teacher.getName());
            userResourcePageDto.setUserAvatarUrl(teacher.getAvatar());
            College college = collegeMapper.selectById(teacher.getBelong());
            if (college == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS));
            }
            userResourcePageDto.setCollegeName(college.getName());

            userResourcePageDto.setResourceStatus(status);
            userResourcePageDto.setResourceId(resource.getId());
            userResourcePageDto.setResourceName(resource.getName());
            if (status == 0) {
                userResourcePageDto.setResourceInfo(resource.getInfo());
                userResourcePageDto.setResourceScore(resource.getScore());
                userResourcePageDto.setResourceUrl(resource.getUrl());
            }
            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            commentLambdaQueryWrapper.eq(Comment::getResource, resource.getId());
            Integer commentCount = Math.toIntExact(commentMapper.selectCount(commentLambdaQueryWrapper));
            userResourcePageDto.setCommentCount(commentCount);

            return userResourcePageDto;
        }).collect(Collectors.toList());
        pageDtoList.removeIf(Objects::isNull);
        returnResult.setTotal(pageDtoList.size());
        returnResult.setRecords(pageDtoList);
        return returnResult;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteUserResource(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);

        if (!Objects.equals(teacherLoginDto.getId(), resourceMapper.selectById(id).getBelong())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "不能在用户详情界面删除其他用户的教学资料");
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

        if (!Objects.equals(targetTeacher.getId(), teacherLoginDto.getId()) && (Objects.equals(teacherLoginDto.getRole(), CommonConstant.ADMIN_ROLE) && !Objects.equals(targetTeacher.getRole(), CommonConstant.DEFAULT_ROLE))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "管理员没有权限在此删除其他管理员和超级管理员的教学资料");
        }

        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getResource, id);
        commentMapper.delete(commentLambdaQueryWrapper);

        int deleteResult = resourceMapper.delete(resourceLambdaQueryWrapper);

        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "教学资料数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        fileOssService.delete(selectResult.getUrl());
//待定。
        //用户自己删除链操作//////////////////////////////////////////////////////////////////////
//        try {
//            Contract contract = fabricUtil.getContract();
//            contract.submitTransaction("deleteResource", String.valueOf(id));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public UserResourceGetDto getUserResource(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (Objects.isNull(teacherLoginDto)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "用户未登录");
        }
        Resource resource = resourceMapper.selectById(id);
        if (Objects.isNull(resource)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS));
        }
        if (!Objects.equals(teacherLoginDto.getId(), resource.getBelong())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "任何用户都无法在个人信息页面获取其他用户的教学资料信息");
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

        UserResourceGetDto userResourceGetDto = new UserResourceGetDto();
        userResourceGetDto.setId(selectResult.getId());
        userResourceGetDto.setName(selectResult.getName());
        userResourceGetDto.setInfo(selectResult.getInfo());
        userResourceGetDto.setUrl(selectResult.getUrl());
        return userResourceGetDto;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void updateUserResource(UserResourceUpdateVo userResourceUpdateVo, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (Objects.isNull(teacherLoginDto)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "用户未登录");
        }
        Resource resultFromDatabase = resourceMapper.selectById(userResourceUpdateVo.getId());
        if (Objects.isNull(resultFromDatabase)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "教学资料不存在，不需要进行下一步操作");
        }
        if (!Objects.equals(teacherLoginDto.getId(), resultFromDatabase.getBelong())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "任何用户都无法在个人信息页面获取其他用户的教学资料信息");
        }

        if (Objects.equals(userResourceUpdateVo.getName(), resultFromDatabase.getName()) && Objects.equals(userResourceUpdateVo.getInfo(), resultFromDatabase.getInfo()) && Objects.equals(userResourceUpdateVo.getUrl(), resultFromDatabase.getUrl())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.THE_UPDATE_DATA_IS_THE_SAME_AS_THE_BACKGROUND_DATA), "更新数据和库中数据相同");
        }

        Teacher targetTeacher = teacherMapper.selectById(resultFromDatabase.getBelong());
        if (targetTeacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该教学资料的老师不存在");
        }

        if (!Objects.equals(resultFromDatabase.getUrl(), userResourceUpdateVo.getUrl())) {
            fileOssService.delete(resultFromDatabase.getUrl());
        }

        resultFromDatabase.setName(userResourceUpdateVo.getName());
        resultFromDatabase.setInfo(userResourceUpdateVo.getInfo());
        resultFromDatabase.setUrl(userResourceUpdateVo.getUrl());

        int updateResult = resourceMapper.updateById(resultFromDatabase);

        // 判断数据库插入结果
        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改教学资料失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        //修改链操作///////////////////////////////////////////////////////////
        try {
            Contract contract = fabricGatewayUtil.getContract();
            String operateName = "普通用户:" + teacherLoginDto.getName() + "(ID=" + teacherLoginDto.getId() + ")" + "执行修改资料操作";
            contract.newProposal("updateResource")
                    .addArguments(String.valueOf(resultFromDatabase.getId()), operateName, String.valueOf(resultFromDatabase.getBelong()),
                            resultFromDatabase.getName(), resultFromDatabase.getInfo(), resultFromDatabase.getUrl(), String.valueOf(resultFromDatabase.getScore()), String.valueOf(resultFromDatabase.getStatus()),
                            String.valueOf(resultFromDatabase.getCreateTime()), String.valueOf(resultFromDatabase.getUpdateTime()), String.valueOf(resultFromDatabase.getIsDeleted()))
                    .build()
                    .endorse()
                    .submitAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Page<PostPageDto> pagePost(Integer current, Integer pageSize, HttpServletRequest request, PostPageVo postPageVo) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，普通用户未登录");
        }
        Page<Resource> page = new Page<>(current, pageSize);
        Page<PostPageDto> returnResult = new Page<>(current, pageSize);
        LambdaQueryWrapper<Resource> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByDesc(Resource::getCreateTime);

        if (postPageVo == null) {
            this.page(page, lambdaQueryWrapper);
            BeanUtils.copyProperties(page, returnResult, "records");
            List<PostPageDto> pageDtoList = page.getRecords().stream().map(resource -> {
                if (resource.getStatus() == 1) {
                    return null;
                }
                PostPageDto postPageDto = new PostPageDto();

                Teacher teacher = teacherMapper.selectById(resource.getBelong());
                if (teacher == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS));
                }
                postPageDto.setUserId(teacher.getId());
                postPageDto.setUserName(teacher.getName());
                postPageDto.setUserAvatarUrl(teacher.getAvatar());
                College college = collegeMapper.selectById(teacher.getBelong());
                if (college == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS));
                }
                postPageDto.setCollegeName(college.getName());

                postPageDto.setResourceStatus(resource.getStatus());
                postPageDto.setResourceId(resource.getId());
                postPageDto.setResourceName(resource.getName());
                postPageDto.setResourceInfo(resource.getInfo());
                postPageDto.setResourceScore(resource.getScore());
                postPageDto.setResourceUrl(resource.getUrl());
                postPageDto.setCreateTime(resource.getCreateTime());
                LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
                commentLambdaQueryWrapper.eq(Comment::getResource, resource.getId());
                Integer commentCount = Math.toIntExact(commentMapper.selectCount(commentLambdaQueryWrapper));
                postPageDto.setCommentCount(commentCount);

                LambdaUpdateWrapper<Collect> collectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                collectLambdaUpdateWrapper.eq(Collect::getBelong, teacherLoginDto.getId());
                List<Collect> collectList = collectMapper.selectList(collectLambdaUpdateWrapper);
                List<Long> resourceIds = collectList.stream().map(Collect::getResource).collect(Collectors.toList());
                postPageDto.setCollectStatus(resourceIds.contains(resource.getId()) ? 1 : 0);

                return postPageDto;
            }).collect(Collectors.toList());
            pageDtoList.removeIf(Objects::isNull);
            returnResult.setTotal(pageDtoList.size());
            returnResult.setRecords(pageDtoList);
            return returnResult;
        }

        lambdaQueryWrapper.like(!StringUtils.isEmpty(postPageVo.getName()), Resource::getName, postPageVo.getName()).like(!StringUtils.isEmpty(postPageVo.getInfo()), Resource::getInfo, postPageVo.getInfo());
        this.page(page, lambdaQueryWrapper);
        BeanUtils.copyProperties(page, returnResult, "records");

        String belongName = postPageVo.getBelongName();
        List<Long> belongIds = null;
        if (!StringUtils.isEmpty(belongName)) {
            LambdaQueryWrapper<Teacher> belongNameLambdaQueryWrapper = new LambdaQueryWrapper<>();
            belongNameLambdaQueryWrapper.like(Teacher::getName, belongName);
            List<Teacher> teachers = teacherMapper.selectList(belongNameLambdaQueryWrapper);
            belongIds = teachers.stream().map(Teacher::getId).collect(Collectors.toList());
        }
        String collegeName = postPageVo.getCollegeName();
        List<Long> collegeIds = null;
        if (!StringUtils.isEmpty(collegeName)) {
            LambdaQueryWrapper<College> belongNameLambdaQueryWrapper = new LambdaQueryWrapper<>();
            belongNameLambdaQueryWrapper.like(College::getName, collegeName);
            List<College> colleges = collegeMapper.selectList(belongNameLambdaQueryWrapper);
            collegeIds = colleges.stream().map(College::getId).collect(Collectors.toList());
        }

        if (belongIds != null && belongIds.isEmpty()) {
            page.setRecords(new ArrayList<>());
        }
        if (collegeIds != null && collegeIds.isEmpty()) {
            page.setRecords(new ArrayList<>());
        }

        List<Long> finalBelongIds = belongIds;
        List<Long> finalCollegeIds = collegeIds;

        List<PostPageDto> pageDtoList = page.getRecords().stream().map(resource -> {
            if (finalBelongIds != null && !finalBelongIds.isEmpty() && !finalBelongIds.contains(resource.getBelong())) {
                return null;
            }
            if (finalCollegeIds != null && !finalCollegeIds.isEmpty() && !finalCollegeIds.contains(collegeMapper.selectById(teacherMapper.selectById(resource.getBelong()).getBelong()).getId())) {
                return null;
            }

            if (resource.getStatus() == 1) {
                return null;
            }
            PostPageDto postPageDto = new PostPageDto();

            Teacher teacher = teacherMapper.selectById(resource.getBelong());
            if (teacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS));
            }
            postPageDto.setUserId(teacher.getId());
            postPageDto.setUserName(teacher.getName());
            postPageDto.setUserAvatarUrl(teacher.getAvatar());
            College college = collegeMapper.selectById(teacher.getBelong());
            if (college == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS));
            }
            postPageDto.setCollegeName(college.getName());
            postPageDto.setResourceStatus(resource.getStatus());
            postPageDto.setResourceId(resource.getId());
            postPageDto.setResourceName(resource.getName());
            postPageDto.setResourceInfo(resource.getInfo());
            postPageDto.setResourceScore(resource.getScore());
            postPageDto.setResourceUrl(resource.getUrl());
            LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
            commentLambdaQueryWrapper.eq(Comment::getResource, resource.getId());
            Integer commentCount = Math.toIntExact(commentMapper.selectCount(commentLambdaQueryWrapper));
            postPageDto.setCommentCount(commentCount);

            LambdaUpdateWrapper<Collect> collectLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            collectLambdaUpdateWrapper.eq(Collect::getBelong, teacherLoginDto.getId());
            List<Collect> collects = collectMapper.selectList(collectLambdaUpdateWrapper);
            if (Boolean.TRUE.equals(collects.stream().map(Collect::getResource).collect(Collectors.toList()).contains(resource.getId()))) {
                postPageDto.setCollectStatus(1);
            } else {
                postPageDto.setCollectStatus(0);
            }

            return postPageDto;
        }).collect(Collectors.toList());
        pageDtoList.removeIf(Objects::isNull);
        returnResult.setTotal(pageDtoList.size());
        returnResult.setRecords(pageDtoList);
        return returnResult;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void add(PostAddVo postAddVo, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，普通用户未登录");
        }
        if (!Objects.equals(postAddVo.getBelong(), teacherLoginDto.getId())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED));
        }

        Resource resource = new Resource();
        BeanUtils.copyProperties(postAddVo, resource);

        int insertResult = resourceMapper.insert(resource);

        // 判断数据库插入结果
        if (insertResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_ADDITION_FAILED), "添加教学资料失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        LambdaUpdateWrapper<Teacher> teacherLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        teacherLambdaUpdateWrapper.set(Teacher::getScore, teacherMapper.selectById(teacherLoginDto.getId()).getScore() + 1).eq(Teacher::getId, teacherLoginDto.getId());
        teacherMapper.update(null, teacherLambdaUpdateWrapper);

        //入链操作/////////////////////////////////////////////////////////////////////////////////////
        try {
            Contract contract = fabricGatewayUtil.getContract();
            String operateName = "普通用户:" + teacherLoginDto.getName() + "(ID=" + teacherLoginDto.getId() + ")" + "执行增加资料操作";
            contract.newProposal("createResource")
                    .addArguments(String.valueOf(resource.getId()), operateName, String.valueOf(resource.getBelong()),
                            resource.getName(), resource.getInfo(), resource.getUrl(), String.valueOf(resource.getScore()), String.valueOf(resource.getStatus()),
                            String.valueOf(resource.getCreateTime()), String.valueOf(resource.getUpdateTime()), String.valueOf(resource.getIsDeleted()))
                    .build()
                    .endorse()
                    .submitAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public PostInfoDto info(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，普通用户未登录");
        }

        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "教学资料不存在");
        }

        Teacher teacher = teacherMapper.selectById(resource.getBelong());
        if (teacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "教师信息不存在");
        }

        College college = collegeMapper.selectById(teacher.getBelong());
        if (college == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS), "高校不存在");
        }

        PostInfoDto postInfoDto = new PostInfoDto();
        postInfoDto.setUserId(teacher.getId());
        postInfoDto.setUserName(teacher.getName());
        postInfoDto.setUserAvatarUrl(teacher.getAvatar());

        postInfoDto.setCollegeName(college.getName());

        Integer status = resource.getStatus();
        if (status == 1) {
            postInfoDto.setResourceStatus(status);
            return postInfoDto;
        }
        postInfoDto.setResourceStatus(status);
        postInfoDto.setResourceId(resource.getId());
        postInfoDto.setResourceName(resource.getName());
        postInfoDto.setResourceInfo(resource.getInfo());
        postInfoDto.setResourceUrl(resource.getUrl());
        postInfoDto.setResourceScore(resource.getScore());

        LambdaQueryWrapper<Comment> commentLambdaQueryWrapper = new LambdaQueryWrapper<>();
        commentLambdaQueryWrapper.eq(Comment::getResource, resource.getId());
        postInfoDto.setCommentCount(Math.toIntExact(commentMapper.selectCount(commentLambdaQueryWrapper)));

        LambdaQueryWrapper<Collect> collectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectLambdaQueryWrapper.eq(Collect::getBelong, teacher.getId());
        List<Collect> collectList = collectMapper.selectList(collectLambdaQueryWrapper);
        List<Long> resourceIds = collectList.stream().map(Collect::getResource).collect(Collectors.toList());
        postInfoDto.setCollectStatus(resourceIds.contains(id) ? 1 : 0);

        return postInfoDto;
    }

}
