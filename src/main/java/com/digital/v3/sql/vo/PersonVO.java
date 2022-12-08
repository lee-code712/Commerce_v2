package com.digital.v3.sql.vo;

import java.util.List;

import lombok.Data;

@Data
public class PersonVO {

	private long personId;
	private String personName;
	private String gender;
	private String password;
	private List<AddressVO> addressVoList;
	private List<PhoneVO> phoneVoList;
	
	public void setPersonId(long personId) {
		this.personId = personId;
	}
	
	public long getPersonId() {
		long personId = this.personId;
		return personId;
	}

	public void setPersonName(String personName) {
		this.personName = personName;
	}
	
	public String getPersonName() {
		String personName = this.personName;
		return personName;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
	
	public String getGender() {
		String gender = this.gender;
		return gender;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		String password = this.password; 
		return password;
	}

	public void setAddressVoList(List<AddressVO> addressVoList) {
		this.addressVoList = addressVoList;
	}
	
	public List<AddressVO> getAddressVoList() {
		List<AddressVO> addressVoList = this.addressVoList;
		return addressVoList;
	}

	public void setPhoneVoList(List<PhoneVO> phoneVoList) {
		this.phoneVoList = phoneVoList;
	}

	public List<PhoneVO> getPhoneVoList() {
		List<PhoneVO> phoneVoList = this.phoneVoList;
		return phoneVoList;
	}
	
}
