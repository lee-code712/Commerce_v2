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
	
	@RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "상품 구매", notes = "상품을 구매하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = List.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> purchase (@Parameter(name = "구매 정보", required = true) @RequestBody Purchase purchase,
			HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");

		List<Purchase> resPurchases = new ArrayList<Purchase>();
		try {
			// 유효한 token(personId)인지 확인
			if (!personSvc.personIdCheck(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 접근입니다.", header);
			}
			
			purchase.setPersonId(Long.valueOf(token));
			purchase.setPurchaseDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
			if (purchaseSvc.purchase(purchase)) {
				resPurchases = purchaseSvc.purchaseSearch(token, "purchasedate", purchase.getPurchaseDate());
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<List<Purchase>>(resPurchases, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/inCart", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "장바구니 상품 구매", notes = "장바구니의 상품들을 일괄로 구매하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = List.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> purchaseInCart (@Parameter(name = "구매 정보", required = true) @RequestBody Purchase purchase,
			HttpServletRequest request, HttpServletResponse response) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");

		List<Purchase> resPurchases = new ArrayList<Purchase>();
		try {
			// 유효한 token(personId)인지 확인
			if (!personSvc.personIdCheck(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 접근입니다.", header);
			}
			
			List<String> cartValueList = getValueList("cart", request);	// cart cookie 값 가져오기
			
			if (cartValueList != null) {		
				purchase.setPersonId(Long.valueOf(token));
				purchase.setPurchaseDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
				if (purchaseSvc.purchaseInCart(cartValueList, purchase)) {
					resPurchases = purchaseSvc.purchaseSearch(token, "purchasedate", purchase.getPurchaseDate());
					deleteCookie("cart", response);	// 구매 성공 시 cart cookie 삭제
				}
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<List<Purchase>>(resPurchases, header, HttpStatus.valueOf(200));
	}

	@RequestMapping(value = "/inquiry/{keyword}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "구매 검색", notes = "키워드를 포함하는 구매 날짜로 구매 목록을 검색하는 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = PurchaseList.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> purchaseSearch (@Parameter(name = "구매 날짜 키워드", required = true) @PathVariable String keyword, 
			HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");
		
		try {
			// 유효한 token(personId)인지 확인
			if (!personSvc.personIdCheck(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 접근입니다.", header);
			}
			
			PurchaseList purchases = purchaseSvc.purchaseDetailSearch(token, "purchasedate", keyword);
			return new ResponseEntity<PurchaseList>(purchases, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}
}
