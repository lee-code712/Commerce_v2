
package com.digital.v2.schema;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class PurchaseDetail {

	@ApiModelProperty(required = false, position = 1, notes = "회원 ID", example = "0", dataType = "long")
	private long personId;
	
	@ApiModelProperty(required = false, position = 2, notes = "구매 상품 정보", example = "0", dataType = "object")
	private Product product;
	
	@ApiModelProperty(required = false, position = 3, notes = "구매 수량", example = "0", dataType = "long")
	private long purchaseNumber;
	
	@ApiModelProperty(required = false, position = 4, notes = "주소 정보", example = "0", dataType = "object")
	private Address address;
	
	@ApiModelProperty(required = false, position = 5, notes = "전화번호 정보", example = "0", dataType = "object")
	private Phone phone;
	
	@ApiModelProperty(required = false, position = 6, notes = "구매 날짜", example = "yyyyMMdd HHmmss", dataType = "string")
	private String purchaseDate;

	public long getPersonId() {
		long personId = this.personId;
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public Product getProduct() {
		Product product = this.product;
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public long getPurchaseNumber() {
		long purchaseNumber = this.purchaseNumber;
		return purchaseNumber;
	}

	public void setPurchaseNumber(long purchaseNumber) {
		this.purchaseNumber = purchaseNumber;
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

	public String getPurchaseDate() {
		String purchaseDate = this.purchaseDate;
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

}
