package com.digital.v3.sql.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.digital.v3.sql.vo.PartyAddressVO;
import com.digital.v3.sql.vo.PartyPhoneVO;
import com.digital.v3.sql.vo.PersonVO;

@Mapper
public interface PersonMapper {

	public void createPerson(PersonVO personVo);
	
	public PersonVO getPersonByName(String personName);
	
	public PersonVO getPersonById(long personId);
	
	public void createPartyAddress(PartyAddressVO partyAddressVo);
	
	public int isExistPartyAddress(PartyAddressVO partyAddressVo);
	
	public void deletePartyAddress(PartyAddressVO partyAddressVo);
	
	public void createPartyPhone(PartyPhoneVO partyPhoneVo);
	
	public int isExistPartyPhone(PartyPhoneVO partyPhoneVo);
	
	public void deletePartyPhone(PartyPhoneVO partyPhoneVo);
	
}
