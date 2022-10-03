package com.digital.v2.controller;

import static com.digital.v2.utils.CookieUtils.getValueList;
import static com.digital.v2.utils.CookieUtils.deleteCookie;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.digital.v2.schema.Purchase;
import com.digital.v2.schema.PurchaseDetail;
import com.digital.v2.service.PurchaseService;
import com.digital.v2.utils.ExceptionUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "구매", description = "Purchase Related API")
@RequestMapping(value = "/rest/purchase")
public class PurchaseController {
	
	@Resource
	PurchaseService purchaseSvc;
	
	@RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "상품 구매", notes = "상품을 구매하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = List.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> purchase (@Parameter(name = "상품 구매", description = "", required = true) @RequestBody Purchase purchase,
			HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");

		List<Purchase> resPurchaseList = new ArrayList<Purchase>();
		try {
			purchase.setPersonId(Long.valueOf(token));	// 토큰에서 사용자 id를 가져와 set
			purchase.setPurchaseDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss")));	// 현재 날짜 구해서 set
			
			if (purchaseSvc.purchaseWrite(purchase)) {
				resPurchaseList = purchaseSvc.purchaseSearch("purchasedate", purchase.getPurchaseDate());
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<List<Purchase>>(resPurchaseList, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/shoppingCart", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "장바구니 상품 구매", notes = "장바구니의 상품들을 일괄로 구매하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = List.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> purchaseWithCart (@Parameter(name = "장바구니 상품 일괄 구매", description = "", required = true) @RequestBody Purchase purchase,
			HttpServletRequest request, HttpServletResponse response) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");

		List<Purchase> resPurchaseList = new ArrayList<Purchase>();
		try {
			List<String> cartItemStringList = getValueList("cart", request);
			
			if (cartItemStringList != null) {	
				purchase.setPersonId(Long.valueOf(token));	// 토큰에서 사용자 id를 가져와 set
				purchase.setPurchaseDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss")));	// 현재 날짜 구해서 set
				
				if (purchaseSvc.purchaseWithCartWrite(cartItemStringList, purchase)) {
					resPurchaseList = purchaseSvc.purchaseSearch("purchasedate", purchase.getPurchaseDate());
					// 장바구니 삭제
					deleteCookie("cart", response);
				}
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<List<Purchase>>(resPurchaseList, header, HttpStatus.valueOf(200));
	}

	// 날짜로 구매 상세 조회
	@RequestMapping(value = "/inquiry/{purchaseDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "구매 목록 조회", notes = "특정 날짜의 구매 목록을 검색하는 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = List.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)	// 에러를 담고 있는 schema를 따로 생성해서 사용
	})
	public ResponseEntity<?> productSearchByCategory (@PathVariable String purchaseDate) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		try {
			List<PurchaseDetail> purchaseList = purchaseSvc.purchaseDetailSearch("purchasedate", purchaseDate);
			return new ResponseEntity<List<PurchaseDetail>>(purchaseList, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}
}
