package com.mapago.service.commonCode;

import com.mapago.config.exception.CustomException;
import com.mapago.mapper.commonCode.CommonCodeMapper;
import com.mapago.model.commonCode.CommonCode;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonCodeService {

    private final CommonCodeMapper commonCodeMapper;

    public CommonCodeService(CommonCodeMapper commonCodeMapper) {
        this.commonCodeMapper = commonCodeMapper;
    }

    public List<CommonCode> getCommonCodeList(CommonCode commonCode) throws Exception {
        return commonCodeMapper.findCommonCodeList(commonCode);
    }

    @Transactional
    public CommonCode insertCommonCode(CommonCode commonCode) throws Exception {
        commonCodeMapper.insertCommonCode(commonCode);
        return commonCode;
    }

    @Transactional
    public CommonCode updateCommonCode(CommonCode commonCode) throws Exception {
        int result = commonCodeMapper.updateCommonCode(commonCode);
        return Optional.ofNullable(commonCode)
                .filter(t -> result > 0)
                .orElseThrow(() -> new CustomException("수정할 데이터가 없습니다."));
    }

    @Transactional
    public Integer deleteCommonCode(CommonCode commonCode) throws Exception {
        return Optional.of(commonCodeMapper.deleteCommonCode(commonCode))
                .filter(result -> result > 0)
                .orElseThrow(() -> new CustomException("삭제할 데이터가 없습니다."));
    }

    @Transactional
    public Integer sortCommonCodeList(List<CommonCode> commonCodeList) throws Exception {
        int totalUpdated = commonCodeList.stream()
                .mapToInt(commonCodeMapper::sortCommonCode)
                .sum();

        if (totalUpdated <= 0) {
            throw new CustomException("정렬할 데이터가 없습니다.");
        }
        return totalUpdated;
    }
}