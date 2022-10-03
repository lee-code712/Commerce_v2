package com.digital.v2.service;

import static com.digital.v2.lucene.DataHandler.findListHardly;
import static com.digital.v2.lucene.DataHandler.wildCardQuery;
import static com.digital.v2.lucene.DataHandler.write;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Address;
import com.digital.v2.schema.Inventory;
import com.digital.v2.schema.Phone;
import com.digital.v2.schema.Product;
import com.digital.v2.schema.Purchase;
import com.digital.v2.schema.PurchaseDetail;
import com.digital.v2.schema.ShoppingCart;
import com.digital.v2.schema.ShoppingCartItem;

@Component
public class PurchaseService {
	
	@Resource
	ProductService productSvc;
	@Resource
	InventoryService inventorySvc;
	@Resource
	AddressService addressSvc;
	@Resource
	PhoneService phoneSvc;
	@Resource
	ShoppingCartService shoppingCartSvc;
	
	public boolean purchaseWithCartWrite (List<String> cartItemStringList, Purchase purchase) throws Exception {
		
		String errorMsg = "아래 상품들의 구매 수량이 재고 수량을 초과합니다.\n";
		try {
			ShoppingCart shoppingCart = shoppingCartSvc.setShoppingCart(cartItemStringList);
			
			// 장바구니 상품들의 구매 수량 초과 여부 확인
			boolean flag = false;
			for (ShoppingCartItem cartItem : shoppingCart.getShoppingCart()) {
				Inventory inventory = inventorySvc.inventorySearchByProduct("productid", "" + cartItem.getProductId());
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
				Inventory inventory = inventorySvc.inventorySearchByProduct("productid", "" + cartItem.getProductId());
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
			
			// personId가 존재하는지 확인
			doc.add(new TextField("purchasepersonid", "" + purchase.getPersonId(), Store.YES));
			// product가 존재하는지 확인
			doc.add(new TextField("purchaseproductid", "" + purchase.getProductId(), Store.YES));
			doc.add(new TextField("purchasenumber", "" + purchase.getPurchaseNumber(), Store.YES));
			// address가 존재하는지 확인
			doc.add(new TextField("purchaseaddressid", "" + purchase.getAddressId(), Store.YES));
			// phone이 존재하는지 확인
			doc.add(new TextField("purchasephoneid", "" + purchase.getPhoneId(), Store.YES));
			doc.add(new TextField("purchasedate", "" + purchase.getPurchaseDate(), Store.YES));
			
			write(doc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Purchase> purchaseSearch (String key, String value) {
		
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

	public List<PurchaseDetail> purchaseDetailSearch (String key, String value) throws Exception {
		
		List<Document> purchaseDocList = wildCardQuery(key, value);
		
		List<PurchaseDetail> purchaseDetails = new ArrayList<PurchaseDetail>();
		for (Document purchaseDoc : purchaseDocList) {
			
			PurchaseDetail purchase = new PurchaseDetail();
			if (purchaseDoc != null) {
				
				// purchase set
				purchase.setPersonId(Long.parseLong(purchaseDoc.get("purchasepersonid")));
				purchase.setPurchaseNumber(Long.parseLong(purchaseDoc.get("purchasenumber")));
				purchase.setPurchaseDate(purchaseDoc.get("purchasedate"));
				
				// product set
				Product product = productSvc.productSearch("productid", purchaseDoc.get("purchaseproductid"));
				purchase.setProduct(product);
				
				// address set
				Address address = addressSvc.addressSearch("addressid", purchaseDoc.get("purchaseaddressid"));
				purchase.setAddress(address);
				
				// phone set
				Phone phone = phoneSvc.phoneSearch("phoneid", purchaseDoc.get("purchasephoneid"));
				purchase.setPhone(phone);
				
				purchaseDetails.add(purchase);
			}
		}
		
		return purchaseDetails;
	}

}
