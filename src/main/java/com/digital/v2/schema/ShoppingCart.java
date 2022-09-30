package com.digital.v2.schema;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@ArraySchema
public class ShoppingCart {
	
	@ApiModelProperty(required = true, position = 1, notes = "장바구니 아이템 목록", example = "0", dataType = "array")
	private List<ShoppingCartItem> shoppingCart;

	public List<ShoppingCartItem> getShoppingCart() {
		return shoppingCart;
	}

	public void setShoppingCart(List<ShoppingCartItem> shoppingCart) {
		this.shoppingCart = shoppingCart;
	}
	
}
