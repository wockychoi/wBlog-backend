package com.mapago.mapper.dashboard;

import com.mapago.model.dashboard.Dashboard;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DashboardMapper {
    List<Dashboard> getTopLikeList(Dashboard dashboard);

    List<Dashboard> getTopViewList(Dashboard dashboard);

    List<Dashboard> getTopUserLoginList(Dashboard dashboard);

    List<Dashboard> getTopLogList(Dashboard dashboard);

    List<Dashboard> getTotalLoginUser(Dashboard dashboard);

    List<Dashboard> getRgstShop(Dashboard dashboard);

    List<Dashboard> getTypePercentage(Dashboard dashboard);


}