package com.mapago.service.banner;

import com.mapago.config.exception.CustomException;
import com.mapago.mapper.banner.BannerMapper;
import com.mapago.model.banner.Banner;
import com.mapago.service.file.FileService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BannerService {

    @Autowired
    private BannerMapper bannerMapper;
    @Autowired
    private FileService fileService;

    public List<Banner> getBannerList(Banner banner) throws Exception {
        return bannerMapper.findBannerList(banner);
    }

    @Transactional
    public Banner insertBanner2(Banner banner, List<MultipartFile> bannerFiles) throws Exception {
        bannerMapper.insertBanner(banner);
        return banner;
    }

    @Transactional
    public Banner insertBanner(Banner banner, List<MultipartFile> files) throws Exception {
        bannerMapper.insertBanner(banner);
        if (files != null && !files.isEmpty()) {
            List<String> data = fileService.saveFiles(files, "banner", String.valueOf(banner.getBannerId()));
            banner.setFileId(String.join(",", data));
        }
        bannerMapper.updateBannerFileId(banner);
        return banner;
    }

    @Transactional
    public Banner updateBanner2(Banner banner) throws Exception {
        int result = bannerMapper.updateBanner(banner);
        return Optional.ofNullable(banner)
                .filter(t -> result > 0)
                .orElseThrow(() -> new CustomException("수정할 데이터가 없습니다."));
    }

    @Transactional
    public Banner updateBanner(Banner banner, List<MultipartFile> files) throws Exception {
        if (files != null && !files.isEmpty()) {
            List<String> data = fileService.saveFiles(files, "banner", String.valueOf(banner.getBannerId()));
            banner.setFileId(data.stream().collect(
                    Collectors.joining(",", banner.getFileId() + (banner.getFileId().isEmpty() ? "" : ","), ""))
            );
        }
        int result = bannerMapper.updateBanner(banner);
        return Optional.ofNullable(banner)
                .filter(t -> result > 0)
                .orElseThrow(() -> new CustomException("수정할 데이터가 없습니다."));
    }


    @Transactional
    public Integer deleteBanner(Banner banner) throws Exception {
        return Optional.of(bannerMapper.deleteBanner(banner))
                .filter(result -> result > 0)
                .orElseThrow(() -> new CustomException("삭제할 데이터가 없습니다."));
    }

    @Transactional
    public Integer sortBannerList(List<Banner> bannerList) throws Exception {
        int totalUpdated = bannerList.stream()
                .mapToInt(bannerMapper::sortBanner)
                .sum();

        if (totalUpdated <= 0) {
            throw new CustomException("정렬할 데이터가 없습니다.");
        }
        return totalUpdated;
    }

}