package com.digital.v3.sql.vo;

import lombok.Data;

@Data
public class ProductVO {

	private long productId;
	private String productName;
	private long price;
	private CategoryVO categoryVo;
	private long quantity;
	
	public void setProductId(long productId) {
		this.productId = productId;
	}
	
	public long getProductId() {
		long productId = this.productId;
		return productId;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	public String getProductName() {
		String productName = this.productName;
		return productName;
	}

	public void setPrice(long price) {
		this.price = price;
	}
	
	public long getPrice() {
		long price = this.price;
		return price;
	}

	public void setCategoryVo(CategoryVO categoryVo) {
		this.categoryVo = categoryVo;
	}
	
	public CategoryVO getCategoryVo() {
		CategoryVO categoryVo = this.categoryVo;
		return categoryVo;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public long getQuantity() {
		long quantity = this.quantity;
		return quantity;
	}
	
}
