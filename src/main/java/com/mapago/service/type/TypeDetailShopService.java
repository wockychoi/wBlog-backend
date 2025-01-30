package com.mapago.service.type;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mapago.config.exception.CustomException;
import com.mapago.mapper.type.TypeDetailShopMapper;
import com.mapago.model.type.TypeDetailShop;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TypeDetailShopService {

    private final TypeDetailShopMapper typeDetailShopMapper;

    public TypeDetailShopService(TypeDetailShopMapper typeDetailShopMapper) {
        this.typeDetailShopMapper = typeDetailShopMapper;
    }

    public PageInfo<TypeDetailShop> findTypeDetailShopListWithInfos(TypeDetailShop typeDetailShop) throws Exception {
        PageHelper.startPage(typeDetailShop.getPageNum(), typeDetailShop.getPageSize());
        List<TypeDetailShop> typeDetailShopList = typeDetailShopMapper.findTypeDetailShopListWithInfos(typeDetailShop);
        return new PageInfo<>(typeDetailShopList);
    }

    @Transactional
    public Integer insertTypeDetailShops(List<TypeDetailShop> typeDetailShopList) throws Exception {
        int totalUpdated = typeDetailShopList.stream()
                .mapToInt(typeDetailShopMapper::insertTypeDetailShop)
                .sum();

        if (totalUpdated <= 0) {
            throw new CustomException("등록할 데이터가 없습니다.");
        }
        return totalUpdated;
    }

    @Transactional
    public Integer deleteTypeDetailShop(TypeDetailShop typeDetailShop) throws Exception {
        return Optional.of(typeDetailShopMapper.deleteTypeDetailShop(typeDetailShop))
                .filter(result -> result > 0)
                .orElseThrow(() -> new CustomException("삭제할 데이터가 없습니다."));
    }

    @Transactional
    public Integer sortTypeDetailShopList(List<TypeDetailShop> typeDetailShopList) {
        int totalUpdated = typeDetailShopList.stream()
                .mapToInt(typeDetailShopMapper::sortTypeDetailShop)
                .sum();

        if (totalUpdated <= 0) {
            throw new CustomException("정렬할 데이터가 없습니다.");
        }
        return totalUpdated;
    }

    public PageInfo<TypeDetailShop> findDisplayShopList(TypeDetailShop typeDetailShop) throws Exception {
        PageHelper.startPage(typeDetailShop.getPageNum(), typeDetailShop.getPageSize());
        return new PageInfo<>(typeDetailShopMapper.findDisplayShopList(typeDetailShop));
    }

    public List<TypeDetailShop> findRecommendShopList(TypeDetailShop typeDetailShop) throws Exception {
        return typeDetailShopMapper.findRecommendShopList(typeDetailShop);
    }

}