package com.mapago.controller.admin.banner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mapago.model.banner.Banner;
import com.mapago.service.banner.BannerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/banner")
public class AdmBannerController {

    @Autowired
    private BannerService bannerService;

    @GetMapping("/list")
    public ResponseEntity<?> list(Banner banner) throws Exception {
        return ResponseEntity.ok(bannerService.getBannerList(banner));
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestPart(value = "files", required = true) List<MultipartFile> files,
                                  @RequestPart(value = "shop", required = true) String data) throws Exception {

        Banner banner = new ObjectMapper().readValue(data, Banner.class);
        return ResponseEntity.ok(bannerService.insertBanner(banner, files));

    }

    @PostMapping("/remove")
    public ResponseEntity<?> remove(@RequestBody Banner banner) throws Exception {
        return ResponseEntity.ok(bannerService.deleteBanner(banner));
    }

    @PostMapping("/modify")
    public ResponseEntity<?> modify(@RequestPart("shop") String data,
                                    @RequestPart(value = "files", required = false) List<MultipartFile> files)
            throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Banner banner = new ObjectMapper().readValue(data, Banner.class);

        return ResponseEntity.ok(bannerService.updateBanner(banner, files));
    }

}
