package com.digital.v2.service;

import static com.digital.v2.lucene.DataHandler.findHardly;
import static com.digital.v2.lucene.DataHandler.write;
import static com.digital.v2.lucene.DataHandler.update;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.document.Field.Store;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Inventory;
import com.digital.v2.schema.Product;

@Component
public class InventoryService {
	
	@Resource
	ProductService productSvc;

	/* 재고 등록 서비스 - 재고가 등록되어 있는 경우 재고 변경 서비스 호출 */
	public boolean inventoryWrite (Inventory inventory) throws Exception {
		
		try {
			// inventory 중복 여부 확인
			if (inventorySearch("inventoryid", "" + inventory.getInventoryId()).getInventoryId() != 0) {
				// 중복이면 update
				inventoryUpdate(inventory);
				return true;
			}
			
			// 중복이 아니면 write
			Document doc = new Document();
			
			doc.add(new TextField("inventoryid", "" + inventory.getInventoryId(), Store.YES));
			doc.add(new TextField("quantity", "" + inventory.getQuantity(), Store.YES));
			
			write(doc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 재고 변경 서비스 */
	public boolean inventoryUpdate (Inventory inventory) throws Exception {
		
		try {
			Term updateTerm = new Term("inventoryid", "" + inventory.getInventoryId());
			Document newDoc = new Document();
			
			newDoc.add(new TextField("inventoryid", "" + inventory.getInventoryId(), Store.YES));
			newDoc.add(new TextField("quantity", "" + inventory.getQuantity(), Store.YES));
			
			update(newDoc, updateTerm);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 재고 검색 서비스 */
	public Inventory inventorySearch (String key, String value) throws Exception {
		
		Document doc = findHardly(key, value);
		
		Inventory inventory = new Inventory();
		if (doc != null) {
			inventory.setInventoryId(Long.parseLong(doc.get("inventoryid")));
			inventory.setQuantity(Long.parseLong(doc.get("quantity")));
		}
		
		return inventory;
	}
	
	/* 상품 정보를 이용한 재고 검색 서비스 */
	public Inventory inventorySearchByProduct (String productKey, String productValue) throws Exception {
		
		String key = "inventoryid";
		String value;
		
		Product product = productSvc.productSearch(productKey, productValue);
		
		Inventory inventory = new Inventory();
		if (product.getProductName() != null) {
			value = "" + product.getInventoryId();
			
			Document doc = findHardly(key, value);
			
			if (doc != null) {
				inventory.setInventoryId(Long.parseLong(doc.get("inventoryid")));
				inventory.setQuantity(Long.parseLong(doc.get("quantity")));
			}
		}
		
		return inventory;
	}

	/* 상품에 대한 입력 수량 유효성 검증 */
	public boolean inventoryQuantityCheck (long productId, long quantity) throws Exception {
		
		Inventory inventory = inventorySearchByProduct("productid", "" + productId);
		
		if (inventory.getQuantity() - quantity < 0) {
			return false;
		}
		return true;
	}
}
