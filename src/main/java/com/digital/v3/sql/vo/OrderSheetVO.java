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
	
}
