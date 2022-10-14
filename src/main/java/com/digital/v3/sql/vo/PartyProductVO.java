package com.digital.v3.sql.vo;

import lombok.Data;

@Data
public class PartyProductVO {

	private long personId;
	private long productId;
	private long quantity;
	private long orderId;
	private String createDate;	// 장바구니에 상품을 추가한 날짜 - 장바구니 상품 식별자
	
}
