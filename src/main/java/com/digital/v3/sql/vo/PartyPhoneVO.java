package com.digital.v3.sql.vo;

import lombok.Data;

@Data
public class PartyPhoneVO {

	private long personId;
	private long phoneId;
	
	public void setPersonId(long personId) {
		this.personId = personId;
	}
	
	public long getPersonId() {
		long personId = this.personId;
		return personId;
	}

	public void setPhoneId(long phoneId) {
		this.phoneId = phoneId;
	}
	
	public long getPhoneId() {
		long phoneId = this.phoneId;
		return phoneId;
	}
	
}
