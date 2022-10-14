package com.digital.v3.sql.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.digital.v3.sql.vo.PhoneVO;

@Mapper
public interface PhoneMapper {

	public void createPhone(PhoneVO phoneVo);
	
	public PhoneVO getPhoneByNumber(String phoneNumber);
	
}
