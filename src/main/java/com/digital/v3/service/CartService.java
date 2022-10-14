package com.digital.v3.service;

import static com.digital.v3.lucene.DataHandler.delete;
import static com.digital.v3.lucene.DataHandler.findHardly;
import static com.digital.v3.lucene.DataHandler.findListHardly;
import static com.digital.v3.lucene.DataHandler.write;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.Term;
import org.springframework.stereotype.Component;

import com.digital.v3.schema.Cart;
import com.digital.v3.schema.CartProduct;

@Component
public class CartService {
	
	@Resource
	InventoryService inventorySvc;

	/* 장바구니에 상품 등록 */
	public boolean cartProductWrite (long personId, CartProduct cartProduct) throws Exception {
		try {
			// cartProduct 중복 여부 확인 - 중복 상품이 있으면 담은 수량 합하기
			List<CartProduct> cartProductList = cartProductListSearch(personId, cartProduct);
			int cartQuantity = 0;
			for (CartProduct product : cartProductList) {
				cartQuantity += product.getQuantity();
			}

			// 입력 수량 유효성 검사 - 중복 상품의 담은 수량을 입력 수량에 더한 값으로 계산
			cartQuantity += cartProduct.getQuantity();
			if (!inventorySvc.inventoryQuantityCheck(cartProduct.getProductId(), cartQuantity)) {
				throw new Exception("상품 ID: " + cartProduct.getProductId() + "의 입력 수량이 재고 수량을 초과합니다.");
			}
	
			// 유효한 입력 수량이면 write
			Document cartProductDoc = new Document();
			
			cartProductDoc.add(new TextField("cartpersonid", "" + personId, Store.YES));
			cartProductDoc.add(new TextField("cartproductid", "" + cartProduct.getProductId(), Store.YES));
			cartProductDoc.add(new TextField("cartquantity", "" + cartProduct.getQuantity(), Store.YES));
			
			write(cartProductDoc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 장바구니 상품 삭제 */
	public boolean cartProductDelete (long personId, CartProduct cartProduct) throws Exception {
		try {
			// cartProduct 존재 여부 확인
			if (cartProductSearch(personId, cartProduct).getProductId() == 0) {
				throw new Exception("장바구니에 해당하는 상품이 없습니다."); 
			}
			
			// 존재하면 delete
			Term term1 = new Term("cartpersonid", "" + personId);
			Term term2 = new Term("cartproductid", "" + cartProduct.getProductId());
			Term term3 = new Term("cartquantity", "" + cartProduct.getQuantity());
			
			delete(term1, term2, term3);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 장바구니 상품 검색 */
	public CartProduct cartProductSearch (long personId, CartProduct cartProduct) throws Exception {
		
		Term term1 = new Term("cartpersonid", "" + personId);
		Term term2 = new Term("cartproductid", "" + cartProduct.getProductId());
		Term term3 = new Term("cartquantity", "" + cartProduct.getQuantity());
		
		Document cartProductDoc = findHardly(term1, term2, term3);
		
		cartProduct = new CartProduct();
		if (cartProductDoc != null) {
			cartProduct.setProductId(Long.parseLong(cartProductDoc.get("cartproductid")));
			cartProduct.setQuantity(Long.parseLong(cartProductDoc.get("cartquantity")));
		}

		return cartProduct;
	}
	
	/* 장바구니 중복 상품 목록 검색 */
	public List<CartProduct> cartProductListSearch (long personId, CartProduct cartProduct) throws Exception {
		
		Term term1 = new Term("cartpersonid", "" + personId);
		Term term2 = new Term("cartproductid", "" + cartProduct.getProductId());
		
		List<Document> cartProductDocList = findListHardly(term1, term2);
		
		List<CartProduct> cartProductList = new ArrayList<CartProduct>();
		for (Document cartProductDoc : cartProductDocList) {
			cartProduct = new CartProduct();
			cartProduct.setProductId(Long.parseLong(cartProductDoc.get("cartproductid")));
			cartProduct.setQuantity(Long.parseLong(cartProductDoc.get("cartquantity")));
			
			cartProductList.add(cartProduct);
		}

		return cartProductList;
	}
	
	/* 장바구니 검색 */
	public Cart cartSearch (long personId) throws Exception {
		
		String key = "cartpersonid";
		String value = "" + personId;
		
		List<Document> cartDoc = findListHardly(key, value);
		
		Cart cart = new Cart();
		List<CartProduct> cartProductList = new ArrayList<CartProduct>();
		for (Document cartProductDoc : cartDoc) {
			CartProduct cartProduct = new CartProduct();
			cartProduct.setProductId(Long.parseLong(cartProductDoc.get("cartproductid")));
			cartProduct.setQuantity(Long.parseLong(cartProductDoc.get("cartquantity")));

			cartProductList.add(cartProduct);
		}
		
		cart.setCart(cartProductList);
		
		return cart;
	}
	
}
