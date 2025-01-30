package com.mapago.mapper.portal;

import com.mapago.model.category.Category;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MainMapper {

    List<Category> findCategoryTree();

}