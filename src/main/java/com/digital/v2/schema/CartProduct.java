package com.digital.v2.schema;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class CartProduct {
	
	@ApiModelProperty(required = true, position = 1, notes = "고객 ID", example = "0", dataType = "long")
	private long personId;
	
	@ApiModelProperty(required = true, position = 2, notes = "상품 ID", example = "0", dataType = "long")
	private long productId;
	
	@ApiModelProperty(required = true, position = 3, notes = "구매 수량", example = "0", dataType = "long")
	private long purchaseNumber;
	
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

}
