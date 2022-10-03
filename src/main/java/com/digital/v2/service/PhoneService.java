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
	
	public boolean phoneWrite (Phone phone) throws Exception {

		try {
			// phone 중복 여부 확인
			if (phoneSearch("phonenumber", phone.getPhoneNumber()).getPhoneNumber() != null) {
				throw new Exception("이미 등록된 전화번호입니다."); 
			}
			
			// 중복이 아니면 write
			phone.setPhoneId(System.currentTimeMillis());
			Document doc = new Document();
			
			doc.add(new TextField("phoneid", "" + phone.getPhoneId(), Store.YES));
			doc.add(new TextField("phonenumber", "" + phone.getPhoneNumber(), Store.YES));
				
			write(doc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Phone phoneSearch (String key, String value) throws Exception {
		
		Document doc = findHardly(key, value);
		
		Phone phone = new Phone();
		if (doc != null) {
			phone.setPhoneId(Long.parseLong(doc.get("phoneid")));
			phone.setPhoneNumber(doc.get("phonenumber"));
		}
		
		return phone;
	}
	
}
