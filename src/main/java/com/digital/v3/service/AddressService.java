package com.digital.v3.service;

import static com.digital.v3.lucene.DataHandler.findHardly;
import static com.digital.v3.lucene.DataHandler.write;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.springframework.stereotype.Component;

import com.digital.v3.schema.Address;

@Component
public class AddressService {

	/* 주소 등록 */
	public boolean addressWrite (Address address) throws Exception {

		try {
			// address 중복 여부 확인
			if (addressSearch("addressdetail", address.getAddressDetail()).getAddressDetail() != null) {
				throw new Exception("이미 등록된 주소입니다."); 
			} 
	
			// 중복이 아니면 write
			address.setAddressId(System.currentTimeMillis());
			Document addressDoc = new Document();
			
			addressDoc.add(new TextField("addressid", "" + address.getAddressId(), Store.YES));
			addressDoc.add(new TextField("addressdetail", "" + address.getAddressDetail(), Store.YES));
			
			write(addressDoc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 주소 검색 */
	public Address addressSearch (String key, String value) throws Exception {
		
		Document addressDoc = findHardly(key, value);
		
		Address address = new Address();
		if (addressDoc != null) {
			address.setAddressId(Long.parseLong(addressDoc.get("addressid")));
			address.setAddressDetail(addressDoc.get("addressdetail"));
		}
		
		return address;
	}
	
}
