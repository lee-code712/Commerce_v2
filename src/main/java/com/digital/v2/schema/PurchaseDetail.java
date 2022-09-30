
package com.digital.v2.schema;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@ArraySchema
public class PurchaseDetail {

	@ApiModelProperty(required = false, position = 1, notes = "회원 ID", example = "0", dataType = "long")
	private long personId;
	
	@ApiModelProperty(required = false, position = 1, notes = "구매 상품 정보", example = "0", dataType = "object")
	private Product product;
	
	@ApiModelProperty(required = false, position = 1, notes = "구매 수량", example = "0", dataType = "long")
	private long purchaseNumber;
	
	@ApiModelProperty(required = false, position = 1, notes = "주소 정보", example = "0", dataType = "object")
	private Address address;
	
	@ApiModelProperty(required = false, position = 1, notes = "전화번호 정보", example = "0", dataType = "object")
	private Phone phone;
	
	@ApiModelProperty(required = false, position = 1, notes = "구매 날짜", example = "0", dataType = "string")
	private String purchaseDate;

	public long getPersonId() {
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public long getPurchaseNumber() {
		return purchaseNumber;
	}

	public void setPurchaseNumber(long purchaseNumber) {
		this.purchaseNumber = purchaseNumber;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public Phone getPhone() {
		return phone;
	}

	public void setPhone(Phone phone) {
		this.phone = phone;
	}

	public String getPurchaseDate() {
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

}
