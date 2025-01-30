package com.mapago.mapper.partnership;

import com.mapago.model.partnership.Partnership;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PartnershipMapper {

    void insertPartnership(Partnership partnership);

    Integer updatePartnershipFiles(Partnership partnership);

    Integer updatePartnershipYn(Partnership partnership);
}