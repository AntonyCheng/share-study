package top.sharehome.share_study.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeTransactionException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.config.RabbitMqConfig;
import top.sharehome.share_study.mapper.CollegeMapper;
import top.sharehome.share_study.mapper.TeacherCensorMapper;
import top.sharehome.share_study.mapper.TeacherMapper;
import top.sharehome.share_study.model.dto.teacher.TeacherLoginDto;
import top.sharehome.share_study.model.dto.teacher_censor.TeacherCensorPageDto;
import top.sharehome.share_study.model.entity.College;
import top.sharehome.share_study.model.entity.Teacher;
import top.sharehome.share_study.model.entity.TeacherCensor;
import top.sharehome.share_study.model.vo.teacher.TeacherRegisterVo;
import top.sharehome.share_study.model.vo.teacher_censor.TeacherCensorPageVo;
import top.sharehome.share_study.model.vo.teacher_censor.TeacherCensorUpdateVo;
import top.sharehome.share_study.service.TeacherCensorService;
import top.sharehome.share_study.utils.object.ObjectDataUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 教师用户注册审核ServiceImpl
 *
 * @author AntonyCheng
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TeacherCensorServiceImpl extends ServiceImpl<TeacherCensorMapper, TeacherCensor> implements TeacherCensorService {
    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private CollegeMapper collegeMapper;

    @Resource
    private TeacherCensorMapper teacherCensorMapper;

    @Resource(name = "noSingletonRabbitTemplate")
    private RabbitTemplate noSingletonRabbitTemplate;

    /**
     * 注册加盐
     */
    private static final String SALT = "share_study_platform";

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void addTeacherCensor(TeacherRegisterVo teacherRegisterVo) {
        // 校验数据库中是否包含该用户
        LambdaQueryWrapper<TeacherCensor> teacherCensorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherCensorLambdaQueryWrapper
                .eq(TeacherCensor::getAccount, teacherRegisterVo.getAccount())
                .and(condition -> {
                    condition
                            .eq(TeacherCensor::getStatus, CommonConstant.TEACHER_CENSOR_STATUS_PASS)
                            .or()
                            .eq(TeacherCensor::getStatus, CommonConstant.TEACHER_CENSOR_STATUS_WAIT);
                });
        Long resultFromTeacherCensor = teacherCensorMapper.selectCount(teacherCensorLambdaQueryWrapper);
        if (resultFromTeacherCensor != 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_ALREADY_EXISTS), "数据库中已经包含该用户：" + teacherRegisterVo.getAccount());
        }
        LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherLambdaQueryWrapper.eq(Teacher::getAccount, teacherRegisterVo.getAccount());
        Long resultFromTeacher = teacherMapper.selectCount(teacherLambdaQueryWrapper);
        if (resultFromTeacher != 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_ALREADY_EXISTS), "数据库中已经包含该用户：" + teacherRegisterVo.getAccount());
        }

        College resultCollege = collegeMapper.selectById(teacherRegisterVo.getBelong());
        if (resultCollege == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.COLLEGE_NOT_EXISTS), "没有该院校");
        }

        TeacherCensor teacherCensor = new TeacherCensor();
        BeanUtils.copyProperties(teacherRegisterVo, teacherCensor);
        teacherCensor.setPassword(DigestUtil.md5Hex(teacherCensor.getPassword() + SALT));
        if (StringUtils.isEmpty(teacherCensor.getAvatar())) {
            teacherCensor.setAvatar("");
        }

        int insertResult = teacherCensorMapper.insert(teacherCensor);

        // 判断数据库插入结果
        if (insertResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_ADDITION_FAILED), "注册申请插入审核失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void publishTeacherCensor(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN));
        }
        if (!Objects.equals(teacherLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非超级管理员不能进行发布操作");
        }

        TeacherCensor teacherCensor = teacherCensorMapper.selectById(id);
        LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherLambdaQueryWrapper.eq(Teacher::getAccount, teacherCensor.getAccount());
        Long resultFromTeacher = teacherMapper.selectCount(teacherLambdaQueryWrapper);
        if (resultFromTeacher != 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.USERNAME_ALREADY_EXISTS), "数据库中已经包含该用户：" + teacherCensor.getAccount());
        }


        if (!Objects.equals(teacherCensor.getStatus(), CommonConstant.TEACHER_CENSOR_STATUS_PASS)) {
            if (Objects.equals(teacherCensor.getStatus(), CommonConstant.TEACHER_CENSOR_STATUS_PUBLISHED)) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_DUPLICATE_RELEASES));
            }
            throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_NOT_PASS));
        }

        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherCensor, teacher, "id");

        int insertResult = teacherMapper.insert(teacher);

        // 判断数据库插入结果
        if (insertResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_ADDITION_FAILED), "用户注册失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        teacherCensor.setStatus(CommonConstant.TEACHER_CENSOR_STATUS_PUBLISHED);
        int teacherCensorUpdateResult = teacherCensorMapper.updateById(teacherCensor);
        if (teacherCensorUpdateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改教师注册申请审核失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        String to = teacherCensor.getEmail();
        String subject = "基于区块链的资源共享平台申请反馈";
        String content = teacherCensor.getContent();
        if (StringUtils.isEmpty(content)) {
            content = "无";
        }
        HashMap<String, Object> rabbitMqResult = new HashMap<>();
        rabbitMqResult.put("to", to);
        rabbitMqResult.put("subject", subject);
        rabbitMqResult.put("content", "<h1>基于区块链的资源共享平台</h1><h2>您的注册申请已经通过，欢迎您的加入！</h2><p><b>补充说明:</b>" + content + "</p>");
        try {
            noSingletonRabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
                @Override
                public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                    if (!ack) {
                        throw new CustomizeReturnException(R.failure(RCodeEnum.PROVIDER_TO_EXCHANGE_ERROR));
                    }
                }
            });
            noSingletonRabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
                @Override
                public void returnedMessage(ReturnedMessage returnedMessage) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.EXCHANGE_TO_QUEUE_ERROR));
                }
            });
            noSingletonRabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "mail." + CommonConstant.SEND_REGISTRATION_FEEDBACK_EMAIL, JSON.toJSONString(rabbitMqResult));
        } catch (AmqpException exception) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.MESSAGE_QUEUE_SEND_ERROR));
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Boolean updateTeacherCensor(TeacherCensorUpdateVo teacherCensorUpdateVo, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN));
        }
        if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非管理员不能进行删除操作");
        }

        TeacherCensor resultFromDatabase = teacherCensorMapper.selectById(teacherCensorUpdateVo.getId());
        if (resultFromDatabase == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "目标用户申请不存在，不需要进行下一步操作");
        }

        Boolean returnResult = false;

        if (!Objects.equals(resultFromDatabase.getStatus(), CommonConstant.TEACHER_CENSOR_STATUS_WAIT)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_OF_INFORMATION_HAS_BEEN_COMPLETED));
        }

        if (Objects.equals(teacherCensorUpdateVo.getResult(), CommonConstant.TEACHER_CENSOR_OPTIONS_FAILURE)) {
            resultFromDatabase.setCensorAdminId(teacherLoginDto.getId());
            resultFromDatabase.setCensorAdminName(teacherLoginDto.getName());
            resultFromDatabase.setContent(teacherCensorUpdateVo.getContent());
            resultFromDatabase.setStatus(CommonConstant.TEACHER_CENSOR_STATUS_NOT_PASS);
        }

        if (Objects.equals(teacherCensorUpdateVo.getResult(), CommonConstant.TEACHER_CENSOR_OPTIONS_SUCCESS)) {
            resultFromDatabase.setCensorAdminId(teacherLoginDto.getId());
            resultFromDatabase.setCensorAdminName(teacherLoginDto.getName());
            resultFromDatabase.setContent(teacherCensorUpdateVo.getContent());
            resultFromDatabase.setStatus(CommonConstant.TEACHER_CENSOR_STATUS_PASS);
            returnResult = true;
        }

        int updateResult = teacherCensorMapper.updateById(resultFromDatabase);

        // 判断数据库插入结果
        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改注册申请审核失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
        if (Objects.equals(teacherCensorUpdateVo.getResult(), CommonConstant.TEACHER_CENSOR_OPTIONS_FAILURE)) {
            String to = resultFromDatabase.getEmail();
            String subject = "基于区块链的资源共享平台申请反馈";
            String content = resultFromDatabase.getContent();
            if (StringUtils.isEmpty(content)) {
                content = "无";
            }
            HashMap<String, Object> rabbitMqResult = new HashMap<>();
            rabbitMqResult.put("to", to);
            rabbitMqResult.put("subject", subject);
            rabbitMqResult.put("content", "<h1>基于区块链的资源共享平台</h1><h2>很抱歉，您的信息不符合平台要求，审核未通过</h2><p><b>补充说明:</b>" + content + "</p>");
            try {
                noSingletonRabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
                    @Override
                    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                        if (!ack) {
                            throw new CustomizeReturnException(R.failure(RCodeEnum.PROVIDER_TO_EXCHANGE_ERROR));
                        }
                    }
                });
                noSingletonRabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
                    @Override
                    public void returnedMessage(ReturnedMessage returnedMessage) {
                        throw new CustomizeReturnException(R.failure(RCodeEnum.EXCHANGE_TO_QUEUE_ERROR));
                    }
                });
                noSingletonRabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "mail." + CommonConstant.SEND_REGISTRATION_FEEDBACK_EMAIL, JSON.toJSONString(rabbitMqResult));
            } catch (AmqpException exception) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.MESSAGE_QUEUE_SEND_ERROR));
            }
        }

        return returnResult;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Page<TeacherCensorPageDto> pageTeacherCensor(Integer current, Integer pageSize, TeacherCensorPageVo teacherCensorPageVo) {
        Page<TeacherCensor> page = new Page<>(current, pageSize);
        Page<TeacherCensorPageDto> returnResult = new Page<>(current, pageSize);
        LambdaQueryWrapper<TeacherCensor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .orderByAsc(TeacherCensor::getStatus)
                .orderByDesc(TeacherCensor::getCreateTime);
        if (ObjectDataUtil.isAllObjectDataEmpty(teacherCensorPageVo)) {
            this.page(page, lambdaQueryWrapper);
            BeanUtils.copyProperties(page, returnResult, "records");
            List<TeacherCensorPageDto> pageDtoList = page.getRecords().stream().map(teacherCensor -> {
                TeacherCensorPageDto teacherCensorPageDto = new TeacherCensorPageDto();
                BeanUtils.copyProperties(teacherCensor, teacherCensorPageDto);
                LambdaQueryWrapper<College> collegeLambdaQueryWrapper = new LambdaQueryWrapper<>();
                collegeLambdaQueryWrapper.eq(College::getId, teacherCensor.getBelong());
                College college = collegeMapper.selectOne(collegeLambdaQueryWrapper);
                if (college == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "教学资料所属教师不存在");
                }
                teacherCensorPageDto.setBelongName(college.getName());
                return teacherCensorPageDto;
            }).collect(Collectors.toList());
            returnResult.setRecords(pageDtoList);
            return returnResult;
        }
        lambdaQueryWrapper
                .like(!StringUtils.isEmpty(teacherCensorPageVo.getAccount()), TeacherCensor::getAccount, teacherCensorPageVo.getAccount())
                .like(!StringUtils.isEmpty(teacherCensorPageVo.getCensorAdminName()), TeacherCensor::getCensorAdminName, teacherCensorPageVo.getCensorAdminName())
                .like(!StringUtils.isEmpty(teacherCensorPageVo.getName()), TeacherCensor::getName, teacherCensorPageVo.getName())
                .like(!ObjectUtils.isEmpty(teacherCensorPageVo.getGender()), TeacherCensor::getGender, teacherCensorPageVo.getGender())
                .like(!ObjectUtils.isEmpty(teacherCensorPageVo.getStatus()), TeacherCensor::getStatus, teacherCensorPageVo.getStatus());

        String belongName = teacherCensorPageVo.getBelongName();
        List<Long> collegeIds = null;
        if (!StringUtils.isEmpty(belongName)) {
            LambdaQueryWrapper<College> collegeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            collegeLambdaQueryWrapper.like(College::getName, belongName);
            List<College> colleges = collegeMapper.selectList(collegeLambdaQueryWrapper);
            collegeIds = colleges.stream().map(College::getId).collect(Collectors.toList());
        }

        List<Long> finalCollegeIds = collegeIds;
        
        if (!(!Objects.isNull(finalCollegeIds)&&finalCollegeIds.isEmpty())) {
            if (!Objects.isNull(finalCollegeIds)&&!finalCollegeIds.isEmpty()){
                lambdaQueryWrapper
                        .in(TeacherCensor::getBelong,finalCollegeIds);
            }
            this.page(page, lambdaQueryWrapper);
        }
        BeanUtils.copyProperties(page, returnResult, "records");

        List<TeacherCensorPageDto> pageDtoList = page.getRecords().stream().map(record -> {
            TeacherCensorPageDto teacherCensorPageDto = new TeacherCensorPageDto();
            BeanUtils.copyProperties(record, teacherCensorPageDto);
            LambdaQueryWrapper<College> collegeLambdaQueryWrapper = new LambdaQueryWrapper<>();
            collegeLambdaQueryWrapper.eq(College::getId, record.getBelong());
            College college = collegeMapper.selectOne(collegeLambdaQueryWrapper);
            if (college == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "教学资料所属教师不存在");
            }
            teacherCensorPageDto.setBelongName(college.getName());
            return teacherCensorPageDto;
        }).collect(Collectors.toList());
        returnResult.setRecords(pageDtoList);
        return returnResult;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteTeacherCensor(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (!Objects.equals(teacherLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非超级管理员不能进行删除操作");
        }

        LambdaQueryWrapper<TeacherCensor> teacherCensorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        teacherCensorLambdaQueryWrapper.eq(TeacherCensor::getId, id);
        TeacherCensor selectResult = teacherCensorMapper.selectOne(teacherCensorLambdaQueryWrapper);

        if (selectResult == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "用户注册申请条目不存在，不需要进行下一步操作");
        }

        if (!(Objects.equals(selectResult.getStatus(), CommonConstant.TEACHER_CENSOR_STATUS_NOT_PASS)
                || Objects.equals(selectResult.getStatus(), CommonConstant.TEACHER_CENSOR_STATUS_PUBLISHED))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_DID_NOT_COMPLETE), "未完成审核的数据无法被删除");
        }

        int deleteResult = teacherCensorMapper.delete(teacherCensorLambdaQueryWrapper);

        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "用户注册申请审核数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteBatchTeacherCensor(List<Long> ids, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (!Objects.equals(teacherLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非超级管理员不能进行删除操作");
        }

        ids.forEach(id -> {
            TeacherCensor selectResult = teacherCensorMapper.selectById(id);
            if (selectResult == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "用户注册申请条目不存在，不需要进行下一步操作");
            }

            if (!(Objects.equals(selectResult.getStatus(), CommonConstant.TEACHER_CENSOR_STATUS_NOT_PASS)
                    || Objects.equals(selectResult.getStatus(), CommonConstant.TEACHER_CENSOR_STATUS_PUBLISHED))) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_DID_NOT_COMPLETE), "未完成审核的数据无法被删除");
            }
        });

        int deleteResult = teacherCensorMapper.deleteBatchIds(ids);

        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "用户注册申请数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }
}
