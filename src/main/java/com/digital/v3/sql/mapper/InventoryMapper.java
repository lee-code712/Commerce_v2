package com.digital.v3.sql.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.digital.v3.sql.vo.InventoryVO;

@Mapper
public interface InventoryMapper {

	public void createInventory(InventoryVO inventoryVO);
	
	public InventoryVO getInventoryById(long productId);
	
	public InventoryVO getInventoryByName(String productName); 
	
	public void updateInventoryQuantity(InventoryVO inventoryVO);
	
}
