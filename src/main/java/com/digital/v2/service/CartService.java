package com.digital.v2.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.digital.v2.schema.Inventory;
import com.digital.v2.schema.Cart;
import com.digital.v2.schema.CartProduct;

@Component
public class CartService {

	@Resource
	InventoryService inventorySvc;
	@Resource
	ProductService productSvc;

	public boolean isValidCartProduct (List<String> cartValueList, CartProduct cartProduct) throws Exception {
		
		try {
			// cart product 중복 여부 확인
			if (cartValueList != null) {
				for (String cartValue : cartValueList) {
					if (cartValue.split("/")[0].equals("" + cartProduct.getProductId())) {
						throw new Exception("이미 장바구니에 담겨있는 상품입니다.");
					}
				}
			}
			
			// 중복이 아니면 수량 초과 여부 확인
			Inventory inventory = inventorySvc.inventorySearchByProduct("productid", "" + cartProduct.getProductId());
			if (inventory.getQuantity() - cartProduct.getPurchaseNumber() < 0) {
				throw new Exception("상품의 재고 수량(" + inventory.getQuantity() + "개)을 초과합니다.");
			}
			
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public boolean isExistCartProduct (List<String> cartValueList, CartProduct cartProduct) throws Exception {
		
		try {
			// cart product 존재 여부 확인
			if (cartValueList != null) {
				for (String cartValue : cartValueList) {
					if (cartValue.split("/")[0].equals("" + cartProduct.getProductId())) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			throw e;
		}
	}
		
	public Cart setCart (List<String> cartValueList) {
		
		Cart cart = new Cart();
		List<CartProduct> cartProductList = new ArrayList<CartProduct>();
		
		for (String cartValue : cartValueList) {
			
			CartProduct cartItem = new CartProduct();
			cartItem.setProductId(Long.parseLong(cartValue.split("/")[0]));
			cartItem.setPurchaseNumber(Long.parseLong(cartValue.split("/")[1]));
			
			cartProductList.add(cartItem);
		}
		
		cart.setCart(cartProductList);
		return cart;
	}
	
}
