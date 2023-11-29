package top.sharehome.share_study.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
import top.sharehome.share_study.mapper.*;
import top.sharehome.share_study.model.dto.resource_censor.ResourceCensorPageDto;
import top.sharehome.share_study.model.dto.teacher.TeacherLoginDto;
import top.sharehome.share_study.model.entity.*;
import top.sharehome.share_study.model.vo.post.PostAddVo;
import top.sharehome.share_study.model.vo.resource_censor.ResourceCensorPageVo;
import top.sharehome.share_study.model.vo.resource_censor.ResourceCensorUpdateVo;
import top.sharehome.share_study.service.FileOssService;
import top.sharehome.share_study.service.ResourceCensorService;
import top.sharehome.share_study.utils.object.ObjectDataUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 教学资料审核ServiceImpl
 *
 * @author AntonyCheng
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ResourceCensorServiceImpl extends ServiceImpl<ResourceCensorMapper, ResourceCensor> implements ResourceCensorService {

    @javax.annotation.Resource
    private ResourceCensorMapper resourceCensorMapper;

    @javax.annotation.Resource
    private TeacherMapper teacherMapper;

    @javax.annotation.Resource
    private CommentMapper commentMapper;

    @javax.annotation.Resource
    private FileOssService fileOssService;

    @javax.annotation.Resource
    private ResourceMapper resourceMapper;

    @javax.annotation.Resource
    private TagMapper tagMapper;

    @javax.annotation.Resource(name = "noSingletonRabbitTemplate")
    private RabbitTemplate noSingletonRabbitTemplate;

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void addResourceCensor(PostAddVo postAddVo, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.USER_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN), "登录状态为空，普通用户未登录");
        }
        if (!Objects.equals(postAddVo.getBelong(), teacherLoginDto.getId())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED));
        }

        ResourceCensor resourceCensor = new ResourceCensor();
        BeanUtils.copyProperties(postAddVo, resourceCensor, "tags");
        List<Long> tagsFromRequest = ObjectUtils.isEmpty(postAddVo.getTags()) ? new ArrayList<>() : postAddVo.getTags();
        tagsFromRequest.forEach(tagValue -> {
            if (!tagMapper.exists(new LambdaQueryWrapper<Tag>().eq(Tag::getId, tagValue))) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TAG_NOT_EXISTS));
            }
        });
        String tags = JSONUtil.toJsonStr(tagsFromRequest);
        resourceCensor.setTags(tags);

        int insertResult = resourceCensorMapper.insert(resourceCensor);
        // 判断数据库插入结果
        if (insertResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_ADDITION_FAILED), "添加教学资料失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        Resource resource = new Resource();
        BeanUtils.copyProperties(resourceCensor, resource);

        String operate = "用户:" + teacherLoginDto.getName() + "(ID=" + teacherLoginDto.getId() + ")的资料进入审核期";
        HashMap<String, Object> rabbitMqResult = new HashMap<>();
        rabbitMqResult.put("operate", operate);
        rabbitMqResult.put("object", resource);
        rabbitMqResult.put("method", CommonConstant.CREATE_RESOURCE);
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
                    log.error(returnedMessage.toString());
                    throw new CustomizeReturnException(R.failure(RCodeEnum.EXCHANGE_TO_QUEUE_ERROR));
                }
            });
            noSingletonRabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "resource." + CommonConstant.CREATE_RESOURCE, JSON.toJSONString(rabbitMqResult));
        } catch (AmqpException exception) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.MESSAGE_QUEUE_SEND_ERROR));
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Boolean updateResourceCensor(ResourceCensorUpdateVo resourceCensorUpdateVo, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN));
        }
        if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.DEFAULT_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非管理员不能进行删除操作");
        }

        ResourceCensor resultFromDatabase = resourceCensorMapper.selectById(resourceCensorUpdateVo.getId());
        if (resultFromDatabase == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "目标待审核资料不存在，不需要进行下一步操作");
        }

        Teacher targetTeacher = teacherMapper.selectById(resultFromDatabase.getBelong());
        if (targetTeacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该教学资料的老师不存在");
        }

        Boolean returnResult = false;

        if ((Objects.equals(resultFromDatabase.getCensorAdmin1Result(), CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS)
                && Objects.equals(resultFromDatabase.getCensorAdmin2Result(), CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS)
                && Objects.equals(resultFromDatabase.getCensorAdmin3Result(), CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS))
                || Objects.equals(resultFromDatabase.getCensorAdmin1Result(), CommonConstant.RESOURCE_CENSOR_OPTIONS_FAILURE)
                || Objects.equals(resultFromDatabase.getCensorAdmin2Result(), CommonConstant.RESOURCE_CENSOR_OPTIONS_FAILURE)
                || Objects.equals(resultFromDatabase.getCensorAdmin3Result(), CommonConstant.RESOURCE_CENSOR_OPTIONS_FAILURE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_OF_INFORMATION_HAS_BEEN_COMPLETED));
        }

        if (Objects.equals(resultFromDatabase.getCensorAdmin1Id(), teacherLoginDto.getId())
                || Objects.equals(resultFromDatabase.getCensorAdmin2Id(), teacherLoginDto.getId())
                || Objects.equals(resultFromDatabase.getCensorAdmin3Id(), teacherLoginDto.getId())) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_REPEAT));
        }

        if (Objects.equals(resourceCensorUpdateVo.getResult(), CommonConstant.RESOURCE_CENSOR_OPTIONS_FAILURE)) {
            if (Objects.equals(resultFromDatabase.getCensorAdmin1Result(), CommonConstant.RESOURCE_CENSOR_STATUS_WAIT)) {
                resultFromDatabase.setCensorAdmin1Id(teacherLoginDto.getId());
                resultFromDatabase.setCensorAdmin1Name(teacherLoginDto.getName());
                resultFromDatabase.setCensorAdmin1Result(CommonConstant.RESOURCE_CENSOR_OPTIONS_FAILURE);
            } else if (Objects.equals(resultFromDatabase.getCensorAdmin2Result(), CommonConstant.RESOURCE_CENSOR_STATUS_WAIT)) {
                resultFromDatabase.setCensorAdmin2Id(teacherLoginDto.getId());
                resultFromDatabase.setCensorAdmin2Name(teacherLoginDto.getName());
                resultFromDatabase.setCensorAdmin2Result(CommonConstant.RESOURCE_CENSOR_OPTIONS_FAILURE);
            } else if (Objects.equals(resultFromDatabase.getCensorAdmin3Result(), CommonConstant.RESOURCE_CENSOR_STATUS_WAIT)) {
                resultFromDatabase.setCensorAdmin3Id(teacherLoginDto.getId());
                resultFromDatabase.setCensorAdmin3Name(teacherLoginDto.getName());
                resultFromDatabase.setCensorAdmin3Result(CommonConstant.RESOURCE_CENSOR_OPTIONS_FAILURE);
            } else {
                throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_DATA_ERRORS));
            }
            resultFromDatabase.setStatus(CommonConstant.RESOURCE_CENSOR_STATUS_NOT_PASS);
            if (resultFromDatabase.getUrl() != null) {
                fileOssService.delete(resultFromDatabase.getUrl());
            }

            Comment censorComment = new Comment();
            censorComment.setResource(0L);
            censorComment.setBelong(teacherLoginDto.getId());
            censorComment.setSend(resultFromDatabase.getBelong());
            censorComment.setContent(resourceCensorUpdateVo.getReason());
            censorComment.setReadStatus(0);
            commentMapper.insert(censorComment);
        }

        if (Objects.equals(resourceCensorUpdateVo.getResult(), CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS)) {

            // 方便审核通过，超管一次审核即可
            if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
                if (Objects.equals(resultFromDatabase.getCensorAdmin1Result(), CommonConstant.RESOURCE_CENSOR_STATUS_WAIT)) {
                    resultFromDatabase.setCensorAdmin1Id(teacherLoginDto.getId());
                    resultFromDatabase.setCensorAdmin1Name(teacherLoginDto.getName());
                    resultFromDatabase.setCensorAdmin1Result(CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS);
                }
                if (Objects.equals(resultFromDatabase.getCensorAdmin2Result(), CommonConstant.RESOURCE_CENSOR_STATUS_WAIT)) {
                    resultFromDatabase.setCensorAdmin2Id(teacherLoginDto.getId());
                    resultFromDatabase.setCensorAdmin2Name(teacherLoginDto.getName());
                    resultFromDatabase.setCensorAdmin2Result(CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS);
                }
                if (Objects.equals(resultFromDatabase.getCensorAdmin3Result(), CommonConstant.RESOURCE_CENSOR_STATUS_WAIT)) {
                    resultFromDatabase.setCensorAdmin3Id(teacherLoginDto.getId());
                    resultFromDatabase.setCensorAdmin3Name(teacherLoginDto.getName());
                    resultFromDatabase.setCensorAdmin3Result(CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS);
                }
            } else {
                if (Objects.equals(resultFromDatabase.getCensorAdmin1Result(), CommonConstant.RESOURCE_CENSOR_STATUS_WAIT)) {
                    resultFromDatabase.setCensorAdmin1Id(teacherLoginDto.getId());
                    resultFromDatabase.setCensorAdmin1Name(teacherLoginDto.getName());
                    resultFromDatabase.setCensorAdmin1Result(CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS);
                } else if (Objects.equals(resultFromDatabase.getCensorAdmin2Result(), CommonConstant.RESOURCE_CENSOR_STATUS_WAIT)) {
                    resultFromDatabase.setCensorAdmin2Id(teacherLoginDto.getId());
                    resultFromDatabase.setCensorAdmin2Name(teacherLoginDto.getName());
                    resultFromDatabase.setCensorAdmin2Result(CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS);
                } else if (Objects.equals(resultFromDatabase.getCensorAdmin3Result(), CommonConstant.RESOURCE_CENSOR_STATUS_WAIT)) {
                    resultFromDatabase.setCensorAdmin3Id(teacherLoginDto.getId());
                    resultFromDatabase.setCensorAdmin3Name(teacherLoginDto.getName());
                    resultFromDatabase.setCensorAdmin3Result(CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS);
                } else {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_DATA_ERRORS));
                }
            }

            if (Objects.equals(resultFromDatabase.getCensorAdmin1Result(), CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS)
                    && Objects.equals(resultFromDatabase.getCensorAdmin2Result(), CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS)
                    && Objects.equals(resultFromDatabase.getCensorAdmin3Result(), CommonConstant.RESOURCE_CENSOR_OPTIONS_SUCCESS)) {
                resultFromDatabase.setStatus(CommonConstant.RESOURCE_CENSOR_STATUS_PASS);
            } else {
                resultFromDatabase.setStatus(CommonConstant.RESOURCE_CENSOR_STATUS_ONGOING);
            }
            returnResult = true;
        }

        int updateResult = resourceCensorMapper.updateById(resultFromDatabase);

        // 判断数据库插入结果
        if (updateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改教学资料审核失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        Resource resource = new Resource();
        BeanUtils.copyProperties(resultFromDatabase, resource);
        resource.setStatus(0);

        String operate = "管理员:" + teacherLoginDto.getName() + "(ID=" + teacherLoginDto.getId() + ")对" + resultFromDatabase.getBelong() + "的教学资料：" + resultFromDatabase.getName() + "进行了审核";
        HashMap<String, Object> rabbitMqResult = new HashMap<>();
        rabbitMqResult.put("operate", operate);
        rabbitMqResult.put("object", resource);
        rabbitMqResult.put("method", CommonConstant.UPDATE_RESOURCE);
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
                    log.error(returnedMessage.toString());
                    throw new CustomizeReturnException(R.failure(RCodeEnum.EXCHANGE_TO_QUEUE_ERROR));
                }
            });

            // 方便审核通过，超管一次审核即可，所以要多添加俩次
            if (Objects.equals(teacherLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
                for (int i = 0; i < 2; i++) {
                    noSingletonRabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "resource." + CommonConstant.UPDATE_RESOURCE, JSON.toJSONString(rabbitMqResult));
                }
            }

            noSingletonRabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "resource." + CommonConstant.UPDATE_RESOURCE, JSON.toJSONString(rabbitMqResult));
        } catch (AmqpException exception) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.MESSAGE_QUEUE_SEND_ERROR));
        }
        return returnResult;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void publishResourceCensor(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (teacherLoginDto == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.NOT_LOGIN));
        }
        if (!Objects.equals(teacherLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非超级管理员不能进行发布操作");
        }

        ResourceCensor resourceCensor = resourceCensorMapper.selectById(id);

        if (!Objects.equals(resourceCensor.getStatus(), CommonConstant.RESOURCE_CENSOR_STATUS_PASS)) {
            if (Objects.equals(resourceCensor.getStatus(), CommonConstant.RESOURCE_CENSOR_STATUS_PUBLISHED)) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_DUPLICATE_RELEASES));
            }
            throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_NOT_PASS));
        }

        Resource resource = new Resource();
        BeanUtils.copyProperties(resourceCensor, resource, "id");

        int insertResult = resourceMapper.insert(resource);

        // 判断数据库插入结果
        if (insertResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_ADDITION_FAILED), "添加教学资料失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        resourceCensor.setStatus(CommonConstant.RESOURCE_CENSOR_STATUS_PUBLISHED);
        int resourceCensorUpdateResult = resourceCensorMapper.updateById(resourceCensor);
        if (resourceCensorUpdateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改教师资料审核失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        LambdaUpdateWrapper<Teacher> teacherLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        teacherLambdaUpdateWrapper.set(Teacher::getScore, teacherMapper.selectById(resourceCensor.getBelong()).getScore() + 1).eq(Teacher::getId, resourceCensor.getBelong());
        int teacherUpdateResult = teacherMapper.update(null, teacherLambdaUpdateWrapper);

        // 判断数据库插入结果
        if (teacherUpdateResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_MODIFICATION_FAILED), "修改教师资料失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }

        String operate = "用户:" + teacherLoginDto.getName() + "(ID=" + teacherLoginDto.getId() + ")的资料通过审核，执行增加资料操作";
        HashMap<String, Object> rabbitMqResult = new HashMap<>();
        rabbitMqResult.put("operate", operate);
        rabbitMqResult.put("object", resource);
        rabbitMqResult.put("method", CommonConstant.CREATE_RESOURCE);
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
                    log.error(returnedMessage.toString());
                    throw new CustomizeReturnException(R.failure(RCodeEnum.EXCHANGE_TO_QUEUE_ERROR));
                }
            });
            noSingletonRabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "resource." + CommonConstant.CREATE_RESOURCE, JSON.toJSONString(rabbitMqResult));
        } catch (AmqpException exception) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.MESSAGE_QUEUE_SEND_ERROR));
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public Page<ResourceCensorPageDto> pageResourceCensor(Integer current, Integer pageSize, ResourceCensorPageVo resourceCensorPageVo) {
        Page<ResourceCensor> page = new Page<>(current, pageSize);
        Page<ResourceCensorPageDto> returnResult = new Page<>(current, pageSize);
        LambdaQueryWrapper<ResourceCensor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper
                .orderByAsc(ResourceCensor::getStatus)
                .orderByDesc(ResourceCensor::getCreateTime);
        if (ObjectDataUtil.isAllObjectDataEmpty(resourceCensorPageVo)) {
            this.page(page, lambdaQueryWrapper);
            BeanUtils.copyProperties(page, returnResult, "records");
            List<ResourceCensorPageDto> pageDtoList = page.getRecords().stream().map(record -> {
                ResourceCensorPageDto resourceCensorPageDto = new ResourceCensorPageDto();
                BeanUtils.copyProperties(record, resourceCensorPageDto);
                LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
                teacherLambdaQueryWrapper.eq(Teacher::getId, record.getBelong());
                Teacher teacher = teacherMapper.selectOne(teacherLambdaQueryWrapper);
                if (teacher == null) {
                    throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "教学资料所属教师不存在");
                }
                resourceCensorPageDto.setBelongName(teacher.getName());
                List<String> tagNames = JSONUtil.toList(JSONUtil.parseArray(record.getTags()), Long.class).stream().map(tagId -> {
                    return tagMapper.selectById(tagId).getName();
                }).collect(Collectors.toList());
                resourceCensorPageDto.setTags(tagNames);
                return resourceCensorPageDto;
            }).collect(Collectors.toList());
            returnResult.setRecords(pageDtoList);
            return returnResult;
        }

        lambdaQueryWrapper
                .like(!StringUtils.isEmpty(resourceCensorPageVo.getName()), ResourceCensor::getName, resourceCensorPageVo.getName())
                .like(!StringUtils.isEmpty(resourceCensorPageVo.getInfo()), ResourceCensor::getInfo, resourceCensorPageVo.getInfo())
                .like(!StringUtils.isEmpty(resourceCensorPageVo.getCensorAdmin1Name()), ResourceCensor::getCensorAdmin1Name, resourceCensorPageVo.getCensorAdmin1Name())
                .like(!StringUtils.isEmpty(resourceCensorPageVo.getCensorAdmin2Name()), ResourceCensor::getCensorAdmin2Name, resourceCensorPageVo.getCensorAdmin2Name())
                .like(!StringUtils.isEmpty(resourceCensorPageVo.getCensorAdmin3Name()), ResourceCensor::getCensorAdmin3Name, resourceCensorPageVo.getCensorAdmin3Name())
                .like(!ObjectUtils.isEmpty(resourceCensorPageVo.getStatus()), ResourceCensor::getStatus, resourceCensorPageVo.getStatus());

        String belongName = resourceCensorPageVo.getBelongName();
        List<Long> teacherIds = null;
        if (!StringUtils.isEmpty(belongName)) {
            LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teacherLambdaQueryWrapper.like(Teacher::getName, belongName);
            List<Teacher> teachers = teacherMapper.selectList(teacherLambdaQueryWrapper);
            teacherIds = teachers.stream().map(Teacher::getId).collect(Collectors.toList());
        }

        List<Long> finalTeacherIds = teacherIds;

        if (!((!Objects.isNull(finalTeacherIds) && finalTeacherIds.isEmpty()))
                && !(!Objects.isNull(resourceCensorPageVo.getTag()) && Objects.isNull(tagMapper.selectById(resourceCensorPageVo.getTag())))) {
            if (!Objects.isNull(finalTeacherIds) && !finalTeacherIds.isEmpty()) {
                lambdaQueryWrapper
                        .in(ResourceCensor::getBelong, finalTeacherIds);
            }
            lambdaQueryWrapper.like(ResourceCensor::getTags,resourceCensorPageVo.getTag());
            this.page(page, lambdaQueryWrapper);
        }

        BeanUtils.copyProperties(page, returnResult, "records");

        List<ResourceCensorPageDto> pageDtoList = page.getRecords().stream().map(record -> {
            ResourceCensorPageDto resourceCensorPageDto = new ResourceCensorPageDto();
            BeanUtils.copyProperties(record, resourceCensorPageDto);
            LambdaQueryWrapper<Teacher> teacherLambdaQueryWrapper = new LambdaQueryWrapper<>();
            teacherLambdaQueryWrapper.eq(Teacher::getId, record.getBelong());
            Teacher teacher = teacherMapper.selectOne(teacherLambdaQueryWrapper);
            if (teacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "教学资料所属教师不存在");
            }
            resourceCensorPageDto.setBelongName(teacher.getName());
            List<String> tagNames = JSONUtil.toList(JSONUtil.parseArray(record.getTags()), Long.class).stream().map(tagId -> {
                return tagMapper.selectById(tagId).getName();
            }).collect(Collectors.toList());
            resourceCensorPageDto.setTags(tagNames);
            return resourceCensorPageDto;
        }).collect(Collectors.toList());
        returnResult.setRecords(pageDtoList);
        return returnResult;
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteResourceCensor(Long id, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (!Objects.equals(teacherLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非超级管理员不能进行删除操作");
        }

        LambdaQueryWrapper<ResourceCensor> resourceCensorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        resourceCensorLambdaQueryWrapper.eq(ResourceCensor::getId, id);

        ResourceCensor selectResult = resourceCensorMapper.selectOne(resourceCensorLambdaQueryWrapper);

        if (selectResult == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "教学资料审核条目不存在，不需要进行下一步操作");
        }

        if (!(Objects.equals(selectResult.getStatus(), CommonConstant.RESOURCE_CENSOR_STATUS_NOT_PASS)
                || Objects.equals(selectResult.getStatus(), CommonConstant.RESOURCE_CENSOR_STATUS_PUBLISHED))) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_DID_NOT_COMPLETE), "未完成审核的数据无法被删除");
        }

        Teacher targetTeacher = teacherMapper.selectById(selectResult.getBelong());
        if (targetTeacher == null) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该教学资料审核条目的老师不存在");
        }

        int deleteResult = resourceCensorMapper.delete(resourceCensorLambdaQueryWrapper);

        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "教学资料审核数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

    @Override
    @Transactional(rollbackFor = CustomizeTransactionException.class)
    public void deleteBatchResourceCensor(List<Long> ids, HttpServletRequest request) {
        TeacherLoginDto teacherLoginDto = (TeacherLoginDto) request.getSession().getAttribute(CommonConstant.ADMIN_LOGIN_STATE);
        if (!Objects.equals(teacherLoginDto.getRole(), CommonConstant.SUPER_ROLE)) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.ACCESS_UNAUTHORIZED), "非超级管理员不能进行删除操作");
        }

        ids.forEach(id -> {
            ResourceCensor selectResult = resourceCensorMapper.selectById(id);
            if (selectResult == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.RESOURCE_NOT_EXISTS), "教学资料审核条目不存在，不需要进行下一步操作");
            }

            if (!(Objects.equals(selectResult.getStatus(), CommonConstant.RESOURCE_CENSOR_STATUS_NOT_PASS)
                    || Objects.equals(selectResult.getStatus(), CommonConstant.RESOURCE_CENSOR_STATUS_PUBLISHED))) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.CENSOR_DID_NOT_COMPLETE), "未完成审核的数据无法被删除");
            }

            Teacher targetTeacher = teacherMapper.selectById(selectResult.getBelong());
            if (targetTeacher == null) {
                throw new CustomizeReturnException(R.failure(RCodeEnum.TEACHER_NOT_EXISTS), "发表该教学资料审核条目的老师不存在");
            }
        });

        int deleteResult = resourceCensorMapper.deleteBatchIds(ids);

        if (deleteResult == 0) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.DATA_DELETION_FAILED), "教学资料数据删除失败，从数据库返回的影响行数为0，且在之前没有报出异常");
        }
    }

}