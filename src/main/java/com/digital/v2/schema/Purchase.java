package com.digital.v2.schema;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@ArraySchema
public class Purchase {

	@ApiModelProperty(required = false, position = 1, notes = "회원 ID", example = "0", dataType = "long")
	private long personId;
	
	@ApiModelProperty(required = true, position = 2, notes = "상품 ID", example = "0", dataType = "long")
	private long productId;
	
	@ApiModelProperty(required = true, position = 3, notes = "구매 수량", example = "0", dataType = "long")
	private long purchaseNumber;
	
	@ApiModelProperty(required = true, position = 4, notes = "주소 ID", example = "0", dataType = "long")
	private long addressId;
	
	@ApiModelProperty(required = true, position = 5, notes = "전화번호 ID", example = "0", dataType = "long")
	private long phoneId;
	
	@ApiModelProperty(required = true, position = 6, notes = "구매 날짜", example = "yyyyMMdd HHmmss", dataType = "string")
	private String purchaseDate;

	public long getPersonId() {
		long personId = this.personId;
		return personId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}

	public long getProductId() {
		long productId = this.productId;
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}

	public long getPurchaseNumber() {
		long purchaseNumber = this.purchaseNumber;
		return purchaseNumber;
	}

	public void setPurchaseNumber(long purchaseNumber) {
		this.purchaseNumber = purchaseNumber;
	}

	public long getAddressId() {
		long addressId = this.addressId;
		return addressId;
	}

	public void setAddressId(long addressId) {
		this.addressId = addressId;
	}

	public long getPhoneId() {
		long phoneId = this.phoneId;
		return phoneId;
	}

	public void setPhoneId(long phoneId) {
		this.phoneId = phoneId;
	}

	public String getPurchaseDate() {
		String purchaseDate = this.purchaseDate;
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

}
