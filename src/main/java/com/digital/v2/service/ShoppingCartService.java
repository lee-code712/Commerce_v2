package com.digital.v2.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.digital.v2.schema.Inventory;
import com.digital.v2.schema.ShoppingCart;
import com.digital.v2.schema.ShoppingCartItem;

@Component
public class ShoppingCartService {

	@Resource
	InventoryService inventorySvc;

	public boolean cartItemCheck (List<String> cartItemStringList, ShoppingCartItem cartItem) throws Exception {
		
		try {
			// cartItem 중복 여부 확인
			if (cartItemStringList != null) {
				for (String cartItemString : cartItemStringList) {
					if (cartItemString.split("/")[0].equals("" + cartItem.getProductId())) {
						throw new Exception("이미 장바구니에 담겨있는 상품입니다.");
					}
				}
			}
			
			// 중복이 아니면 수량 초과 여부 확인
			Inventory inventory = inventorySvc.inventorySearchByProduct("productid", "" + cartItem.getProductId());
			if (inventory.getQuantity() - cartItem.getPurchaseNumber() < 0) {
				throw new Exception("상품의 재고 수량(" + inventory.getQuantity() + "개)을 초과합니다.");
			}
			
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public boolean cartItemIsExist (List<String> cartItemStringList, ShoppingCartItem cartItem) throws Exception {
		
		try {
			// cartItem 존재 여부 확인
			if (cartItemStringList != null) {
				for (String cartItemString : cartItemStringList) {
					if (cartItemString.split("/")[0].equals("" + cartItem.getProductId())) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			throw e;
		}
	}
		
	public ShoppingCart setShoppingCart (List<String> cartItemStringList) {
		
		ShoppingCart cart = new ShoppingCart();
		List<ShoppingCartItem> cartItemList = new ArrayList<ShoppingCartItem>();
		
		for (String cartItemString : cartItemStringList) {
			
			ShoppingCartItem cartItem = new ShoppingCartItem();
			cartItem.setProductId(Long.parseLong(cartItemString.split("/")[0]));
			cartItem.setPurchaseNumber(Long.parseLong(cartItemString.split("/")[1]));
			
			cartItemList.add(cartItem);
		}
		
		cart.setShoppingCart(cartItemList);
		
		return cart;
	}
	
}
