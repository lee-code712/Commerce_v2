package com.digital.v2.service;

import static com.digital.v2.lucene.DataHandler.findHardly;
import static com.digital.v2.lucene.DataHandler.write;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Category;

@Component
public class CategoryService {
	
	public boolean categoryWrite (Category category) throws Exception {
		
		try {
			// category 중복 여부 확인
			if (categorySearch("categoryname", category.getCategoryName()).getCategoryName() != null) {
				throw new Exception("이미 등록된 카테고리입니다."); 
			}
			
			// 중복이 아니면 write
			category.setCategoryId(System.currentTimeMillis());
			Document doc = new Document();
			
			doc.add(new TextField("categoryid", "" + category.getCategoryId(), Store.YES));
			doc.add(new TextField("categoryname", "" + category.getCategoryName(), Store.YES));
			
			write(doc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Category categorySearch (String key, String value) throws Exception {
		
		Document doc = findHardly(key, value);
		
		Category category = new Category();
		if (doc != null) {
			category.setCategoryId(Long.parseLong(doc.get("categoryid")));
			category.setCategoryName(doc.get("categoryname"));
		}
		
		return category;
	}

}
