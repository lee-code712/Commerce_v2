package com.digital.v2.schema;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class CartProductDetail {
	
	@ApiModelProperty(required = true, position = 1, notes = "상품 정보", example = "", dataType = "object")
	private Product product;

	@ApiModelProperty(required = true, position = 2, notes = "상품 수량", example = "0", dataType = "long")
	private long quantity;

	public Product getProduct() {
		Product product = this.product;
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public long getQuantity() {
		long quantity = this.quantity;
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

}
