package com.mapago.service.type;

import com.mapago.config.exception.CustomException;
import com.mapago.mapper.type.TypeDetailMapper;
import com.mapago.model.type.TypeDetail;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TypeDetailService {

    private final TypeDetailMapper typeDetailMapper;

    public TypeDetailService(TypeDetailMapper typeDetailMapper) {
        this.typeDetailMapper = typeDetailMapper;
    }

    public List<TypeDetail> findTypeDetailList(TypeDetail typeDetail) throws Exception {
        return typeDetailMapper.findTypeDetailList(typeDetail);
    }

    public List<TypeDetail> findTypeDetailListWithInfos() throws Exception {
        return typeDetailMapper.findTypeDetailListWithInfos();
    }

    @Transactional
    public TypeDetail insertTypeDetail(TypeDetail typeDetail) throws Exception {
        typeDetailMapper.insertTypeDetail(typeDetail);
        return typeDetail;
    }

    @Transactional
    public TypeDetail updateTypeDetail(TypeDetail typeDetail) throws Exception {
        int result = typeDetailMapper.updateTypeDetail(typeDetail);
        return Optional.ofNullable(typeDetail)
                .filter(t -> result > 0)
                .orElseThrow(() -> new CustomException("수정할 데이터가 없습니다."));
    }

    @Transactional
    public Integer deleteTypeDetail(TypeDetail typeDetail) throws Exception {
        return Optional.of(typeDetailMapper.deleteTypeDetail(typeDetail))
                .filter(result -> result > 0)
                .orElseThrow(() -> new CustomException("삭제할 데이터가 없습니다."));
    }

    @Transactional
    public Integer sortTypeDetailList(List<TypeDetail> typeDetailList) throws Exception {
        int totalUpdated = typeDetailList.stream()
                .mapToInt(typeDetailMapper::sortTypeDetail)
                .sum();

        if (totalUpdated <= 0) {
            throw new CustomException("정렬할 데이터가 없습니다.");
        }
        return totalUpdated;
    }
}