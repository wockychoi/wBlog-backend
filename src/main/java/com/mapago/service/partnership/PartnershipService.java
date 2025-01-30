package com.mapago.service.partnership;

import com.mapago.config.exception.CustomException;
import com.mapago.mapper.partnership.PartnershipMapper;
import com.mapago.model.partnership.Partnership;
import com.mapago.model.user.User;
import com.mapago.service.file.FileService;
import com.mapago.service.user.UserService;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PartnershipService {

    @Autowired
    private PartnershipMapper partnershipMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private FileService fileService;

    @Transactional
    public Partnership insertUserPartnership(User userRequest, Partnership partnership, List<MultipartFile> imageFiles,
                                             List<MultipartFile> bizLicFile) throws Exception {
        userService.insertUser(userRequest);
        userService.insertUserRole(userRequest);
        partnershipMapper.insertPartnership(partnership);
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<String> data = fileService.saveFiles(imageFiles, "partnership",
                    String.valueOf(partnership.getUserId()));
            partnership.setFileIdList(String.join(",", data));
        }
        if (bizLicFile != null && !bizLicFile.isEmpty()) {
            List<String> data = fileService.saveFiles(bizLicFile, "partnership",
                    String.valueOf(partnership.getUserId()));
            partnership.setBizLicFileId(String.join(",", data));
        }

        partnershipMapper.updatePartnershipFiles(partnership);
        return partnership;
    }

    @Transactional
    public Partnership updatePartnership(Partnership partnership) throws Exception {
        int result = partnershipMapper.updatePartnershipYn(partnership);
        return Optional.ofNullable(partnership)
                .filter(t -> result > 0)
                .orElseThrow(() -> new CustomException("수정할 데이터가 없습니다."));
    }


}