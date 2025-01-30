package com.mapago.controller.admin.dashboard;

import com.mapago.model.dashboard.Dashboard;
import com.mapago.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/api/dashboard")
public class AdmDashController {

    @Autowired
    private DashboardService dashboardService;

    //전체 좋아요 랭킹
    @GetMapping("/likelist")
    public ResponseEntity<?> likelist(Dashboard dashboard) throws Exception {
        return ResponseEntity.ok(dashboardService.getTopLikeList(dashboard));
    }

    //전체 좋아요 랭킹
    @GetMapping("/viewlist")
    public ResponseEntity<?> viewlist(Dashboard dashboard) throws Exception {
        return ResponseEntity.ok(dashboardService.getTopViewList(dashboard));
    }

    //전체 사용자 접속자 랭킹
    @GetMapping("/userLoginglist")
    public ResponseEntity<?> userLoginglist(Dashboard dashboard) throws Exception {
        return ResponseEntity.ok(dashboardService.getTopUserLoginList(dashboard));
    }

    //로그 랭킹(일간,주간,월간)
    @GetMapping("/toploglist")
    public ResponseEntity<?> toploglist(Dashboard dashboard) throws Exception {
        return ResponseEntity.ok(dashboardService.getTopLogList(dashboard));
    }

    //월간 로그인 유저수 확인
    @GetMapping("/totalLoginUser")
    public ResponseEntity<?> totalLoginUser(Dashboard dashboard) throws Exception {
        return ResponseEntity.ok(dashboardService.getTotalLoginUser(dashboard));
    }

    //샵 최신 등록
    @GetMapping("/rgstShop")
    public ResponseEntity<?> getRgstShop(Dashboard dashboard) throws Exception {
        return ResponseEntity.ok(dashboardService.getRgstShop(dashboard));
    }

    //타입 비율별
    @GetMapping("/typePercentage")
    public ResponseEntity<?> getTypePercentage(Dashboard dashboard) throws Exception {
        return ResponseEntity.ok(dashboardService.getTypePercentage(dashboard));
    }


}
