package com.digital.v2.schema;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class OrderSheetDetail {
	
	@ApiModelProperty(required = false, position = 1, notes = "주문 ID", example = "0", dataType = "long")
	private long orderSheetId;

	@ApiModelProperty(required = false, position = 2, notes = "회원 ID", example = "0", dataType = "long")
	private long personId;
	
	@ApiModelProperty(required = true, position = 3, notes = "주문 상품 목록", example = "", dataType = "array")
	private List<CartProductDetail> products;
	
	@ApiModelProperty(required = true, position = 4, notes = "주소 정보", example = "", dataType = "object")
	private Address address;
	
	@ApiModelProperty(required = true, position = 5, notes = "전화번호 정보", example = "", dataType = "object")
	private Phone phone;

	public long getOrderSheetId() {
		long orderSheetId = this.orderSheetId;
		return orderSheetId;
	}

	public void setOrderSheetId(long orderSheetId) {
		this.orderSheetId = orderSheetId;
	}

	public long getPersonId() {
		long personId = this.personId;
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public List<CartProductDetail> getProducts() {
		List<CartProductDetail> products = this.products;
		return products;
	}

	public void setProducts(List<CartProductDetail> products) {
		this.products = products;
	}

	public Address getAddress() {
		Address address = this.address;
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Phone getPhone() {
		Phone phone = this.phone;
		return phone;
	}

	public void setPhone(Phone phone) {
		this.phone = phone;
	}

}
