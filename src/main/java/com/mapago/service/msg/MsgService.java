package com.mapago.service.msg;

import com.mapago.config.exception.CustomException;
import com.mapago.mapper.msg.MsgMapper;
import com.mapago.model.msg.Msg;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MsgService {

    private final MsgMapper msgMapper;

    public MsgService(MsgMapper msgMapper) {
        this.msgMapper = msgMapper;
    }

    public List<Msg> getMsgList(Msg msg) throws Exception {
        return msgMapper.findMsgList(msg);
    }

    @Transactional
    public Msg insertMsg(Msg msg) throws Exception {
        msgMapper.insertMsg(msg);
        return msg;
    }

    @Transactional
    public Integer deleteMsg(Msg msg) throws Exception {
        return Optional.of(msgMapper.deleteMsg(msg))
                .filter(result -> result > 0)
                .orElseThrow(() -> new CustomException("삭제할 데이터가 없습니다."));
    }

}