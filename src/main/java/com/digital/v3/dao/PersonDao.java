package com.digital.v3.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.digital.v3.sql.mapper.PersonMapper;
import com.digital.v3.sql.vo.PartyAddressVO;
import com.digital.v3.sql.vo.PartyPhoneVO;
import com.digital.v3.sql.vo.PersonVO;

@Repository
public class PersonDao {

	@Autowired
	PersonMapper personMapper;

	public void createPerson(PersonVO personVo) {
		personMapper.createPerson(personVo);
	}

	public PersonVO getPersonByName(String personName) {
		return personMapper.getPersonByName(personName);
	}

	public PersonVO getPersonById(long personId) {
		return personMapper.getPersonById(personId);
	}

	public void createPartyAddress(PartyAddressVO partyAddressVo) {
		personMapper.createPartyAddress(partyAddressVo);
	}

	public int isExistPartyAddress(PartyAddressVO partyAddressVo) {
		return personMapper.isExistPartyAddress(partyAddressVo);
	}

	public void deletePartyAddress(PartyAddressVO partyAddressVo) {
		personMapper.deletePartyAddress(partyAddressVo);
	}

	public void createPartyPhone(PartyPhoneVO partyPhoneVo) {
		personMapper.createPartyPhone(partyPhoneVo);
	}

	public int isExistPartyPhone(PartyPhoneVO partyPhoneVo) {
		return personMapper.isExistPartyPhone(partyPhoneVo);
	}

	public void deletePartyPhone(PartyPhoneVO partyPhoneVo) {
		personMapper.deletePartyPhone(partyPhoneVo);
	}
	
}
