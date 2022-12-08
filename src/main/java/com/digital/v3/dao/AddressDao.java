package com.digital.v3.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.digital.v3.sql.mapper.AddressMapper;
import com.digital.v3.sql.vo.AddressVO;

@Repository
public class AddressDao {
	
	@Autowired AddressMapper addressMapper;

	public void createAddress(AddressVO addressVo) {
		addressMapper.createAddress(addressVo);
	}
	
	public AddressVO getAddressByDetail(String addressDetail) {
		return addressMapper.getAddressByDetail(addressDetail);
	}
	
}
