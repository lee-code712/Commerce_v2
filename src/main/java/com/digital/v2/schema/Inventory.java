package com.digital.v2.schema;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class Inventory {
//	inventoryId (long)
//	quantity (long)
	
	@ApiModelProperty(required = false, position = 1, notes = "상품 재고 ID", example = "0", dataType = "long")
	private long inventoryId;
	
	@ApiModelProperty(required = true, position = 2, notes = "재고 수량", example = "0", dataType = "long")
	private long quantity;
	
	public long getInventoryId() {
		long inventoryId = this.inventoryId;
		return inventoryId;
	}

	public void setInventoryId(long inventoryId) {
		this.inventoryId = inventoryId;
	}

	public long getQuantity() {
		long quantity = this.quantity;
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

}
