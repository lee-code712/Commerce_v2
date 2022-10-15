package com.digital.v3.sql.vo;

import lombok.Data;

@Data
public class ProductVO {

	private long productId;
	private String productName;
	private long price;
	private CategoryVO categoryVo;
	private long quantity;
	
}
