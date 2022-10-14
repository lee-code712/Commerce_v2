package com.digital.v3.sql.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.digital.v3.sql.vo.OrderSheetVO;
import com.digital.v3.sql.vo.PartyProductVO;

@Mapper
public interface OrderMapper {
	
	public void createOrderSheet(OrderSheetVO orderSheetVo);
	
	public OrderSheetVO getOrderSheetByPerson(long personId);
	
	public OrderSheetVO getOrderSheetById(long orderId);
	
	public void deleteOrderSheet(long personId);
	
	public void updateOrderIdOfOrderProduct(PartyProductVO orderProductVo);
	
	public void createPurchase(long orderId);
	
	public OrderSheetVO getOrderById(long orderId);
	
	public List<OrderSheetVO> getOrderByDate(@Param("personId") long personId, @Param("date") String purchaseDate);

}
