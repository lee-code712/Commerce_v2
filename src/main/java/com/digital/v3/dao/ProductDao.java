package com.digital.v3.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.digital.v3.sql.mapper.ProductMapper;
import com.digital.v3.sql.vo.ProductVO;

@Repository
public class ProductDao {

	@Autowired
	ProductMapper productMapper;
	
	public void createProduct(ProductVO productVo) {
		productMapper.createProduct(productVo);
	}
	
	public ProductVO getProductByName(String productName) {
		return productMapper.getProductByName(productName);
	}
	
	public List<ProductVO> getProductByKeyword(String keyword) {
		return productMapper.getProductByKeyword(keyword);
	}
	
	public List<ProductVO> getProductByCategory(String categoryName) {
		return productMapper.getProductByCategory(categoryName);
	}
	
}
