package top.sharehome.share_study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeTransactionException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.mapper.CollectMapper;
import top.sharehome.share_study.mapper.CollegeMapper;
import top.sharehome.share_study.mapper.ResourceMapper;
import top.sharehome.share_study.mapper.TeacherMapper;
import top.sharehome.share_study.model.dto.TeacherLoginDto;
import top.sharehome.share_study.model.dto.UserCollectPageDto;
import top.sharehome.share_study.model.entity.Collect;
import top.sharehome.share_study.model.entity.College;
import top.sharehome.share_study.model.entity.Resource;
import top.sharehome.share_study.model.entity.Teacher;
import top.sharehome.share_study.model.vo.UserCollectPageVo;
import top.sharehome.share_study.service.CollectService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 收藏ServiceImpl
 *
 * @author AntonyCheng
 */
@Service
public class CollectServiceImpl extends ServiceImpl<CollectMapper, Collect> implements CollectService {
    @javax.annotation.Resource
    private ResourceMapper resourceMapper;
    @javax.annotation.Resource
    private TeacherMapper teacherMapper;
    @javax.annotation.Resource
    private CollegeMapper collegeMapper;
    @javax.annotation.Resource
    private CollectMapper collectMapper;

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Page<UserCollectPageDto> getUserResourcePage(Long id, Integer current, Integer pageSize, HttpServletRequest request, UserCollectPageVo userCollectPageVo) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，普通用户未登录");
        }
        Page<Collect> page = new Page<>(current, pageSize);
        Page<UserCollectPageDto> returnResult = new Page<>(current, pageSize);
        LambdaQueryWrapper<Collect> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .eq(Collect::getBelong, id)
                .orderByAsc(Collect::getCreateTime);

        if (userCollectPageVo == null) {
            this.page(page, lambdaQueryWrapper);
            BeanUtils.copyProperties(page, returnResult, "records");
            List<UserCollectPageDto> pageDtoList = page.getRecords().stream().map(collect -> {
                Resource resource = resourceMapper.selectById(collect.getResource());
                Integer status = resource.getStatus();
                if (!Objects.equals(teacherLoginDto.getId(), id) && status == 1) {
                    return null;
                }
                UserCollectPageDto userCollectPageDto = new UserCollectPageDto();

                Teacher teacher = teacherMapper.selectById(resource.getBelong());
                if (teacher == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS));
                }
                userCollectPageDto.setUserId(teacher.getId());
                userCollectPageDto.setUserName(teacher.getName());
                userCollectPageDto.setUserAvatarUrl(teacher.getAvatar());
                College college = collegeMapper.selectById(teacher.getBelong());
                if (college == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS));
                }
                userCollectPageDto.setCollegeName(college.getName());

                userCollectPageDto.setResourceStatus(status);
                userCollectPageDto.setResourceId(resource.getId());
                userCollectPageDto.setResourceName(resource.getName());
                if (status == 0) {
                    userCollectPageDto.setResourceInfo(resource.getInfo());
                    userCollectPageDto.setResourceUrl(resource.getUrl());
                }
                userCollectPageDto.setCollectId(collect.getId());
                userCollectPageDto.setCreateTime(collect.getCreateTime());

                return userCollectPageDto;
            }).collect(Collectors.toList());
            pageDtoList.removeIf(Objects::isNull);
            returnResult.setTotal(pageDtoList.size());
            returnResult.setRecords(pageDtoList);
            return returnResult;
        }

        lambdaQueryWrapper
                .like(!StringUtils.isEmpty(userCollectPageVo.getResourceName()), Collect::getName, userCollectPageVo.getResourceName())
                .like(!StringUtils.isEmpty(userCollectPageVo.getResourceInfo()), Collect::getInfo, userCollectPageVo.getResourceInfo());

        this.page(page, lambdaQueryWrapper);
        BeanUtils.copyProperties(page, returnResult, "records");
        List<UserCollectPageDto> pageDtoList = page.getRecords().stream().map(collect -> {
            Resource resource = resourceMapper.selectById(collect.getResource());
            Integer status = resource.getStatus();
            if (!Objects.equals(teacherLoginDto.getId(), id) && status == 1) {
                return null;
            }
            UserCollectPageDto userCollectPageDto = new UserCollectPageDto();

            Teacher teacher = teacherMapper.selectById(resource.getBelong());
            if (teacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS));
            }
            userCollectPageDto.setUserId(teacher.getId());
            userCollectPageDto.setUserName(teacher.getName());
            userCollectPageDto.setUserAvatarUrl(teacher.getAvatar());
            College college = collegeMapper.selectById(teacher.getBelong());
            if (college == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS));
            }
            userCollectPageDto.setCollegeName(college.getName());

            userCollectPageDto.setResourceStatus(status);
            userCollectPageDto.setResourceId(resource.getId());
            userCollectPageDto.setResourceName(resource.getName());
            if (status == 0) {
                userCollectPageDto.setResourceInfo(resource.getInfo());
                userCollectPageDto.setResourceUrl(resource.getUrl());
            }
            userCollectPageDto.setCollectId(collect.getId());
            userCollectPageDto.setCreateTime(collect.getCreateTime());

            return userCollectPageDto;
        }).collect(Collectors.toList());
        pageDtoList.removeIf(Objects::isNull);
        returnResult.setTotal(pageDtoList.size());
        returnResult.setRecords(pageDtoList);
        return returnResult;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteUserCollect(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);

        if (!Objects.equals(teacherLoginDto.getId(), collectMapper.selectById(id).getBelong())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "不能在用户详情界面删除其他用户的收藏");
        }

        LambdaQueryWrapper<Collect> collectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        collectLambdaQueryWrapper.eq(Collect::getId, id);

        Collect selectResult = collectMapper.selectOne(collectLambdaQueryWrapper);
        if (selectResult == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "收藏不存在，不需要进行下一步操作");
        }

        Resource targetResource = resourceMapper.selectById(selectResult.getResource());
        if (targetResource == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "收藏的教学资料不存在");
        }
        LambdaUpdateWrapper<Resource> resourceLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        resourceLambdaUpdateWrapper
                .set(Resource::getScore, targetResource.getScore() - 1)
                .eq(Resource::getId, targetResource.getId());
        resourceMapper.update(null, resourceLambdaUpdateWrapper);

        Teacher targetTeacher = teacherMapper.selectById(targetResource.getBelong());
        if (targetTeacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "教师的教学资料不存在");
        }
        LambdaUpdateWrapper<Teacher> teacherLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        teacherLambdaUpdateWrapper
                .set(Teacher::getScore, targetTeacher.getScore() - 1)
                .eq(Teacher::getId, targetTeacher.getId());
        teacherMapper.update(null, teacherLambdaUpdateWrapper);

        int deleteResult = collectMapper.delete(collectLambdaQueryWrapper);

        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "收藏数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }
}
