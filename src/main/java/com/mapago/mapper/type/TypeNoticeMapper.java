package com.mapago.mapper.type;

import com.mapago.model.type.TypeNotice;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TypeNoticeMapper {

    List<TypeNotice> findTypeNoticeList(TypeNotice typeNotice);

    void insertTypeNotice(TypeNotice typeNotice);

    Integer updateTypeNotice(TypeNotice typeNotice);

    Integer deleteTypeNotice(TypeNotice typeNotice);

    TypeNotice findTypeNotice(TypeNotice typeNotice);
}