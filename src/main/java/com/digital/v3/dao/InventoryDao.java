package com.digital.v3.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.digital.v3.sql.mapper.InventoryMapper;
import com.digital.v3.sql.vo.InventoryVO;

@Repository
public class InventoryDao {

	@Autowired
	InventoryMapper inventoryMapper;
	
	public void createInventory(InventoryVO inventoryVO) {
		inventoryMapper.createInventory(inventoryVO);
	}

	public InventoryVO getInventoryById(long productId) {
		return inventoryMapper.getInventoryById(productId);
	}

	public InventoryVO getInventoryByName(String productName) {
		return inventoryMapper.getInventoryByName(productName);
	}

	public void updateInventoryQuantity(InventoryVO inventoryVO) {
		inventoryMapper.updateInventoryQuantity(inventoryVO);
	}
	
}
