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

	public boolean inventoryWrite (Inventory inventory) throws Exception {
		
		try {
			// inventory 중복 여부 확인
			if (inventorySearchById(inventory.getInventoryId()).getInventoryId() != 0) {
				throw new Exception("상품에 대한 재고가 이미 등록되어 있습니다.");
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
	
	public Inventory inventorySearch (String productName) throws Exception {
		
		String key = "inventoryid";
		String value;
		
		Product product = productSvc.productSearch(productName);
		
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
	
	public Inventory inventorySearchById (long inventoryId) throws Exception {
		
		String key = "inventoryid";
		String value = "" + inventoryId;
		
		Document doc = findHardly(key, value);
		
		Inventory inventory = new Inventory();
		if (doc != null) {
			inventory.setInventoryId(Long.parseLong(doc.get("inventoryid")));
			inventory.setQuantity(Long.parseLong(doc.get("quantity")));
		}
		
		return inventory;
	}
	
	public Inventory inventorySearchByProductId (long productId) throws Exception {
		
		String key = "inventoryid";
		String value;
		
		Product product = productSvc.productSearchById(productId);
		
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
	
}
