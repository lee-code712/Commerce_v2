package com.digital.v2.service;

import static com.digital.v2.lucene.DataHandler.findListHardlyByTwoTerms;
import static com.digital.v2.lucene.DataHandler.wildCardQuery;
import static com.digital.v2.lucene.DataHandler.write;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.Term;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Address;
import com.digital.v2.schema.Inventory;
import com.digital.v2.schema.Phone;
import com.digital.v2.schema.Product;
import com.digital.v2.schema.Purchase;
import com.digital.v2.schema.PurchaseDetail;
import com.digital.v2.schema.PurchaseList;
import com.digital.v2.schema.Cart;
import com.digital.v2.schema.CartProduct;

@Component
public class PurchaseService {
	
	@Resource ProductService productSvc;
	@Resource InventoryService inventorySvc;
	@Resource AddressService addressSvc;
	@Resource PhoneService phoneSvc;
	@Resource CartService cartSvc;
	@Resource PersonService personSvc;
	
	public boolean purchaseWithCartWrite (List<String> cartValueList, Purchase purchase) throws Exception {
		
		try {
			Cart cart = cartSvc.setCart(cartValueList);
			
			// 장바구니 상품들의 구매 수량 초과 여부 확인
			String errorMsg = "아래 상품들의 구매 수량이 재고 수량을 초과합니다.\n";
			boolean flag = false;
			
			for (CartProduct cartProduct : cart.getCart()) {
				Inventory inventory = inventorySvc.inventorySearchByProduct("productid", "" + cartProduct.getProductId());
				if (inventory.getQuantity() - cartProduct.getPurchaseNumber() < 0) {
					errorMsg += "상품 ID: " + cartProduct.getProductId() 
						+ ", 구매 수량: " + cartProduct.getPurchaseNumber()
						+ ", 재고 수량: " + inventory.getQuantity() + "\n";
					if (!flag) {
						flag = true;
					}
				}
			}
			
			if (flag) {
				throw new Exception(errorMsg);
			}
			
			// 구매 수량을 초과하지 않는 경우 write
			List<Document> docList = setPluralDoc(purchase, cart);
			
			for (Document doc : docList) {
				write(doc);
				
				// 재고 수량 update
				Inventory inventory = inventorySvc.inventorySearchByProduct("productid", doc.get("purchaseproductid"));
				inventory.setQuantity(inventory.getQuantity() - Long.valueOf(doc.get("purchasenumber")));
				inventorySvc.inventoryUpdate(inventory);
			}
			
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public boolean purchaseWrite (Purchase purchase) throws Exception {

		try {			
			Document purchaseDoc = setPurchaseDoc(purchase);
			
			write(purchaseDoc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}

	public List<Purchase> purchaseSearch (String token, String key, String value) {
		
		Term term1 = new Term("purchasepersonid", token);
		Term term2 = new Term(key, value);
		List<Document> purchaseDocList = findListHardlyByTwoTerms(term1, term2);
		
		List<Purchase> purchaseList = new ArrayList<Purchase>();
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
			purchaseList.add(purchase);
		}

		return purchaseList;
	}

	public PurchaseList purchaseDetailSearch (String token, String key, String value) throws Exception {
		
		List<Document> purchaseDocList = wildCardQuery(key, value);
		
		PurchaseList purchases = new PurchaseList();
		List<PurchaseDetail> purchaseDetailList = new ArrayList<PurchaseDetail>();
		for (Document purchaseDoc : purchaseDocList) {
			
			PurchaseDetail purchase = new PurchaseDetail();
			if (purchaseDoc != null && purchaseDoc.get("purchasepersonid").equals(token)) {
				
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
				
				purchaseDetailList.add(purchase);
			}
		}
		
		purchases.setPurchases(purchaseDetailList);
		
		return purchases;
	}
	
	public List<Document> setPluralDoc (Purchase purchase, Cart cart) throws Exception {
		
		List<Document> docList = new ArrayList<Document>();
		
		for (CartProduct cartProduct : cart.getCart()) {
			// cart 상품정보 set
			Purchase productPurchase = purchase;
			purchase.setProductId(cartProduct.getProductId());
			purchase.setPurchaseNumber(cartProduct.getPurchaseNumber());

			// purchase doc add
			docList.add(setPurchaseDoc(productPurchase));
		}
		
		return docList;
	}
	
	public Document setPurchaseDoc (Purchase purchase) throws Exception {

		Document purchaseDoc = new Document();
		
		purchaseDoc.add(new TextField("purchasepersonid", "" + purchase.getPersonId(), Store.YES));
		purchaseDoc.add(new TextField("purchaseproductid", "" + purchase.getProductId(), Store.YES));
		purchaseDoc.add(new TextField("purchasenumber", "" + purchase.getPurchaseNumber(), Store.YES));
		purchaseDoc.add(new TextField("purchaseaddressid", "" + purchase.getAddressId(), Store.YES));
		purchaseDoc.add(new TextField("purchasephoneid", "" + purchase.getPhoneId(), Store.YES));
		purchaseDoc.add(new TextField("purchasedate", "" + purchase.getPurchaseDate(), Store.YES));
			
		return purchaseDoc;
	}

}
