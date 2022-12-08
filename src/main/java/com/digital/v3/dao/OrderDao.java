package com.digital.v3.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.digital.v3.sql.mapper.OrderMapper;
import com.digital.v3.sql.vo.OrderSheetVO;
import com.digital.v3.sql.vo.PartyProductVO;

@Repository
public class OrderDao {
	
	@Autowired
	OrderMapper orderMapper;
	
	public void createOrderSheet(OrderSheetVO orderSheetVo) {
		orderMapper.createOrderSheet(orderSheetVo);
	}
	
	public OrderSheetVO getOrderSheetByPerson(long personId) {
		return orderMapper.getOrderSheetByPerson(personId);
	}
	
	public OrderSheetVO getOrderSheetById(long orderId) {
		return orderMapper.getOrderSheetById(orderId);
	}
	
	public void deleteOrderSheet(long personId) {
		orderMapper.deleteOrderSheet(personId);
	}
	
	public void updateOrderIdOfOrderProduct(PartyProductVO orderProductVo) {
		orderMapper.updateOrderIdOfOrderProduct(orderProductVo);
	}
	
	public void createPurchase(long orderId) {
		orderMapper.createPurchase(orderId);
	}
	
	public OrderSheetVO getOrderById(long orderId) {
		return orderMapper.getOrderById(orderId);
	}
	
	public List<OrderSheetVO> getOrderByDate(long personId, String purchaseDate) {
		return orderMapper.getOrderByDate(personId, purchaseDate);
	}

}
