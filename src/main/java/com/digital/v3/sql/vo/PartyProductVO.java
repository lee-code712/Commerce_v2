package com.digital.v3.sql.vo;

import lombok.Data;

@Data
public class PartyProductVO {

	private long personId;
	private long productId;
	private long quantity;
	private long orderId;
	private String createDate;	// 장바구니에 상품을 추가한 날짜 - 장바구니 상품 식별자
	
	public void setPersonId(long personId) {
		this.personId = personId;
	}
	
	public long getPersonId() {
		long personId = this.personId;
		return personId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}
	
	public long getProductId() {
		long productId = this.productId;
		return productId;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	
	public long getQuantity() {
		long quantity = this.quantity;
		return quantity;
	}

	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	
	public long getOrderId() {
		long orderId = this.orderId;
		return orderId;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	
	public String getCreateDate() {
		String createDate = this.createDate;
		return createDate;
	}
	
}
