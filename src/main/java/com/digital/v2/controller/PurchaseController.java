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
import com.digital.v2.schema.PurchaseList;
import com.digital.v2.service.PersonService;
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
	@Resource
	PersonService personSvc;
	
	/**
	 * @description 상품 구매
	 * @params purchase: 상품 구매 정보 (productId, purchaseNumber, addressId, phoneId)
	 */
	@RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "상품 구매", notes = "상품을 구매하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = List.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> purchase (@Parameter(name = "상품 구매 정보", required = true) @RequestBody Purchase purchase,
			HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");

		List<Purchase> resPurchases = new ArrayList<Purchase>();
		try {
			if (!personSvc.isValidPerson(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 token 사용으로 접근할 수 없습니다.", header);
			}
			
			purchase.setPersonId(Long.valueOf(token));	// 토큰에서 사용자 id를 가져와 set
			purchase.setPurchaseDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HHmmss")));	// 현재 날짜 구해서 set
			
			if (purchaseSvc.purchase(purchase)) {
				resPurchases = purchaseSvc.purchaseSearch(token, "purchasedate", purchase.getPurchaseDate());
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<List<Purchase>>(resPurchases, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/shoppingCart", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "장바구니 상품 구매", notes = "장바구니의 상품들을 일괄로 구매하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = List.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> purchaseWithCart (@Parameter(name = "장바구니 상품 일괄 구매", required = true) @RequestBody Purchase purchase,
			HttpServletRequest request, HttpServletResponse response) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");

		List<Purchase> resPurchases = new ArrayList<Purchase>();
		try {
			if (!personSvc.isValidPerson(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 token 사용으로 접근할 수 없습니다.", header);
			}
			
			List<String> cartValueList = getValueList("cart", request);
			
			if (cartValueList != null) {	
				purchase.setPersonId(Long.valueOf(token));	// 토큰에서 사용자 id를 가져와 set
				purchase.setPurchaseDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")));	// 현재 날짜 구해서 set
				
				if (purchaseSvc.purchaseInCart(cartValueList, purchase)) {
					resPurchases = purchaseSvc.purchaseSearch(token, "purchasedate", purchase.getPurchaseDate());
					// 장바구니 삭제
					deleteCookie("cart", response);
				}
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<List<Purchase>>(resPurchases, header, HttpStatus.valueOf(200));
	}

	@RequestMapping(value = "/inquiry/{purchaseDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "구매 목록 조회", notes = "특정 날짜의 구매 목록을 검색하는 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = PurchaseList.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> productSearchByCategory (@Parameter(name = "상품 구매 정보", required = false) @PathVariable String purchaseDate, 
			HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");
		
		try {
			if (!personSvc.isValidPerson(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 token 사용으로 접근할 수 없습니다.", header);
			}
			
			PurchaseList purchases = purchaseSvc.purchaseDetailSearch(token, "purchasedate", purchaseDate);
			return new ResponseEntity<PurchaseList>(purchases, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}
}
