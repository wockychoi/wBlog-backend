package com.mapago.mapper.type;

import com.mapago.model.type.TypeDetail;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TypeDetailMapper {
    List<TypeDetail> findTypeDetailList(TypeDetail typeDetail);

    List<TypeDetail> findTypeDetailListWithInfos();

    void insertTypeDetail(TypeDetail typeDetail);

    Integer updateTypeDetail(TypeDetail typeDetail);

    Integer deleteTypeDetail(TypeDetail typeDetail);

    Integer sortTypeDetail(TypeDetail typeDetail);

}