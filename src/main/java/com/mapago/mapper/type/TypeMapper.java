package com.mapago.mapper.type;

import com.mapago.model.type.Type;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TypeMapper {
    List<Type> findTypeList(Type type);

    List<Type> findTypeListWithInfos();

    void insertType(Type type);

    Integer updateType(Type type);

    Integer deleteType(Type type);

    Integer sortType(Type type);

}