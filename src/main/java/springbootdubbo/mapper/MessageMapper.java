package springbootdubbo.mapper;

import com.example.springbootdubbo.po.Message;
import com.example.springbootdubbo.vo.MessageVo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageMapper {

    Message selectOne(Message message);


    int insertModel(Message message);


    int delModel(Message message);

    int updateModel(Message message);


    int updateBatch(MessageVo messageVo);


    List<Message> queryList(MessageVo messageVo);

    List<Message> queryForSendAdministrator(Message message);
}
