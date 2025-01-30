package com.mapago.service.type;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mapago.config.exception.CustomException;
import com.mapago.mapper.type.TypeNoticeMapper;
import com.mapago.model.type.TypeNotice;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TypeNoticeService {

    private final TypeNoticeMapper typeNoticeMapper;

    public TypeNoticeService(TypeNoticeMapper typeNoticeMapper) {
        this.typeNoticeMapper = typeNoticeMapper;
    }

    public PageInfo<TypeNotice> findTypeNoticeList(TypeNotice typeNotice) throws Exception {
        PageHelper.startPage(typeNotice.getPageNum(), typeNotice.getPageSize());
        List<TypeNotice> shopList = typeNoticeMapper.findTypeNoticeList(typeNotice);
        return new PageInfo<>(shopList);
    }

    @Transactional
    public TypeNotice insertTypeNotice(TypeNotice typeNotice) throws Exception {
        typeNoticeMapper.insertTypeNotice(typeNotice);
        return typeNotice;
    }

    @Transactional
    public TypeNotice updateTypeNotice(TypeNotice typeNotice) throws Exception {
        int result = typeNoticeMapper.updateTypeNotice(typeNotice);
        return Optional.ofNullable(typeNotice)
                .filter(t -> result > 0)
                .orElseThrow(() -> new CustomException("수정할 데이터가 없습니다."));
    }

    @Transactional
    public Integer deleteTypeNotice(TypeNotice typeNotice) throws Exception {
        return Optional.of(typeNoticeMapper.deleteTypeNotice(typeNotice))
                .filter(result -> result > 0)
                .orElseThrow(() -> new CustomException("삭제할 데이터가 없습니다."));
    }
    
    public TypeNotice findTypeNotice(TypeNotice typeNotice) {
        return typeNoticeMapper.findTypeNotice(typeNotice);
    }
}