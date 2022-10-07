
package com.digital.v2.schema;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class PurchaseDetail {
	
	@ApiModelProperty(required = false, position = 1, notes = "구매 날짜", example = "yyyyMMddHHmmss", dataType = "string")
	private String purchaseDate;
	
	@ApiModelProperty(required = true, position = 2, notes = "구매 정보", example = "0", dataType = "object")
	private OrderSheetDetail order;

	public String getPurchaseDate() {
		String purchaseDate = this.purchaseDate;
		return purchaseDate;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	
	public OrderSheetDetail getOrder() {
		OrderSheetDetail order = this.order;
		return order;
	}

	public void setOrder(OrderSheetDetail order) {
		this.order = order;
	}

}
