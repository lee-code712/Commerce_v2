package com.digital.v3.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.digital.v3.sql.mapper.CategoryMapper;
import com.digital.v3.sql.vo.CategoryVO;

@Repository
public class CategoryDao {
	
	@Autowired
	CategoryMapper categoryMapper;

	public void createCategory(CategoryVO categoryVo) {
		categoryMapper.createCategory(categoryVo);
	}

	public CategoryVO getCategoryByName(String categoryName) {
		return categoryMapper.getCategoryByName(categoryName);
	}

}
