package com.digital.v2.service;

import static com.digital.v2.lucene.DataHandler.findHardly;
import static com.digital.v2.lucene.DataHandler.write;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Phone;

@Component
public class PhoneService {
	
	/* 전화번호 등록 서비스 */
	public boolean phoneWrite (Phone phone) throws Exception {

		try {
			// phone 중복 여부 확인
			if (phoneSearch("phonenumber", phone.getPhoneNumber()).getPhoneNumber() != null) {
				throw new Exception("이미 등록된 전화번호입니다."); 
			}
			
			// 중복이 아니면 write
			phone.setPhoneId(System.currentTimeMillis());
			Document phoneDoc = new Document();
			
			phoneDoc.add(new TextField("phoneid", "" + phone.getPhoneId(), Store.YES));
			phoneDoc.add(new TextField("phonenumber", "" + phone.getPhoneNumber(), Store.YES));
				
			write(phoneDoc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 전화번호 검색 서비스 */
	public Phone phoneSearch (String key, String value) throws Exception {
		
		Document phoneDoc = findHardly(key, value);
		
		Phone phone = new Phone();
		if (phoneDoc != null) {
			phone.setPhoneId(Long.parseLong(phoneDoc.get("phoneid")));
			phone.setPhoneNumber(phoneDoc.get("phonenumber"));
		}
		
		return phone;
	}
	
}
