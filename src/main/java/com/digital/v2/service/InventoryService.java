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
			inventory.setInventoryId(System.currentTimeMillis());
			Document doc = new Document();
			
			doc.add(new TextField("inventoryid", "" + inventory.getInventoryId(), Store.YES));
			doc.add(new TextField("inventoryproductid", "" + inventory.getProductId(), Store.YES));
			doc.add(new TextField("quantity", "" + inventory.getQuantity(), Store.YES));
			
			write(doc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Inventory inventorySearch (String productName) throws Exception {
		
		String key = "inventoryproductid";
		String value;
		
		Product product = productSvc.productSearch(productName);
		
		Inventory inventory = new Inventory();
		if (product.getProductName() != null) {
			value = "" + product.getProductId();
			
			Document doc = findHardly(key, value);
			
			if (doc != null) {
				inventory.setInventoryId(Long.parseLong(doc.get("inventoryid")));
				inventory.setProductId(Long.parseLong(doc.get("inventoryproductid")));
				inventory.setQuantity(Long.parseLong(doc.get("quantity")));
			}
		}
		
		return inventory;
	}
	
	public Inventory inventorySearchByProductId (long productId) throws Exception {
		
		String key = "inventoryproductid";
		String value = "" + productId;
		
		Document doc = findHardly(key, value);
		
		Inventory inventory = new Inventory();
		if (doc != null) {
			inventory.setInventoryId(Long.parseLong(doc.get("inventoryid")));
			inventory.setProductId(Long.parseLong(doc.get("inventoryproductid")));
			inventory.setQuantity(Long.parseLong(doc.get("quantity")));
		}
		
		return inventory;
	}
	
	public boolean inventoryUpdate (long productId, long newQuantity) throws Exception {
		
		try {
			Inventory inventory = inventorySearchByProductId(productId);

			Term updateTerm = new Term("inventoryid", "" + inventory.getInventoryId());		
			Document newDoc = new Document();
			
			newDoc.add(new TextField("inventoryid", "" + inventory.getInventoryId(), Store.YES));
			newDoc.add(new TextField("inventoryproductid", "" + inventory.getProductId(), Store.YES));
			newDoc.add(new TextField("quantity", "" + newQuantity, Store.YES));
			
			update(newDoc, updateTerm);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
//	public boolean inventoryUpdate (long productId, long quantity) throws Exception {
//		
//		try {
//			Inventory inventory = inventorySearchByProductId(productId);
//			
//			long newQuantity = inventory.getQuantity() + quantity;
//			if (newQuantity < 0) {
//				throw new Exception("재고 수량이 " + (-newQuantity) + "개 부족합니다.");
//			} else if (newQuantity > 1000) {
//				throw new Exception("재고 수량은 1000개를 넘을 수 없습니다.");
//			}
//
//			Term updateTerm = new Term("inventoryid", "" + inventory.getInventoryId());		
//			Document newDoc = new Document();
//			
//			newDoc.add(new TextField("inventoryid", "" + inventory.getInventoryId(), Store.YES));
//			newDoc.add(new TextField("inventoryproductid", "" + inventory.getProductId(), Store.YES));
//			newDoc.add(new TextField("quantity", "" + newQuantity, Store.YES));
//			
//			update(newDoc, updateTerm);
//			return true;
//		} catch (Exception e) {
//			throw e;
//		}
//	}
	
}
