package top.sharehome.share_study.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * RabbitMQ 配置类
 *
 * @author AntonyCheng
 */
@Configuration
public class RabbitMqConfig {
    public static final String EXCHANGE_NAME = "shareStudyExchange";
    public static final String RESOURCE_QUEUE = "resourceQueue";
    public static final String COMMENT_QUEUE = "commentQueue";
    public static final String MAIL_QUEUE = "mailQueue";

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate noSingletonRabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        //设置消息进入交换机后未被队列接收的消息不被丢弃由broker保存,false为丢弃
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReceiveTimeout(30000);
        rabbitTemplate.setReplyTimeout(30000);
        return rabbitTemplate;
    }

    /**
     * 定义交换机
     */
    @Bean
    public Exchange shareStudyExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false, null);
    }

    /**
     * 定义资源队列
     */
    @Bean
    public Queue resourceQueue(ConnectionFactory connectionFactory) {
        Queue queue = new Queue(RESOURCE_QUEUE, true, false, false, null);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    /**
     * 定义评论队列
     */
    @Bean
    public Queue commentQueue(ConnectionFactory connectionFactory) {
        Queue queue = new Queue(COMMENT_QUEUE, true, false, false, null);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    /**
     * 定义邮件队列
     */
    @Bean
    public Queue mailQueue(ConnectionFactory connectionFactory) {
        Queue queue = new Queue(MAIL_QUEUE, true, false, false, null);
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    /**
     * 交换机绑定资源队列
     *
     * @param resourceQueue  对应的队列
     * @param topicsExchange 对应的交换机
     * @return 返回绑定对象
     */
    @Bean
    public Binding resourceBinding(Queue resourceQueue, Exchange topicsExchange) {
        return BindingBuilder
                .bind(resourceQueue)
                .to(topicsExchange)
                .with("resource.*")
                .noargs();
    }

    /**
     * 交换机绑定评论队列
     *
     * @param commentQueue   对应的队列
     * @param topicsExchange 对应的交换机
     * @return 返回绑定对象
     */
    @Bean
    public Binding commentBinding(Queue commentQueue, Exchange topicsExchange) {
        return BindingBuilder
                .bind(commentQueue)
                .to(topicsExchange)
                .with("comment.*")
                .noargs();
    }

    /**
     * 交换机绑定邮件队列
     *
     * @param mailQueue      对应的队列
     * @param topicsExchange 对应的交换机
     * @return 返回绑定对象
     */
    @Bean
    public Binding mailBinding(Queue mailQueue, Exchange topicsExchange) {
        return BindingBuilder
                .bind(mailQueue)
                .to(topicsExchange)
                .with("mail.*")
                .noargs();
    }
}
