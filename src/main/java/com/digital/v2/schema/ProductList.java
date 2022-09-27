package com.digital.v2.schema;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.ArraySchema;

@ArraySchema
public class ProductList {

	@ApiModelProperty(required = false, position = 1, notes = "상품 리스트", example = "", dataType = "array")
	private List<Product> products;

	public List<Product> getProducts() {
		List<Product> products = this.products;
		return products;
	}

	public void setProducts(List<Product> products) {	// 인자값이 동일해야 하므로 list.add 사용하지 말 것
		this.products = products;
	}

}
