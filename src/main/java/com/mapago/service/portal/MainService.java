package com.mapago.service.portal;

import com.mapago.mapper.portal.MainMapper;
import com.mapago.service.category.CategoryService;
import com.mapago.service.commonCode.CommonCodeService;
import com.mapago.service.shop.ShopService;
import com.mapago.service.type.TypeService;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MainService {

    @Autowired
    private MainMapper mainMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private ShopService shopService;

    @Autowired
    private CommonCodeService commonCodeService;

    public Map<String, Object> getData() throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("categoryTree", mainMapper.findCategoryTree());
        return null;
    }

}