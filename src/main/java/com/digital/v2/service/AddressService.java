package com.digital.v2.service;

import static com.digital.v2.lucene.DataHandler.findHardly;
import static com.digital.v2.lucene.DataHandler.write;


import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Address;

@Component
public class AddressService {

	public boolean addressWrite (Address address) throws Exception {

		try {
			// address 중복 여부 확인
			if (addressSearch(address.getAddressDetail()).getAddressDetail() != null) {
				throw new Exception("이미 등록된 주소입니다."); 
			} 
	
			// 중복이 아니면 write
			address.setAddressId(System.currentTimeMillis());
			Document doc = new Document();
			
			doc.add(new TextField("addressid", "" + address.getAddressId(), Store.YES));
			doc.add(new TextField("addressdetail", "" + address.getAddressDetail(), Store.YES));
			
			write(doc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public Address addressSearch (String addressDetail) throws Exception {
		
		String key = "addressdetail";
		String value = addressDetail;
		
		Document doc = findHardly(key, value);
		
		Address address = new Address();
		if (doc != null) {
			address.setAddressId(Long.parseLong(doc.get("addressid")));
			address.setAddressDetail(doc.get("addressdetail"));
		}
		
		return address;
	}
	
	public Address addressSearchById (long addressId) throws Exception {
		
		String key = "addressid";
		String value = "" + addressId;
		
		Document doc = findHardly(key, value);
		
		Address addr = new Address();
		if (doc != null) {
			addr.setAddressId(Long.parseLong(doc.get("addressid")));
			addr.setAddressDetail(doc.get("addressdetail"));
		}
		
		return addr;
	}
	
}
