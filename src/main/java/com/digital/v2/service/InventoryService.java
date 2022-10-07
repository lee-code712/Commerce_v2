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

	/* 재고 등록 */
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
	
	/* 재고 변경 */
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
	
	/* 재고 검색 */
	public Inventory inventorySearch (String key, String value) throws Exception {
		
		Document inventoryDoc = findHardly(key, value);
		
		Inventory inventory = new Inventory();
		if (inventoryDoc != null) {
			inventory.setInventoryId(Long.parseLong(inventoryDoc.get("inventoryid")));
			inventory.setQuantity(Long.parseLong(inventoryDoc.get("quantity")));
		}
		
		return inventory;
	}
	
	/* 상품 정보를 이용한 재고 검색 */
	public Inventory inventorySearchByProduct (String productKey, String productValue) throws Exception {
		
		String key = "inventoryid";
		String value;
		
		Product product = productSvc.productSearch(productKey, productValue);
		
		Inventory inventory = new Inventory();
		if (product.getProductName() != null) {
			value = "" + product.getInventoryId();
			
			Document inventoryDoc = findHardly(key, value);
			
			if (inventoryDoc != null) {
				inventory.setInventoryId(Long.parseLong(inventoryDoc.get("inventoryid")));
				inventory.setQuantity(Long.parseLong(inventoryDoc.get("quantity")));
			}
		}
		
		return inventory;
	}

	/* 상품에 대한 입력 수량 유효성 검사 */
	public boolean inventoryQuantityCheck (long productId, long quantity) throws Exception {
		
		try {
			Inventory inventory = inventorySearchByProduct("productid", "" + productId);
			
			if (inventory.getInventoryId() == 0) {
				throw new Exception("아직 재고를 등록하지 않은 상품입니다.");
			}
			if (inventory.getQuantity() - quantity < 0) {
				return false;
			}	
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
}
