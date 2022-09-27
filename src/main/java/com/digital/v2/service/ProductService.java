package com.digital.v2.service;

import static com.digital.v2.lucene.DataHandler.findHardly;
import static com.digital.v2.lucene.DataHandler.wildCardQuery;
import static com.digital.v2.lucene.DataHandler.write;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Category;
import com.digital.v2.schema.Inventory;
import com.digital.v2.schema.Product;
import com.digital.v2.schema.ProductList;

@Component
public class ProductService {
	
	@Resource
	CategoryService categorySvc;
	@Resource
	InventoryService inventorySvc;
	
	public boolean productWrite (Product product) throws Exception {
		
		try {
			// product 중복 여부 확인
			if (productSearch(product.getProductName()).getProductName() != null) {
				throw new Exception("이미 등록된 상품입니다.");
			}
			
			// 중복이 아니면 write
			product.setProductId(System.currentTimeMillis());
			List<Document> docList = setPluralDoc(product);
			
			for (Document document : docList) {
				write(document);
			}		
			return true;
		} catch (Exception e) {
			throw e;
		}
	}


	public Product productSearch (String productName) throws Exception {
		
		String key = "productname";
		String value = productName;
		
		Document productDoc = findHardly(key, value);
		
		Product product = new Product();
		if (productDoc != null) {
			
			// product set
			product.setProductId(Long.parseLong(productDoc.get("productid")));
			product.setPrice(Long.parseLong(productDoc.get("price")));
			product.setProductName(productDoc.get("productname"));
			
			// category set
			Document categoryDoc = getPartyCategory(product);
			Category category = categorySvc.categorySearchById(Long.parseLong(categoryDoc.get("partycategoryid")));
			
			product.setCategory(category);
			
			// inventory set
			Inventory inventory = inventorySvc.inventorySearchByProductId(product.getProductId());
			
			product.setInventory(inventory);
		}
		
		return product;
	}

	public ProductList productSearchByKeyword (String keyword) throws Exception {
		
		String key = "productname";
		String value = keyword;
		
		List<Document> productDocList = wildCardQuery(key, value);
		
		ProductList productList = new ProductList(); 
		List<Product> products = new ArrayList<Product>();
		for (Document productDoc : productDocList) {
			
			Product product = new Product();
			if (productDoc != null) {
				
				// product set
				product.setProductId(Long.parseLong(productDoc.get("productid")));
				product.setPrice(Long.parseLong(productDoc.get("price")));
				product.setProductName(productDoc.get("productname"));
				
				// category set
				Document categoryDoc = getPartyCategory(product);
				Category category = categorySvc.categorySearchById(Long.parseLong(categoryDoc.get("partycategoryid")));
				
				product.setCategory(category);
				
				// inventory set
				Inventory inventory = inventorySvc.inventorySearchByProductId(product.getProductId());
				
				product.setInventory(inventory);
			}
			products.add(product);
		}
		
		productList.setProducts(products);
		
		return productList;
	}
	
	public List<Document> setPluralDoc (Product product) {
		
		List<Document> docList = new ArrayList<Document>();
		
		// product doc add
		Document productDoc = new Document();
		
		productDoc.add(new TextField("productid", "" + product.getProductId(), Store.YES));
		productDoc.add(new TextField("price", "" + product.getPrice(), Store.YES));
		productDoc.add(new TextField("productname", "" + product.getProductName(), Store.YES));
		
		docList.add(productDoc);
		
		// category & party category doc add
		Category category = product.getCategory();
		try {
			if (categorySvc.categoryWrite(category)) {
				docList.add(setPartyCategory(product, categorySvc.categorySearch(category.getCategoryName())));
			}
		} catch (Exception e) {
			try {
				docList.add(setPartyCategory(product, categorySvc.categorySearch(category.getCategoryName())));
			} catch (Exception e1) {}
		}
		
		// inventory doc add
		Inventory inventory = product.getInventory();
		inventory.setProductId(product.getProductId());
		try {
			inventorySvc.inventoryWrite(inventory);
		} catch (Exception e) {}
		
		return docList;
	}


	public Document setPartyCategory (Product product, Category category) {
		
		Document doc = new Document();
		
		doc.add(new TextField("partycategoryid", "" + category.getCategoryId(), Store.YES));
		doc.add(new TextField("partycategoryproductid", "" + product.getProductId(), Store.YES));
		
		return doc;
	}
	
	public Document getPartyCategory (Product product) {

		Document doc = findHardly("partycategoryproductid", "" + product.getProductId());

		return doc;
	}
}
