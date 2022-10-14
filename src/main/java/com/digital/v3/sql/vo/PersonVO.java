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
	
}
