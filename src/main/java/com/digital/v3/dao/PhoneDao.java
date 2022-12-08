package com.digital.v3.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.digital.v3.sql.mapper.PhoneMapper;
import com.digital.v3.sql.vo.PhoneVO;

@Repository
public class PhoneDao {
	
	@Autowired
	PhoneMapper phoneMapper;

	public void createPhone(PhoneVO phoneVo) {
		phoneMapper.createPhone(phoneVo);
	}
	
	public PhoneVO getPhoneByNumber(String phoneNumber) {
		return phoneMapper.getPhoneByNumber(phoneNumber);
	}
	
}
