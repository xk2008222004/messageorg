package springbootdubbo.scheduler;


import com.example.springbootdubbo.dict.ParamDict;
import com.example.springbootdubbo.po.Message;
import com.example.springbootdubbo.service.MessageService;
import com.example.springbootdubbo.vo.MessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import springbootdubbo.mapper.MessageMapper;

import java.util.ArrayList;
import java.util.List;

@Component
public class TimeScheduler {
    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageMapper messageMapper;

    @Value(value="${scheduler.repeat.time}")
    private Integer time;

    @Scheduled(cron = "0 0/1 * * * ?")//每隔1分钟执行1次
    public void scheduler(){
        List<Integer> list = new ArrayList();
        list.add(ParamDict.MessageState.SENDFINISH.getCode());
        list.add(ParamDict.MessageState.SENDFAIL.getCode());
        MessageVo messageVo = new MessageVo();
        messageVo.setUpdateMessageState(false);
        messageVo.setStateList(list);
        messageService.sendMessage(messageVo);
    }

    @Scheduled(cron = "0 0/50 * * * ?")//每隔5分钟执行1次
    public void scheduler2(){
        Message message = new Message();
        message.setState(String.valueOf(ParamDict.MessageState.SENDFINISH));
        message.setFixCount(time);
        List<Message> messages = messageMapper.queryForSendAdministrator(message);
        //TODO
        //根据消息查询订单对应的用户，发送邮件或短信通知工作人员
    }

}
