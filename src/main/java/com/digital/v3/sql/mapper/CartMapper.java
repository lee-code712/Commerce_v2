package com.digital.v3.sql.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.digital.v3.sql.vo.PartyProductVO;

@Mapper
public interface CartMapper {

	public void createCartProduct(PartyProductVO cartProductVo);
	
	public List<PartyProductVO> getCartProductByPerson(long personId);
	
	public int isExistCartProduct(PartyProductVO cartProductVo);
	
	public int getQuantityOfPluralCartProduct(PartyProductVO cartProductVo);
	
	public void deleteCartProduct(PartyProductVO cartProductVo);
	
}
