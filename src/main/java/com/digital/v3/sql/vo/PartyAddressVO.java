package com.digital.v3.sql.vo;

import lombok.Data;

@Data
public class PartyAddressVO {

	private long personId;
	private long addressId;
	
	public void setPersonId(long personId) {
		this.personId = personId;
	}
	
	public long getPersonId() {
		long personId = this.personId;
		return personId;
	}

	public void setAddressId(long addressId) {
		this.addressId = addressId;
	}
	
	public long getAddressId() {
		long addressId = this.addressId;
		return addressId;
	}
	
}
