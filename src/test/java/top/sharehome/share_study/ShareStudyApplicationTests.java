package top.sharehome.share_study;

import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.share_study.common.constant.CommonConstant;
import top.sharehome.share_study.common.exception_handler.customize.CustomizeReturnException;
import top.sharehome.share_study.common.response.R;
import top.sharehome.share_study.common.response.RCodeEnum;
import top.sharehome.share_study.config.RabbitMqConfig;

import java.util.HashMap;

@SpringBootTest
class ShareStudyApplicationTests {
    @javax.annotation.Resource(name = "noSingletonRabbitTemplate")
    private RabbitTemplate noSingletonRabbitTemplate;

    @Test
    void contextLoads() {
        String to = "1911261716@qq.com";
        String subject = "基于区块链的资源共享平台申请反馈";
        String content = "";
        if (StringUtils.isEmpty(content)) {
            content = "无";
        }
        HashMap<String, Object> rabbitMqResult = new HashMap<>();
        rabbitMqResult.put("to", to);
        rabbitMqResult.put("subject", subject);
        rabbitMqResult.put("content", "<h1>基于区块链的资源共享平台</h1><h2>您的注册申请已经通过，欢迎您的加入！</h2><p><b>补充说明:</b>" + content + "</p>");
        //rabbitMqResult.put("content","<h1>基于区块链的资源共享平台</h1><h2>很抱歉，您的注册申请未能通过！</h2><p><b>补充说明:</b>" + content + "</p>");
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
            for (int i = 0; i < 5; i++) {
                noSingletonRabbitTemplate.convertAndSend(RabbitMqConfig.EXCHANGE_NAME, "mail." + CommonConstant.SEND_REGISTRATION_FEEDBACK_EMAIL, JSON.toJSONString(rabbitMqResult));
            }
        } catch (AmqpException exception) {
            throw new CustomizeReturnException(R.failure(RCodeEnum.MESSAGE_QUEUE_SEND_ERROR));
        }
    }
}
