package com.digital.v3.sql.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.digital.v3.sql.vo.ProductVO;

@Mapper
public interface ProductMapper {

	public void createProduct(ProductVO productVo);
	
	public ProductVO getProductByName(String productName);
	
	public List<ProductVO> getProductByKeyword(String keyword);
	
	public List<ProductVO> getProductByCategory(String categoryName);
	
}
