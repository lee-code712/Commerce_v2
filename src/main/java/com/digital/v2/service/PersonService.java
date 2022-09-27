package com.digital.v2.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Address;
import com.digital.v2.schema.Person;
import com.digital.v2.schema.Phone;

import static com.digital.v2.lucene.DataHandler.findHardly;
import static com.digital.v2.lucene.DataHandler.findListHardly;
import static com.digital.v2.lucene.DataHandler.write;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

@Component
public class PersonService {
	
	@Resource
	AddressService addressSvc;
	@Resource
	PhoneService phoneSvc;

	public boolean signUp (Person person) throws Exception {
		
		try {
			// person 중복 여부 확인
			if (personSearch(person.getLoginId()).getLoginId() != null) {
				throw new Exception("이미 가입된 회원정보입니다.");
			}

			// 중복이 아니면 write
			person.setPersonId(System.currentTimeMillis());
			List<Document> docList = setPluralDoc(person);

			for (Document document : docList) {
				write(document);
			}		
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	public boolean login (Person person) throws Exception {
		
		Person findPerson = personSearch(person.getLoginId());
		
		if (findPerson.getLoginId() == null) {
			return false;
		}
		else if (!person.getPassword().equals(findPerson.getPassword())) {
			throw new Exception("비밀번호가 일치하지 않습니다.");
		}
		
		return true;
	}
	
	public Person personSearch (String loginId) throws Exception {
		
		String key = "loginid";
		String value = loginId;
		
		Document personDoc = findHardly(key, value);
		
		Person person = new Person();
		if (personDoc != null) {
			
			// person set
			person.setPersonId(Long.parseLong(personDoc.get("personid")));
			person.setLoginId(personDoc.get("loginid"));
			person.setPassword(personDoc.get("password"));
			person.setGender(personDoc.get("gender"));
			person.setPersonName(personDoc.get("personname"));
			
			// address list set
			List<Document> partyAddressDocList = getPartyAddress(person);
			
			List<Address> addressList = new ArrayList<Address>();
			for (Document partyAddressDoc : partyAddressDocList) {
				Address address = addressSvc.addressSearchById(Long.parseLong(partyAddressDoc.get("partyaddressid")));
				addressList.add(address);
			}

			person.setAddressList(addressList);
			
			// phone list set
			List<Document> partyPhoneDocList = getPartyPhone(person);
			
			List<Phone> phoneList = new ArrayList<Phone>();
			for (Document partyPhoneDoc : partyPhoneDocList) {
				Phone phone = phoneSvc.phoneSearchById(Long.parseLong(partyPhoneDoc.get("partyphoneid")));
				phoneList.add(phone);
			}

			person.setPhoneList(phoneList);
		}
		
		return person;
	}
	
	public List<Document> setPluralDoc(Person person) {

		List<Document> docList = new ArrayList<Document>();
		
		// person doc add
		Document personDoc = new Document();

		personDoc.add(new TextField("personid", "" + person.getPersonId(), Store.YES));
		personDoc.add(new TextField("loginid", "" + person.getLoginId(), Store.YES));
		personDoc.add(new TextField("password", "" + person.getPassword(), Store.YES));
		personDoc.add(new TextField("personname", "" + person.getPersonName(), Store.YES));
		personDoc.add(new TextField("gender", "" + person.getGender(), Store.YES));

		docList.add(personDoc);
		
		// address & party address doc add
		List<Address> addressList = person.getAddressList();
		for (Address address : addressList) {
			try {
				if (addressSvc.addressWrite(address)) {
					docList.add(setPartyAddress(person, addressSvc.addressSearch(address.getAddressDetail())));
				}
			} catch (Exception e) {
				try {
					docList.add(setPartyAddress(person, addressSvc.addressSearch(address.getAddressDetail())));
				} catch (Exception e1) {}
			}
		}
		
		// phone & party phone doc add
		List<Phone> phoneList = person.getPhoneList();
		for (Phone phone : phoneList) {
			try {
				if (phoneSvc.phoneWrite(phone)) {
					docList.add(setPartyPhone(person, phoneSvc.phoneSearch(phone.getPhoneNumber())));
				}
			} catch (Exception e) {
				try {
					docList.add(setPartyPhone(person, phoneSvc.phoneSearch(phone.getPhoneNumber())));
				} catch (Exception e1) {}
			}
		}

		return docList;
	}
	
	public Document setPartyAddress(Person person, Address address) {

		Document doc = new Document();

		doc.add(new TextField("partyaddressid", "" + address.getAddressId(), Store.YES));
		doc.add(new TextField("partyaddresspersonid", "" + person.getPersonId(), Store.YES));

		return doc;
	}
	
	public List<Document> getPartyAddress(Person person) {

		List<Document> docList = findListHardly("partyaddresspersonid", "" + person.getPersonId());
		
		return docList;
	}
	
	public Document setPartyPhone(Person person, Phone phone) {

		Document doc = new Document();

		doc.add(new TextField("partyphoneid", "" + phone.getPhoneId(), Store.YES));
		doc.add(new TextField("partyphonepersonid", "" + person.getPersonId(), Store.YES));

		return doc;
	}
	
	public List<Document> getPartyPhone(Person person) {

		List<Document> docList = findListHardly("partyphonepersonid", "" + person.getPersonId());

		return docList;
	}

}
