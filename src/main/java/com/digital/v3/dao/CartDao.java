package com.digital.v3.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.digital.v3.sql.mapper.CartMapper;
import com.digital.v3.sql.vo.PartyProductVO;

@Repository
public class CartDao {
	
	@Autowired
	CartMapper cartMapper;
	
	public void createCartProduct(PartyProductVO cartProductVo) {
		cartMapper.createCartProduct(cartProductVo);
	}
	
	public List<PartyProductVO> getCartProductByPerson(long personId) {
		return cartMapper.getCartProductByPerson(personId);
	}
	
	public int isExistCartProduct(PartyProductVO cartProductVo) {
		return cartMapper.isExistCartProduct(cartProductVo);
	}
	
	public int getQuantityOfPluralCartProduct(PartyProductVO cartProductVo) {
		return cartMapper.getQuantityOfPluralCartProduct(cartProductVo);
	}
	
	public void deleteCartProduct(PartyProductVO cartProductVo) {
		cartMapper.deleteCartProduct(cartProductVo);
	}

}
