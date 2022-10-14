package com.digital.v3.sql.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.digital.v3.sql.vo.CategoryVO;

@Mapper
public interface CategoryMapper {

	public void createCategory(CategoryVO categoryVo);
	
	public CategoryVO getCategoryByName(String categoryName);
	
}
