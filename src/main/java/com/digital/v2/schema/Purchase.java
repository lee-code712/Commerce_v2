package com.digital.v2.schema;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@ArraySchema
public class Purchase {
	
	@ApiModelProperty(required = true, position = 1, notes = "상품 ID", example = "0", dataType = "long")
	private long productId;
	
	@ApiModelProperty(required = false, position = 2, notes = "구매 수량", example = "0", dataType = "long")
	private long purchaseNumber;
	
	@ApiModelProperty(required = true, position = 3, notes = "고객 ID", example = "0", dataType = "long")
	private long personId;
	
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
	
	public long getPersonId() {
		long personId = this.personId;
		return personId;
	}
	
	public void setPersonId(long personId) {
		this.personId = personId;
	}

}
