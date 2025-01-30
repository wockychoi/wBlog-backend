package com.mapago.mapper.commonCode;

import com.mapago.model.commonCode.CommonCode;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommonCodeMapper {
    List<CommonCode> findCommonCodeList(CommonCode commonCode);

    void insertCommonCode(CommonCode commonCode);

    Integer updateCommonCode(CommonCode commonCode);

    Integer deleteCommonCode(CommonCode commonCode);

    Integer sortCommonCode(CommonCode commonCode);

}