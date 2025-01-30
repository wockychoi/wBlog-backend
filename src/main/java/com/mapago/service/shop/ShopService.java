package com.mapago.service.shop;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mapago.config.exception.CustomException;
import com.mapago.mapper.shop.ShopMapper;
import com.mapago.model.shop.Shop;
import com.mapago.service.file.FileService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ShopService {

    @Autowired
    private FileService fileService;

    private final ShopMapper shopMapper;

    public ShopService(ShopMapper shopMapper) {
        this.shopMapper = shopMapper;
    }

    public PageInfo<Shop> findShopList(Shop shop) {
        PageHelper.startPage(shop.getPageNum(), shop.getPageSize());
        List<Shop> shopList = shopMapper.findShopList(shop);
        return new PageInfo<>(shopList);
    }

    public List<Shop> findShopListAll() {
        return shopMapper.findShopListAll();
    }

    public Shop findShop(Shop shop) {
        return shopMapper.findShop(shop);
    }

    @Transactional
    public Shop insertShop(Shop shop, List<MultipartFile> files) throws Exception {
        shopMapper.insertShop(shop);
        if (files != null && !files.isEmpty()) {
            List<String> data = fileService.saveFiles(files, "shop", String.valueOf(shop.getShopId()));
            shop.setFileIdList(String.join(",", data));
        }
        shopMapper.updateShopFileIdList(shop);
        return shop;
    }

    @Transactional
    public Shop updateShop(Shop shop, List<MultipartFile> files) throws Exception {
        if (files != null && !files.isEmpty()) {
            List<String> data = fileService.saveFiles(files, "shop", String.valueOf(shop.getShopId()));
            shop.setFileIdList(
                    Stream.concat(
                            shop.getFileList().stream()
                                    .map(file -> String.valueOf(file.getFileId())),
                            data.stream()
                    ).collect(Collectors.joining(","))
            );
        } else {
            shop.setFileIdList(
                    shop.getFileList().stream()
                            .map(file -> String.valueOf(file.getFileId()))
                            .collect(Collectors.joining(","))
            );
        }

        int result = shopMapper.updateShop(shop);
        return Optional.ofNullable(shop)
                .filter(t -> result > 0)
                .orElseThrow(() -> new CustomException("수정할 데이터가 없습니다."));
    }

    @Transactional
    public Integer deleteShop(Shop shop) throws Exception {
        return Optional.of(shopMapper.deleteShop(shop))
                .filter(result -> result > 0)
                .orElseThrow(() -> new CustomException("삭제할 데이터가 없습니다."));
    }

    @Transactional
    public Integer sortShopFileList(Shop shop) throws Exception {
        return Optional.of(shopMapper.sortShopFileList(shop))
                .filter(result -> result > 0)
                .orElseThrow(() -> new CustomException("정렬할 데이터가 없습니다."));
    }

    public List<Shop> findOwnerShopList(String ownerUserId) {
        return shopMapper.findOwnerShopList(ownerUserId);
    }

    @Transactional
    public Integer updateRefreshDt(Shop shop) {
        return Optional.of(shopMapper.updateRefreshDt(shop))
                .filter(result -> result > 0)
                .orElseThrow(() -> new CustomException("갱신할 데이터가 없습니다."));
    }
}