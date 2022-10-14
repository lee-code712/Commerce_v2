package com.digital.v3.sql.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.digital.v3.sql.vo.AddressVO;

@Mapper
public interface AddressMapper {

	public void createAddress(AddressVO addressVo);
	
	public AddressVO getAddressByDetail(String addressDetail);
	
}
