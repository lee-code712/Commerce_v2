package com.digital.v2.service;

import static com.digital.v2.lucene.DataHandler.findListHardly;
import static com.digital.v2.lucene.DataHandler.wildCardQuery;
import static com.digital.v2.lucene.DataHandler.delete;
import static com.digital.v2.lucene.DataHandler.findHardly;
import static com.digital.v2.lucene.DataHandler.write;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.Term;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Address;
import com.digital.v2.schema.Inventory;
import com.digital.v2.schema.OrderSheet;
import com.digital.v2.schema.OrderSheetDetail;
import com.digital.v2.schema.Phone;
import com.digital.v2.schema.Product;
import com.digital.v2.schema.Purchase;
import com.digital.v2.schema.PurchaseDetail;
import com.digital.v2.schema.PurchaseList;
import com.digital.v2.schema.CartProduct;
import com.digital.v2.schema.CartProductDetail;

@Component
public class OrderService {
	
	@Resource 
	ProductService productSvc;
	@Resource 
	InventoryService inventorySvc;
	@Resource
	CartService cartSvc;
	@Resource
	PhoneService phoneSvc;
	@Resource
	AddressService addressSvc;
	
	/* 주문서 등록 */
	public long orderSheetWrite (OrderSheet orderSheet) throws Exception {
		
		try {
			// 주문서 중복 여부 확인
			if (orderSheetSearch("ordersheetpersonid", "" + orderSheet.getPersonId()).getOrderSheetId() != 0) {
				throw new Exception("이미 등록된 가주문서가 존재합니다.");
			}
			
			// 중복이 아니면 상품들의 구매 수량 유효성 검사 - 중복 상품들의 입력 수량을 더한 값으로 계산
			String errorMsg = "아래 상품들의 재고 수량이 부족합니다.\n";
			boolean exceptionFlag = false;
			
			Map<Long, Long> productMap = new HashMap<Long, Long>();
			for (CartProduct product : orderSheet.getProducts()) {
				if (productMap.get(product.getProductId()) == null) {
					productMap.put(product.getProductId(), product.getQuantity());
				} else {
					productMap.put(product.getProductId(), product.getQuantity() + productMap.get(product.getProductId()));
				}
				if (!inventorySvc.inventoryQuantityCheck(product.getProductId(), productMap.get(product.getProductId()))) {
					errorMsg += "상품 ID: " + product.getProductId() + "\n";
					if (!exceptionFlag) {
						exceptionFlag = true;
					}
				}
			}
			
			if (exceptionFlag) {
				throw new Exception(errorMsg);
			}
			
			// 모든 상품의 구매 수량이 유효하면 write
			orderSheet.setOrderSheetId(System.currentTimeMillis());
			List<Document> docList = setOrderSheetPluralDoc(orderSheet);
			
			for (Document doc : docList) {
				write(doc);
			}
			return orderSheet.getOrderSheetId();
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 결제 */
	public boolean purchase (long personId, Purchase purchase) throws Exception {

		try {
			// 주문서 존재 여부 확인
			OrderSheet orderSheet = orderSheetSearch("ordersheetid", "" + purchase.getOrderSheetId());
			if (orderSheet.getOrderSheetId() == 0) {
				throw new Exception("가주문서를 찾을 수 없습니다.");
			}
			
			// 존재하면 구매 정보 write
			Document purchaseDoc = new Document();
			
			purchaseDoc.add(new TextField("purchaseid", "" + purchase.getOrderSheetId(), Store.YES));
			purchaseDoc.add(new TextField("purchasepersonid", "" + orderSheet.getPersonId(), Store.YES));
			purchaseDoc.add(new TextField("purchasephoneid", "" + orderSheet.getPhoneId(), Store.YES));
			purchaseDoc.add(new TextField("purchaseaddressid", "" + orderSheet.getAddressId(), Store.YES));
			purchaseDoc.add(new TextField("purchasedate", "" + purchase.getPurchaseDate(), Store.YES));
			
			write(purchaseDoc);
			
			// order product 관련 작업 처리
			List<CartProduct> products = orderSheet.getProducts();
			for (CartProduct product : products) {
				// 재고 수량 update
				Inventory inventory = inventorySvc.inventorySearchByProduct("productid", "" + product.getProductId());
				inventory.setQuantity(inventory.getQuantity() - Long.valueOf(product.getQuantity()));
				inventorySvc.inventoryUpdate(inventory);
				
				// 장바구니에 있는 상품이면 장바구니에서 삭제
				if(cartSvc.cartProductSearch(personId, product).getProductId() != 0) {
					cartSvc.cartProductDelete(personId, product);
				}
			}
			
			// 주문서 delete
			orderSheetDelete(purchase.getOrderSheetId());
			
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 주문서 삭제 */
	public boolean orderSheetDelete (long orderSheetId) throws Exception {
		try {
			Term deleteTerm = new Term("ordersheetid", "" + orderSheetId);
			
			delete(deleteTerm);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 주문서 검색 */
	public OrderSheet orderSheetSearch (String key, String value) {
		
		Document orderSheetDoc = findHardly(key, value);
		
		OrderSheet orderSheet = new OrderSheet();
		if (orderSheetDoc != null) {
			orderSheet.setOrderSheetId(Long.parseLong(orderSheetDoc.get("ordersheetid")));
			orderSheet.setPersonId(Long.parseLong(orderSheetDoc.get("ordersheetpersonid")));
			orderSheet.setPhoneId(Long.parseLong(orderSheetDoc.get("ordersheetphoneid")));
			orderSheet.setAddressId(Long.parseLong(orderSheetDoc.get("ordersheetaddressid")));
			
			// order product list set
			List<CartProduct> products = orderProductSearch("orderid", "" + orderSheet.getOrderSheetId());
			
			orderSheet.setProducts(products);
		}

		return orderSheet;
	}
	
	/* 주문서 상세 검색 */
	public OrderSheetDetail orderSheetDetailSearch (String key, String value) throws Exception {
		
		Document ordersheetDoc = findHardly(key, value);
		
		OrderSheetDetail orderSheet = new OrderSheetDetail();
		if (ordersheetDoc != null) {
			orderSheet.setOrderSheetId(Long.parseLong(ordersheetDoc.get("ordersheetid")));
			orderSheet.setPersonId(Long.parseLong(ordersheetDoc.get("ordersheetpersonid")));
			
			// phone set
			Phone phone = phoneSvc.phoneSearch("phoneid", ordersheetDoc.get("ordersheetphoneid"));
			orderSheet.setPhone(phone);
			
			// address set
			Address address = addressSvc.addressSearch("addressid", ordersheetDoc.get("ordersheetaddressid"));
			orderSheet.setAddress(address);
			
			// order product list set
			List<CartProductDetail> products = orderProductDetailSearch("orderid", "" + orderSheet.getOrderSheetId());
			orderSheet.setProducts(products);
		}

		return orderSheet;
	}
	
	/* 주문 상품 검색 */
	public List<CartProduct> orderProductSearch (String key, String value) {
		
		List<Document> orderProductDocList = findListHardly(key, value);

		List<CartProduct> products = new ArrayList<CartProduct>();
		for (Document orderProductDoc : orderProductDocList) {
			CartProduct orderProduct = new CartProduct();
			
			orderProduct.setProductId(Long.parseLong(orderProductDoc.get("orderproductid")));
			orderProduct.setQuantity(Long.parseLong(orderProductDoc.get("orderquantity")));
			
			products.add(orderProduct);
		}

		return products;
	}
	
	/* 주문 상품 상세 검색 */
	public List<CartProductDetail> orderProductDetailSearch (String key, String value) throws Exception {
		
		List<Document> orderProductDocList = findListHardly(key, value);

		List<CartProductDetail> products = new ArrayList<CartProductDetail>();
		for (Document orderProductDoc : orderProductDocList) {
			CartProductDetail orderProduct = new CartProductDetail();
			
			orderProduct.setQuantity(Long.parseLong(orderProductDoc.get("orderquantity")));
			
			// product set
			Product product = productSvc.productSearch("productid", orderProductDoc.get("orderproductid"));
			orderProduct.setProduct(product);
			
			products.add(orderProduct);
		}

		return products;
	}
	
	/* 구매 상세 검색 */
	public PurchaseDetail purchaseDetailSearch (String key, String value) throws Exception {

		Document purchaseDoc = findHardly(key, value);
		
		PurchaseDetail purchase = new PurchaseDetail();
		if (purchaseDoc != null) {
			
			purchase.setPurchaseDate(purchaseDoc.get("purchasedate"));
			
			// order set
			OrderSheetDetail order = new OrderSheetDetail();
			
			order.setOrderSheetId(Long.parseLong(purchaseDoc.get("purchaseid")));
			order.setPersonId(Long.parseLong(purchaseDoc.get("purchasepersonid")));
			
			// phone set
			Phone phone = phoneSvc.phoneSearch("phoneid", purchaseDoc.get("purchasephoneid"));
			order.setPhone(phone);
			
			// address set
			Address address = addressSvc.addressSearch("addressid", purchaseDoc.get("purchaseaddressid"));
			order.setAddress(address);
			
			// order product list set
			List<CartProductDetail> products = orderProductDetailSearch("orderid", "" + order.getOrderSheetId());
			order.setProducts(products);
			
			purchase.setOrder(order);
		}

		return purchase;
	}
	
	/* 구매 목록 검색 */
	public PurchaseList purchaseListSearch (String personId, String key, String value) throws Exception {

		List<Document> purchaseDocList = wildCardQuery(key, value);
		
		PurchaseList purchases = new PurchaseList();
		List<PurchaseDetail> purchaseList = new ArrayList<PurchaseDetail>();
		for (Document purchaseDoc : purchaseDocList) {
			
			if (purchaseDoc.get("purchasepersonid").equals(personId)) {
				
				PurchaseDetail purchase = new PurchaseDetail();

				LocalDateTime purchaseDate = LocalDateTime.parse((String) purchaseDoc.get("purchasedate"), 
						DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				purchase.setPurchaseDate(purchaseDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
				
				// order set
				OrderSheetDetail order = new OrderSheetDetail();
				
				order.setOrderSheetId(Long.parseLong(purchaseDoc.get("purchaseid")));
				order.setPersonId(Long.parseLong(purchaseDoc.get("purchasepersonid")));
				
				// phone set
				Phone phone = phoneSvc.phoneSearch("phoneid", purchaseDoc.get("purchasephoneid"));
				order.setPhone(phone);
				
				// address set
				Address address = addressSvc.addressSearch("addressid", purchaseDoc.get("purchaseaddressid"));
				order.setAddress(address);
				
				// order product list set
				List<CartProductDetail> products = orderProductDetailSearch("orderid", "" + order.getOrderSheetId());
				order.setProducts(products);
				
				purchase.setOrder(order);
				
				purchaseList.add(purchase);
			}
		}
		
		purchases.setPurchases(purchaseList);

		return purchases;
	}
	
	public List<Document> setOrderSheetPluralDoc (OrderSheet orderSheet) {

		List<Document> docList = new ArrayList<Document>();
		
		// order sheet doc add
		Document orderSheetDoc = new Document();
		
		orderSheetDoc.add(new TextField("ordersheetid", "" + orderSheet.getOrderSheetId(), Store.YES));
		orderSheetDoc.add(new TextField("ordersheetpersonid", "" + orderSheet.getPersonId(), Store.YES));
		orderSheetDoc.add(new TextField("ordersheetphoneid", "" + orderSheet.getPhoneId(), Store.YES));
		orderSheetDoc.add(new TextField("ordersheetaddressid", "" + orderSheet.getAddressId(), Store.YES));

		docList.add(orderSheetDoc);
		
		// order product doc add
		List<CartProduct> products = orderSheet.getProducts();
		for (CartProduct product : products) {
			Document productDoc = new Document();
			
			productDoc.add(new TextField("orderid", "" + orderSheet.getOrderSheetId(), Store.YES));
			productDoc.add(new TextField("orderproductid", "" + product.getProductId(), Store.YES));
			productDoc.add(new TextField("orderquantity", "" + product.getQuantity(), Store.YES));
			
			docList.add(productDoc);
		}
		
		return docList;
	}

}
