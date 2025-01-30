package com.mapago.mapper.msg;

import com.mapago.model.msg.Msg;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MsgMapper {
    List<Msg> findMsgList(Msg msg);

    void insertMsg(Msg msg);

    Integer deleteMsg(Msg msg);


}