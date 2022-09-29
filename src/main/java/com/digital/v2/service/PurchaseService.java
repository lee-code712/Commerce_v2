package com.digital.v2.service;

import static com.digital.v2.lucene.DataHandler.write;

import javax.annotation.Resource;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.springframework.stereotype.Component;

import com.digital.v2.schema.Person;
import com.digital.v2.schema.Purchase;

@Component
public class PurchaseService {
	
	@Resource
	PersonService personSvc;

	public boolean writeCart(Purchase purchase, String loginId) throws Exception {
		
		try {
			Person person = personSvc.personSearch(loginId);
			
			Document doc = new Document();
			
			doc.add(new TextField("cartproductid", "" + purchase.getProductId(), Store.YES));
			doc.add(new TextField("cartpurchasenumber", "" + purchase.getPurchaseNumber(), Store.YES));
			doc.add(new TextField("cartpersonid", "" + person.getPersonId(), Store.YES));
			
			write(doc);
			return true;
		} catch (Exception e) {
			throw e;
		}
	}
}
