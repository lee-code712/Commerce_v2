package com.digital.v3.sql.vo;

import java.util.List;

import lombok.Data;

@Data
public class OrderSheetVO {

	private long orderId;
	private long personId;
	private AddressVO addressVo;
	private PhoneVO phoneVo;
	private List<PartyProductVO> partyProductVoList;
	private String purchaseDate;
	
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	
	public long getOrderId() {
		long orderId = this.orderId;
		return orderId;
	}

	public void setPersonId(long personId) {
		this.personId = personId;
	}
	
	public long getPersonId() {
		long personId = this.personId;
		return personId;
	}

	public void setAddressVo(AddressVO addressVo) {
		this.addressVo = addressVo;
	}
	
	public AddressVO getAddressVo() {
		AddressVO addressVo = this.addressVo;
		return addressVo;
	}

	public void setPhoneVo(PhoneVO phoneVo) {
		this.phoneVo = phoneVo;
	}
	
	public PhoneVO getPhoneVo() {
		PhoneVO phoneVo = this.phoneVo;
		return phoneVo;
	}

	public void setPartyProductVoList(List<PartyProductVO> partyProductVoList) {
		this.partyProductVoList = partyProductVoList;
	}
	
	public List<PartyProductVO> getPartyProductVoList() {
		List<PartyProductVO> partyProductVoList = this.partyProductVoList;
		return partyProductVoList;
	}

	public void setPurchaseDate(String purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	
	public String getPurchaseDate() {
		String purchaseDate = this.purchaseDate;
		return purchaseDate;
	}
	
}
