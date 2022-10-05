package com.digital.v2.service;

import static com.digital.v2.lucene.DataHandler.wildCardQuery;
import static com.digital.v2.lucene.DataHandler.findListHardly;
import static com.digital.v2.lucene.DataHandler.write;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
	
	@Resource 
	ProductService productSvc;
	@Resource 
	InventoryService inventorySvc;
	@Resource 
	AddressService addressSvc;
	@Resource 
	PhoneService phoneSvc;
	@Resource 
	CartService cartSvc;
	
	/* 구매 서비스 */
	public boolean purchase (Purchase purchase) throws Exception {

		try {
			// 상품의 구매 수량 유효성 검사
			if (!inventorySvc.inventoryQuantityCheck(purchase.getProductId(), purchase.getPurchaseNumber())) {
				throw new Exception("상품 ID: " + purchase.getProductId() + "의 재고 수량이 부족합니다.");
			}
			
			// 상품의 구매 수량이 유효하면 write
			Document purchaseDoc = new Document();
			
			purchaseDoc.add(new TextField("purchasepersonid", "" + purchase.getPersonId(), Store.YES));
			purchaseDoc.add(new TextField("purchaseproductid", "" + purchase.getProductId(), Store.YES));
			purchaseDoc.add(new TextField("purchasenumber", "" + purchase.getPurchaseNumber(), Store.YES));
			purchaseDoc.add(new TextField("purchaseaddressid", "" + purchase.getAddressId(), Store.YES));
			purchaseDoc.add(new TextField("purchasephoneid", "" + purchase.getPhoneId(), Store.YES));
			purchaseDoc.add(new TextField("purchasedate", "" + purchase.getPurchaseDate(), Store.YES));
			
			write(purchaseDoc);
			
			// 상품의 재고 수량 update
			Inventory inventory = inventorySvc.inventorySearchByProduct("productid", "" + purchase.getProductId());
			inventory.setQuantity(inventory.getQuantity() - Long.valueOf(purchase.getPurchaseNumber()));
			inventorySvc.inventoryUpdate(inventory);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 장바구니 상품 일괄 구매 서비스 */
	public boolean purchaseInCart (Purchase purchase) throws Exception {
		
		try {
			Cart cart = cartSvc.cartSearch(purchase.getPersonId());
			
			// 장바구니 상품들의 구매 수량 유효성 검사
			String errorMsg = "아래 상품들의 재고 수량이 부족합니다.\n";
			boolean exceptionFlag = false;
			
			for (CartProduct cartProduct : cart.getCart()) {
				if (!inventorySvc.inventoryQuantityCheck(cartProduct.getProductId(), cartProduct.getPurchaseNumber())) {
					errorMsg += "상품 ID: " + cartProduct.getProductId() + "\n";
					if (!exceptionFlag) {
						exceptionFlag = true;
					}
				}
			}
			
			if (exceptionFlag) {
				throw new Exception(errorMsg);
			}
			
			// 모든 상품의 구매 수량이 유효하면 write
			List<Document> docList = setPluralDoc(purchase, cart);
			
			for (Document doc : docList) {
				write(doc);
				
				// 해당 상품의 재고 수량 update
				Inventory inventory = inventorySvc.inventorySearchByProduct("productid", doc.get("purchaseproductid"));
				inventory.setQuantity(inventory.getQuantity() - Long.valueOf(doc.get("purchasenumber")));
				inventorySvc.inventoryUpdate(inventory);
			}
			
			// 장바구니 일괄 삭제
			cartSvc.cartDelete(purchase.getPersonId());
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 구매 검색 서비스 */
	public List<Purchase> purchaseSearch (String personId, String key, String value) {
		
		Term term1 = new Term("purchasepersonid", personId);
		Term term2 = new Term(key, value);
		
		List<Document> purchaseDocList = findListHardly(term1, term2);
		
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
				
				purchaseList.add(purchase);
			}
		}

		return purchaseList;
	}

	/* 구매 상세 검색 서비스 */
	public PurchaseList purchaseDetailSearch (String personId, String key, String value) throws Exception {
		
		List<Document> purchaseDocList = wildCardQuery(key, value);
		
		PurchaseList purchases = new PurchaseList();
		List<PurchaseDetail> purchaseDetailList = new ArrayList<PurchaseDetail>();
		for (Document purchaseDoc : purchaseDocList) {
			
			PurchaseDetail purchase = new PurchaseDetail();
			if (purchaseDoc != null && purchaseDoc.get("purchasepersonid").equals(personId)) {
				
				// purchase set
				purchase.setPersonId(Long.parseLong(purchaseDoc.get("purchasepersonid")));
				purchase.setPurchaseNumber(Long.parseLong(purchaseDoc.get("purchasenumber")));

				LocalDateTime purchaseDate = LocalDateTime.parse((String) purchaseDoc.get("purchasedate"), 
						DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				purchase.setPurchaseDate(purchaseDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
				
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

			Document purchaseDoc = new Document();
			
			purchaseDoc.add(new TextField("purchaseproductid", "" + cartProduct.getProductId(), Store.YES));
			purchaseDoc.add(new TextField("purchasenumber", "" + cartProduct.getPurchaseNumber(), Store.YES));
			purchaseDoc.add(new TextField("purchasepersonid", "" + purchase.getPersonId(), Store.YES));
			purchaseDoc.add(new TextField("purchaseaddressid", "" + purchase.getAddressId(), Store.YES));
			purchaseDoc.add(new TextField("purchasephoneid", "" + purchase.getPhoneId(), Store.YES));
			purchaseDoc.add(new TextField("purchasedate", "" + purchase.getPurchaseDate(), Store.YES));
			
			docList.add(purchaseDoc);
		}
		
		return docList;
	}

}
