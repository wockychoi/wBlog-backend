package com.mapago.mapper.banner;

import com.mapago.model.banner.Banner;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BannerMapper {

    List<Banner> findBannerList(Banner banner);

    void insertBanner(Banner banner);

    Integer updateBanner(Banner banner);

    Integer deleteBanner(Banner banner);

    Integer sortBanner(Banner banner);

    void updateBannerFileId(Banner banner);

}