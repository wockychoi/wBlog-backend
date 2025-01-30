package com.mapago.service.type;

import com.mapago.config.exception.CustomException;
import com.mapago.mapper.type.TypeMapper;
import com.mapago.model.type.Type;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TypeService {

    private final TypeMapper typeMapper;

    public TypeService(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    public List<Type> findTypeList(Type type) throws Exception {
        return typeMapper.findTypeList(type);
    }

    public List<Type> findTypeListWithInfos() throws Exception {
        return typeMapper.findTypeListWithInfos();
    }

    @Transactional
    public Type insertType(Type type) throws Exception {
        typeMapper.insertType(type);
        return type;
    }

    @Transactional
    public Type updateType(Type type) throws Exception {
        int result = typeMapper.updateType(type);
        return Optional.ofNullable(type)
                .filter(t -> result > 0)
                .orElseThrow(() -> new CustomException("수정할 데이터가 없습니다."));
    }

    @Transactional
    public Integer deleteType(Type type) throws Exception {
        return Optional.of(typeMapper.deleteType(type))
                .filter(result -> result > 0)
                .orElseThrow(() -> new CustomException("삭제할 데이터가 없습니다."));
    }

    @Transactional
    public Integer sortTypeList(List<Type> typeList) throws Exception {
        int totalUpdated = typeList.stream()
                .mapToInt(typeMapper::sortType)
                .sum();

        if (totalUpdated <= 0) {
            throw new CustomException("정렬할 데이터가 없습니다.");
        }
        return totalUpdated;
    }
}