package com.digital.v2.controller;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digital.v2.schema.ErrorMsg;
import com.digital.v2.schema.Inventory;
import com.digital.v2.service.InventoryService;
import com.digital.v2.utils.ExceptionUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "재고", description = "Inventory Related API")
@RequestMapping(value = "/rest/inventory")
public class InventoryController {

	@Resource
	InventoryService inventorySvc;
	
	/**
	 * @description 재고 등록
	 * @params inventory: 재고 정보 (inventoryId, quantity)
	 */
	@RequestMapping(value = "/write", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "재고 등록", notes = "특정 상품의 재고를 등록하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Inventory.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> inventoryWrite (@Parameter(name = "재고 정보", required = true) @RequestBody Inventory inventory) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		Inventory resInventory = new Inventory();
		try {
			if (inventorySvc.inventoryWrite(inventory)) {
				resInventory = inventorySvc.inventorySearch("inventoryid", "" + inventory.getInventoryId());
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<Inventory>(resInventory, header, HttpStatus.valueOf(200));
	}
	
	/**
	 * @description 재고 검색
	 * @params productName: 검색 키워드
	 */
	@RequestMapping(value = "/inquiry/{productName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "재고 검색", notes = "상품명으로 상품 재고를 검색하는 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Inventory.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> productSearch (@Parameter(name = "상품명", required = true) @PathVariable String productName) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		try {
			Inventory inventory = inventorySvc.inventorySearchByProduct("productname", productName);
			return new ResponseEntity<Inventory>(inventory, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}
	
	/**
	 * @description 재고 수량 변경
	 * @params inventory: 변경한 재고 정보 (inventoryId, quantity)
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "재고 수량 변경", notes = "특정 상품의 재고 수량을 변경하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Inventory.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> inventoryUpdate (@Parameter(name = "재고 정보", required = true) @RequestBody Inventory inventory) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		Inventory resInventory = new Inventory();
		try {
			if (inventorySvc.inventoryWrite(inventory)) {
				resInventory = inventorySvc.inventorySearch("inventoryid", "" + inventory.getInventoryId());
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<Inventory>(resInventory, header, HttpStatus.valueOf(200));
	}
	
}
