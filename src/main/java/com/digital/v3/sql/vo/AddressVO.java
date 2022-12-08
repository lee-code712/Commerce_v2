package com.digital.v3.sql.vo;

import lombok.Data;

@Data
public class AddressVO {

	private long addressId;
	private String addressDetail;
	
	public void setAddressId(long addressId) {
		this.addressId = addressId;
	}
	
	public long getAddressId() {
		long addressId = this.addressId;
		return addressId;
	}

	public void setAddressDetail(String addressDetail) {
		this.addressDetail = addressDetail;
	}

	public String getAddressDetail() {
		String addressDetail = this.addressDetail;
		return addressDetail;
	}
	
}
