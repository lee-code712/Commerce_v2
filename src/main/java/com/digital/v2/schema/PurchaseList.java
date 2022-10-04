package com.digital.v2.schema;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@ArraySchema
public class PurchaseList {

	@ApiModelProperty(required = false, position = 1, notes = "상품 구매 상세 리스트", example = "", dataType = "array")
	private List<PurchaseDetail> purchases;

	public List<PurchaseDetail> getPurchases() {
		List<PurchaseDetail> purchases = this.purchases;
		return purchases;
	}

	public void setPurchases(List<PurchaseDetail> purchases) {
		this.purchases = purchases;
	}
	
}
