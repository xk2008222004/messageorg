package springbootdubbo.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.example.springbootdubbo.dict.ParamDict;
import com.example.springbootdubbo.po.Message;
import com.example.springbootdubbo.po.ResultObject;
import com.example.springbootdubbo.service.MessageService;
import com.example.springbootdubbo.service.RabbitMqService;
import com.example.springbootdubbo.vo.MessageVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import springbootdubbo.mapper.MessageMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = MessageService.class,version = "1.0.0",timeout = 1500)
@Repository
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value(value="${scheduler.repeat.time}")
    private Integer time;

    @Override
    public ResultObject queryMessage(MessageVo messageVo) {
        Message message = new Message();
        message.setMessageId(messageVo.getMessageId());
        message = messageMapper.selectOne(message);
        return ResultObject.success(message);
    }

    @Override
    public ResultObject createMessage(Message message) {
        return ResultObject.success(messageMapper.insertModel(message));
    }

    @Override
    public ResultObject delMessage(Message message) {
        return ResultObject.success(messageMapper.delModel(message));
    }

    @Override
    public ResultObject updateMessage(Message message) {
        return ResultObject.success(messageMapper.updateModel(message));
    }

    @Override
    public ResultObject sendMessage(MessageVo messageVo) {
        boolean flag = messageVo.getUpdateMessageState();
        Map<String,Object> map = new HashMap<>();
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        if(flag){
            Message message = new Message();
            message.setMessageId(messageVo.getMessageId());
            message.setState(String.valueOf(messageVo.getStateList().get(0)));
            this.updateMessage(message);
            map.put("messageId",messageVo.getMessageId());
            map.put("messageData",messageVo.getData());
            map.put("createTime",createTime);
            rabbitTemplate.convertAndSend(ParamDict.ORDEREXCHANGE,ParamDict.ORDERROUTING,map);
        }else{
            messageVo.setFixCount(time);
            int result = messageMapper.updateBatch(messageVo);
            log.info("修改了"+result+"行数据");
            List<Message> list = messageMapper.queryList(messageVo);
            if(!CollectionUtils.isEmpty(list)){
                list.forEach(message -> {
                    map.put("messageId",message.getMessageId());
                    map.put("messageData",message.getData());
                    rabbitTemplate.convertAndSend(ParamDict.ORDEREXCHANGE,ParamDict.ORDERROUTING,map);
                });
            }else{
                log.info("没有要rabbitmq发送的数据");
            }
        }
        return ResultObject.success("ok");
    }
}
