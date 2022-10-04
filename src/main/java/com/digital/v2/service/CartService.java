package com.digital.v2.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.digital.v2.schema.Cart;
import com.digital.v2.schema.CartProduct;

@Component
public class CartService {

	@Resource
	ProductService productSvc;
	@Resource
	InventoryService inventorySvc;

	public boolean cartProductCheck (List<String> cartValueList, CartProduct cartProduct) throws Exception {
		
		try {
			// cart product 중복 여부 확인
			if (cartValueList != null) {
				for (String cartValue : cartValueList) {
					if (cartValue.split("/")[0].equals("" + cartProduct.getProductId())) {
						throw new Exception("이미 장바구니에 담겨있는 상품입니다.");
					}
				}
			}
			
			// 중복이 아니면 입력 수량 유효성 검사
			if (!inventorySvc.inventoryQuantityCheck(cartProduct.getProductId(), cartProduct.getPurchaseNumber())) {
				throw new Exception("상품 ID: " + cartProduct.getProductId() + "의 재고 수량이 부족합니다.");
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
			throw new Exception("장바구니에 해당하는 상품이 없습니다.");
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
