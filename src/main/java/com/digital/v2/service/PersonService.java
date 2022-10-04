package com.digital.v2.service;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Address;
import com.digital.v2.schema.Person;
import com.digital.v2.schema.Phone;

import static com.digital.v2.lucene.DataHandler.findHardly;
import static com.digital.v2.lucene.DataHandler.findListHardly;
import static com.digital.v2.lucene.DataHandler.write;
import static com.digital.v2.lucene.DataHandler.delete;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

@Component
public class PersonService {
	
	@Resource
	AddressService addressSvc;
	@Resource
	PhoneService phoneSvc;

	/* 회원가입 서비스 */
	public boolean signUp (Person person) throws Exception {
		
		try {
			// person 중복 여부 확인
			if (personSearch("personname", person.getPersonName()).getPersonName() != null) {
				throw new Exception("이미 가입한 회원입니다.");
			}

			// 중복이 아니면 write
			person.setPersonId(System.currentTimeMillis());
			List<Document> docList = setPluralDoc(person);

			for (Document doc : docList) {
				write(doc);
			}		
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 로그인 서비스 */
	public Person login (Person person) throws Exception {
		
		Person findPerson = personSearch("personname", person.getPersonName());
		
		if (findPerson.getPersonName() == null) {
			return null;
		}
		else if (!person.getPassword().equals(findPerson.getPassword())) {
			throw new Exception("비밀번호가 일치하지 않습니다.");
		}
		
		return findPerson;
	}
	
	/* 회원 검색 서비스 */
	public Person personSearch (String key, String value) throws Exception {
		
		Document personDoc = findHardly(key, value);
		
		Person person = new Person();
		if (personDoc != null) {
			
			// person set
			person.setPersonId(Long.parseLong(personDoc.get("personid")));
			person.setPersonName(personDoc.get("personname"));
			person.setPassword(personDoc.get("password"));
			person.setGender(personDoc.get("gender"));
			
			// address list set
			List<Document> partyAddressDocList = getPartyAddressDocList(person);
			
			List<Address> addressList = new ArrayList<Address>();
			for (Document partyAddressDoc : partyAddressDocList) {
				Address address = addressSvc.addressSearch("addressid", partyAddressDoc.get("partyaddressid"));
				addressList.add(address);
			}

			person.setAddressList(addressList);
			
			// phone list set
			List<Document> partyPhoneDocList = getPartyPhoneDocList(person);
			
			List<Phone> phoneList = new ArrayList<Phone>();
			for (Document partyPhoneDoc : partyPhoneDocList) {
				Phone phone = phoneSvc.phoneSearch("phoneid", partyPhoneDoc.get("partyphoneid"));
				phoneList.add(phone);
			}

			person.setPhoneList(phoneList);
		}
		
		return person;
	}
	
	/* 회원 주소 등록 서비스 */
	public boolean partyAddressWrite (long personId, long addressId) throws Exception {

		try {
			// party address 중복 여부 확인
			if (getPartyAddressDoc(personId, addressId) != null) {
				throw new Exception("회원 정보에 이미 등록된 주소입니다."); 
			}

			// 중복이 아니면 write
			Document doc = new Document();

			doc.add(new TextField("partyaddressid", "" + addressId, Store.YES));
			doc.add(new TextField("partyaddresspersonid", "" + personId, Store.YES));
			
			write(doc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 회원 주소 삭제 서비스 */
	public boolean partyAddressDelete (long personId, long addressId) throws Exception {
		
		try {
			// party address 존재 여부 확인
			if (getPartyAddressDoc(personId, addressId) == null) {
				throw new Exception("회원 정보에 해당 주소가 없습니다."); 
			} 
	
			// 존재하면 delete
			Term term1 = new Term("partyaddressid", "" + addressId);
			Term term2 = new Term("partyaddresspersonid", "" + personId);
			
			delete(term1, term2);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 회원 전화번호 등록 서비스 */
	public boolean partyPhoneWrite (long personId, long phoneId) throws Exception {

		try {
			// party phone 중복 여부 확인
			if (getPartyPhoneDoc(personId, phoneId) != null) {
				throw new Exception("회원정보에 이미 등록된 전화번호입니다."); 
			} 
	
			// 중복이 아니면 write
			Document doc = new Document();

			doc.add(new TextField("partyphoneid", "" + phoneId, Store.YES));
			doc.add(new TextField("partyphonepersonid", "" + personId, Store.YES));
			
			write(doc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 회원 전화번호 삭제 서비스 */
	public boolean partyPhoneDelete (long personId, long phoneId) throws Exception {
		
		try {
			// party phone 존재 여부 확인
			if (getPartyPhoneDoc(personId, phoneId) == null) {
				throw new Exception("회원정보에 해당 전화번호가 없습니다."); 
			} 
	
			// 존재하면 delete
			Term term1 = new Term("partyphoneid", "" + phoneId);
			Term term2 = new Term("partyphonepersonid", "" + personId);
			
			delete(term1, term2);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/* 회원 ID (token) 유효성 검사 */
	public boolean personIdCheck (String token) throws Exception {
		
		String key = "personid";
		String value = token;

		if (findHardly(key, value) != null) {
			return true;
		}
		return false;
	}
	
	public List<Document> setPluralDoc (Person person) {

		List<Document> docList = new ArrayList<Document>();
		
		// person doc add
		Document personDoc = new Document();

		personDoc.add(new TextField("personid", "" + person.getPersonId(), Store.YES));
		personDoc.add(new TextField("personname", "" + person.getPersonName(), Store.YES));
		personDoc.add(new TextField("password", "" + person.getPassword(), Store.YES));
		personDoc.add(new TextField("gender", "" + person.getGender(), Store.YES));

		docList.add(personDoc);
		
		// address & party address doc add
		List<Address> addressList = person.getAddressList();
		for (Address address : addressList) {
			try {
				if (addressSvc.addressWrite(address)) {
					docList.add(setPartyAddressDoc(person, addressSvc.addressSearch("addressdetail", address.getAddressDetail())));
				}
			} catch (Exception e) {
				try {
					docList.add(setPartyAddressDoc(person, addressSvc.addressSearch("addressdetail", address.getAddressDetail())));
				} catch (Exception e1) {}
			}
		}
		
		// phone & party phone doc add
		List<Phone> phoneList = person.getPhoneList();
		for (Phone phone : phoneList) {
			try {
				if (phoneSvc.phoneWrite(phone)) {
					docList.add(setPartyPhoneDoc(person, phoneSvc.phoneSearch("phonenumber", phone.getPhoneNumber())));
				}
			} catch (Exception e) {
				try {
					docList.add(setPartyPhoneDoc(person, phoneSvc.phoneSearch("phonenumber", phone.getPhoneNumber())));
				} catch (Exception e1) {}
			}
		}

		return docList;
	}
	
	public Document setPartyAddressDoc (Person person, Address address) {

		Document partyAddressDoc = new Document();

		partyAddressDoc.add(new TextField("partyaddressid", "" + address.getAddressId(), Store.YES));
		partyAddressDoc.add(new TextField("partyaddresspersonid", "" + person.getPersonId(), Store.YES));

		return partyAddressDoc;
	}
	
	public Document getPartyAddressDoc (long personId, long addressId) {
		
		Term term1 = new Term("partyaddressid", "" + addressId);
		Term term2 = new Term("partyaddresspersonid", "" + personId);
		Document partyAddressDoc = findHardly(term1, term2);
		
		return partyAddressDoc;
	}
	
	public List<Document> getPartyAddressDocList (Person person) {

		List<Document> partyAddressDocList = findListHardly("partyaddresspersonid", "" + person.getPersonId());
		
		return partyAddressDocList;
	}
	
	public Document setPartyPhoneDoc (Person person, Phone phone) {

		Document partyPhoneDoc = new Document();

		partyPhoneDoc.add(new TextField("partyphoneid", "" + phone.getPhoneId(), Store.YES));
		partyPhoneDoc.add(new TextField("partyphonepersonid", "" + person.getPersonId(), Store.YES));

		return partyPhoneDoc;
	}
	
	public Document getPartyPhoneDoc (long personId, long phoneId) {
		
		Term term1 = new Term("partyphoneid", "" + phoneId);
		Term term2 = new Term("partyphonepersonid", "" + personId);
		Document partyPhoneDoc = findHardly(term1, term2);
		
		return partyPhoneDoc;
	}
	
	public List<Document> getPartyPhoneDocList (Person person) {

		List<Document> partyPhoneDocList = findListHardly("partyphonepersonid", "" + person.getPersonId());

		return partyPhoneDocList;
	}
	
	
	
}
