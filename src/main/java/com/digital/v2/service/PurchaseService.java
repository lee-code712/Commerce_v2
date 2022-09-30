package com.digital.v2.service;

import static com.digital.v2.lucene.DataHandler.findListHardly;
import static com.digital.v2.lucene.DataHandler.write;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Inventory;
import com.digital.v2.schema.Purchase;
import com.digital.v2.schema.ShoppingCart;
import com.digital.v2.schema.ShoppingCartItem;

@Component
public class PurchaseService {
	
	@Resource
	ProductService productSvc;
	@Resource
	ShoppingCartService shoppingCartSvc;
	@Resource
	InventoryService inventorySvc;
	
	public boolean purchaseWithCartWrite (List<String> cartItemStringList, Purchase purchase) throws Exception {
		
		String errorMsg = "아래 상품들의 구매 수량이 재고 수량을 초과합니다.\n";
		try {
			ShoppingCart shoppingCart = shoppingCartSvc.setCart(cartItemStringList);
			
			// 장바구니 상품들의 구매 수량 초과 여부 확인
			boolean flag = false;
			for (ShoppingCartItem cartItem : shoppingCart.getShoppingCart()) {
				Inventory inventory = inventorySvc.inventorySearchByProductId(cartItem.getProductId());
				if (inventory.getQuantity() - cartItem.getPurchaseNumber() < 0) {
					errorMsg += "상품 ID: " + cartItem.getProductId() 
						+ ", 구매 수량: " + cartItem.getPurchaseNumber()
						+ ", 재고 수량: " + inventory.getQuantity() + "\n";
					if (!flag) {
						flag = true;
					}
				}
			}
			
			if (flag) {
				throw new Exception(errorMsg);
			}

			for (ShoppingCartItem cartItem : shoppingCart.getShoppingCart()) {
				// cart 상품정보 set
				Purchase itemPurchase = purchase;
				purchase.setProductId(cartItem.getProductId());
				purchase.setPurchaseNumber(cartItem.getPurchaseNumber());

				// 구매정보 write
				purchaseWrite(itemPurchase);
				
				// 재고정보 변경
				Inventory inventory = inventorySvc.inventorySearchByProductId(cartItem.getProductId());
				inventory.setQuantity(inventory.getQuantity() - cartItem.getPurchaseNumber());
				inventorySvc.inventoryUpdate(inventory);
			}
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	public boolean purchaseWrite (Purchase purchase) throws Exception {

		try {
			Document doc = new Document();
			
			doc.add(new TextField("purchasepersonid", "" + purchase.getPersonId(), Store.YES));
			doc.add(new TextField("purchaseproductid", "" + purchase.getProductId(), Store.YES));
			doc.add(new TextField("purchasenumber", "" + purchase.getPurchaseNumber(), Store.YES));
			doc.add(new TextField("purchaseaddressid", "" + purchase.getAddressId(), Store.YES));
			doc.add(new TextField("purchasephoneid", "" + purchase.getPhoneId(), Store.YES));
			doc.add(new TextField("purchasedate", "" + purchase.getPurchaseDate(), Store.YES));
			
			write(doc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Purchase> purchaseSearch (String purchaseDate) {
		
		String key = "purchasedate";
		String value = "" + purchaseDate;
		
		List<Document> purchaseDocList = findListHardly(key, value);
		
		List<Purchase> purchases = new ArrayList<Purchase>();
		for (Document purchaseDoc : purchaseDocList) {
			
			Purchase purchase = new Purchase();
			if (purchaseDoc != null) {
				purchase.setPersonId(Long.parseLong(purchaseDoc.get("purchasepersonid")));
				purchase.setProductId(Long.parseLong(purchaseDoc.get("purchaseproductid")));
				purchase.setPurchaseNumber(Long.parseLong(purchaseDoc.get("purchasenumber")));
				purchase.setAddressId(Long.parseLong(purchaseDoc.get("purchaseaddressid")));
				purchase.setPhoneId(Long.parseLong(purchaseDoc.get("purchasephoneid")));
				purchase.setPurchaseDate(purchaseDoc.get("purchasedate"));
			}
			purchases.add(purchase);
		}
		
		return purchases;
	}
	
	// 날짜로 구매 상세 조회
}
