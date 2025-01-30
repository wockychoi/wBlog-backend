package com.mapago.service.dashboard;

import com.mapago.mapper.dashboard.DashboardMapper;
import com.mapago.model.dashboard.Dashboard;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final DashboardMapper dashboardMapper;

    public DashboardService(DashboardMapper dashboardMapper) {
        this.dashboardMapper = dashboardMapper;
    }

    public List<Dashboard> getTopLikeList(Dashboard dashboard) throws Exception {
        return dashboardMapper.getTopLikeList(dashboard);
    }

    public List<Dashboard> getTopUserLoginList(Dashboard dashboard) throws Exception {
        return dashboardMapper.getTopUserLoginList(dashboard);
    }

    public List<Dashboard> getTopViewList(Dashboard dashboard) throws Exception {
        return dashboardMapper.getTopViewList(dashboard);
    }

    public List<Dashboard> getTopLogList(Dashboard dashboard) throws Exception {
        return dashboardMapper.getTopLogList(dashboard);
    }

    public List<Dashboard> getTotalLoginUser(Dashboard dashboard) throws Exception {
        return dashboardMapper.getTotalLoginUser(dashboard);
    }

    public List<Dashboard> getRgstShop(Dashboard dashboard) throws Exception {
        return dashboardMapper.getRgstShop(dashboard);
    }

    public List<Dashboard> getTypePercentage(Dashboard dashboard) throws Exception {
        return dashboardMapper.getTypePercentage(dashboard);
    }

}