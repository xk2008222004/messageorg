package springbootdubbo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@Slf4j
public class RabbitMqConfig {

    @Bean
    public Queue testDirectQueue(){
        return new Queue("TestDirectQueue",true);
    }

    @Bean
    public Queue orderQueue(){
        return new Queue("orderQueue",false);
    }

    @Bean
    public DirectExchange orderExchange(){
        return new DirectExchange("orderExchange",true,false);
    }


    @Bean
    public DirectExchange testDirectExchange(){
        return new DirectExchange("TestDirectExchange",true,false);
    }

    @Bean
    Binding bindingDirect(){
        return BindingBuilder.bind(testDirectQueue()).to(testDirectExchange()).with("TestDirectRouting");
    }

    @Bean
    Binding bindingOrderDirect(){
        return BindingBuilder.bind(orderQueue()).to(orderExchange()).with("orderRouting");
    }

    @Bean
    public DirectExchange lonelyExchange(){
        return new DirectExchange("lonelyExchange",true,false);
    }

}
