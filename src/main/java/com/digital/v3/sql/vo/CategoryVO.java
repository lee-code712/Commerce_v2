package com.digital.v3.sql.vo;

import lombok.Data;

@Data
public class CategoryVO {

	private long categoryId;
	private String categoryName;
	
	public void setCategoryId(long categoryId) {
		this.categoryId = categoryId;
	}
	
	public long getCategoryId() {
		long categoryId = this.categoryId;
		return categoryId;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryName() {
		String categoryName = this.categoryName;
		return categoryName;
	}	
	
}
