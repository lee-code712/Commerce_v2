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
	
	/* 카테고리 등록 */
	public boolean categoryWrite (Category category) throws Exception {
		
		try {
			// category 중복 여부 확인
			if (categorySearch("categoryname", category.getCategoryName()).getCategoryName() != null) {
				throw new Exception("이미 등록된 카테고리입니다."); 
			}
			
			// 중복이 아니면 write
			category.setCategoryId(System.currentTimeMillis());
			Document categoryDoc = new Document();
			
			categoryDoc.add(new TextField("categoryid", "" + category.getCategoryId(), Store.YES));
			categoryDoc.add(new TextField("categoryname", "" + category.getCategoryName(), Store.YES));
			
			write(categoryDoc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 카테고리 검색 */
	public Category categorySearch (String key, String value) throws Exception {
		
		Document categoryDoc = findHardly(key, value);
		
		Category category = new Category();
		if (categoryDoc != null) {
			category.setCategoryId(Long.parseLong(categoryDoc.get("categoryid")));
			category.setCategoryName(categoryDoc.get("categoryname"));
		}
		
		return category;
	}

}
