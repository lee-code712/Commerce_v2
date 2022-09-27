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
import com.digital.v2.schema.Product;
import com.digital.v2.schema.ProductList;
import com.digital.v2.service.InventoryService;
import com.digital.v2.service.ProductService;
import com.digital.v2.utils.ExceptionUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "상품", description = "Product Related API")
@RequestMapping(value = "/rest/product")
public class ProductController {
	
	@Resource
	ProductService productSvc;
	@Resource
	InventoryService inventorySvc;
	
	@RequestMapping(value = "/write", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "상품 등록", notes = "상품 등록을 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Product.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> productWrite (@Parameter(name = "상품 등록", description = "", required = true) @RequestBody Product product) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		Product resProduct = new Product();
		try {
			if (productSvc.productWrite(product)) {
				resProduct = productSvc.productSearch(product.getProductName());
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<Product>(resProduct, header, HttpStatus.valueOf(200));
	}

	@RequestMapping(value = "/inquiry/list/{keyword}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "상품 키워드 검색", notes = "특정 단어를 포함하는 상품명으로 상품을 검색하는 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = ProductList.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)	// 에러를 담고 있는 schema를 따로 생성해서 사용
	})
	public ResponseEntity<?> productListsearch (@PathVariable String keyword) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		try {
			ProductList products = productSvc.productSearchByKeyword(keyword);
			return new ResponseEntity<ProductList>(products, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}
	
	@RequestMapping(value = "/inquiry/{productName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "상품 검색", notes = "상품명으로 상품을 검색하는 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Product.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> productSearch (@PathVariable String productName) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		try {
			Product product = productSvc.productSearch(productName);
			return new ResponseEntity<Product>(product, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}
	
	@RequestMapping(value = "/inventory/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "상품 재고 수량 변경", notes = "특정 상품의 재고수량을 변경하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Inventory.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> inventoryUpdate (@Parameter(name = "상품 재고수량 변경", description = "", required = true) @RequestBody Inventory inventory) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		Inventory resInventory = new Inventory();
		try {
			if (inventorySvc.inventoryUpdate(inventory.getProductId(), inventory.getQuantity())) {
				resInventory = inventorySvc.inventorySearchByProductId(inventory.getProductId());
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<Inventory>(resInventory, header, HttpStatus.valueOf(200));
	}
	
}