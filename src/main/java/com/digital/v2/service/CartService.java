package com.digital.v2.service;

import static com.digital.v2.lucene.DataHandler.delete;
import static com.digital.v2.lucene.DataHandler.findHardly;
import static com.digital.v2.lucene.DataHandler.findListHardly;
import static com.digital.v2.lucene.DataHandler.write;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.Term;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Cart;
import com.digital.v2.schema.CartProduct;

@Component
public class CartService {

	@Resource
	ProductService productSvc;
	@Resource
	InventoryService inventorySvc;

	/* 장바구니 상품 등록 서비스 */
	public boolean cartProductWrite (CartProduct cartProduct) throws Exception {
		try {
			// cartProduct 중복 여부 확인
			if (cartProductSearch(cartProduct.getPersonId(), cartProduct.getProductId()).getPersonId() != 0) {
				throw new Exception("이미 등록된 장바구니 상품입니다."); 
			}
			
			// 중복이 아니면 입력 수량 유효성 검사
			if (!inventorySvc.inventoryQuantityCheck(cartProduct.getProductId(), cartProduct.getPurchaseNumber())) {
				throw new Exception("상품 ID: " + cartProduct.getProductId() + "의 재고 수량이 부족합니다.");
			}
	
			// 유효한 입력 수량이면 write
			Document cartProductDoc = new Document();
			
			cartProductDoc.add(new TextField("cartpersonid", "" + cartProduct.getPersonId(), Store.YES));
			cartProductDoc.add(new TextField("cartproductid", "" + cartProduct.getProductId(), Store.YES));
			cartProductDoc.add(new TextField("cartpurchasenumber", "" + cartProduct.getPurchaseNumber(), Store.YES));
			
			write(cartProductDoc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 장바구니 상품 삭제 서비스 */
	public boolean cartProductDelete (CartProduct cartProduct) throws Exception {
		try {
			// cartProduct 존재 여부 확인
			if (cartProductSearch(cartProduct.getPersonId(), cartProduct.getProductId()).getPersonId() == 0) {
				throw new Exception("장바구니에 해당하는 상품이 없습니다."); 
			}
			
			// 존재하면 delete
			Term term1 = new Term("cartpersonid", "" + cartProduct.getPersonId());
			Term term2 = new Term("cartproductid", "" + cartProduct.getProductId());
			
			delete(term1, term2);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 장바구니 삭제 서비스 */
	public boolean cartDelete (long personId) throws Exception {
		try {
			Term deleteTerm = new Term("cartpersonid", "" + personId);
			
			delete(deleteTerm);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 장바구니 상품 검색 서비스 */
	public CartProduct cartProductSearch (long personId, long productId) throws Exception {
		
		Term term1 = new Term("cartpersonid", "" + personId);
		Term term2 = new Term("cartproductid", "" + productId);
		
		Document cartProductDoc = findHardly(term1, term2);
		
		CartProduct cartProduct = new CartProduct();
		if (cartProductDoc != null) {
			cartProduct.setPersonId(Long.parseLong(cartProductDoc.get("cartpersonid")));
			cartProduct.setProductId(Long.parseLong(cartProductDoc.get("cartproductid")));
			cartProduct.setPurchaseNumber(Long.parseLong(cartProductDoc.get("cartpurchasenumber")));
		}
		
		return cartProduct;
	}
	
	/* 장바구니 검색 서비스 */
	public Cart cartSearch (long personId) throws Exception {
		
		String key = "cartpersonid";
		String value = "" + personId;
		
		List<Document> cartDoc = findListHardly(key, value);
		
		Cart cart = new Cart();
		List<CartProduct> cartProductList = new ArrayList<CartProduct>();
		
		for (Document cartProductDoc : cartDoc) {
			
			CartProduct cartProduct = new CartProduct();
			cartProduct.setPersonId(Long.parseLong(cartProductDoc.get("cartpersonid")));
			cartProduct.setProductId(Long.parseLong(cartProductDoc.get("cartproductid")));
			cartProduct.setPurchaseNumber(Long.parseLong(cartProductDoc.get("cartpurchasenumber")));
			
			cartProductList.add(cartProduct);
		}
		
		cart.setCart(cartProductList);
		
		return cart;
	}
	
}
