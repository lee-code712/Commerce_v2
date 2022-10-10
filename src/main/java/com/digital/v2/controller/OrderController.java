package com.digital.v2.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

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
import com.digital.v2.schema.OrderSheet;
import com.digital.v2.schema.OrderSheetDetail;
import com.digital.v2.schema.Purchase;
import com.digital.v2.schema.PurchaseDetail;
import com.digital.v2.schema.PurchaseList;
import com.digital.v2.schema.SuccessMsg;
import com.digital.v2.service.AuthService;
import com.digital.v2.service.OrderService;
import com.digital.v2.utils.ExceptionUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "주문", description = "Order Related API")
@RequestMapping(value = "/rest/order")
public class OrderController {
	
	@Resource
	OrderService orderSvc;
	@Resource
	AuthService authSvc;
	
	@RequestMapping(value = "/sheet/write", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "가주문서 등록", notes = "결제 전 가주문서 등록을 위한 API. *입력 필드: product(s), addressId, phoneId")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = OrderSheetDetail.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> orderSheetWrite (@Parameter(name = "주문 정보", required = true) @RequestBody OrderSheet orderSheet,
			HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();

		OrderSheetDetail resOrderSheet = new OrderSheetDetail();
		try {
			String token = request.getHeader("Authorization");
			long personId = authSvc.getPersonId(token);
			
			orderSheet.setPersonId(personId);
			long orderId = orderSvc.orderSheetWrite(orderSheet);
			if (orderId != 0) {
				resOrderSheet = orderSvc.orderSheetDetailSearch("ordersheetid", "" + orderId);
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<OrderSheetDetail>(resOrderSheet, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "sheet/lookUp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "가주문서 조회", notes = "등록한 가주문서를 확인하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = OrderSheetDetail.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> orderSheetLookUp (HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();

		try {
			String token = request.getHeader("Authorization");
			long personId = authSvc.getPersonId(token);
			
			OrderSheetDetail orderSheet = orderSvc.orderSheetDetailSearch("ordersheetpersonid", "" + personId);
			if (orderSheet.getPersonId() == 0) {
				return ExceptionUtils.setException(errors, 500, "현재 등록된 가주문서가 없습니다.", header);
			}
			return new ResponseEntity<OrderSheetDetail>(orderSheet, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}
	
	@RequestMapping(value = "sheet/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "가주문서 삭제", notes = "가주문서를 삭제하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = SuccessMsg.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> orderSheetDelete (HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		SuccessMsg success = new SuccessMsg();

		try {
			String token = request.getHeader("Authorization");
			long personId = authSvc.getPersonId(token);
			
			OrderSheet orderSheet = orderSvc.orderSheetSearch("ordersheetpersonid", "" + personId);
			if (orderSheet.getPersonId() == 0) {
				return ExceptionUtils.setException(errors, 500, "현재 등록된 가주문서가 없습니다.", header);
			}
			
			if (orderSvc.orderSheetDelete(orderSheet.getOrderSheetId())) {
				success.setSuccessCode(200);
				success.setSuccessMsg("가주문서 삭제가 완료되었습니다.");
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		return new ResponseEntity<SuccessMsg>(success, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/purchase", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "결제", notes = "가주문서에 있는 상품 목록을 구매하기 위한 API. *입력 필드: orderSheetId")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = PurchaseDetail.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> purchase (@Parameter(name = "가주문서 정보", required = true) @RequestBody Purchase purchase,
			HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		PurchaseDetail resPurchase = new PurchaseDetail();
		try {
			String token = request.getHeader("Authorization");
			long personId = authSvc.getPersonId(token);
			
			purchase.setPurchaseDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
			if (orderSvc.purchase(personId, purchase)) {
				resPurchase = orderSvc.purchaseDetailSearch("purchaseid", "" + purchase.getOrderSheetId());
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<PurchaseDetail>(resPurchase, header, HttpStatus.valueOf(200));
	}

	@RequestMapping(value = "/inquiry/{keyword}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "구매내역 검색", notes = "키워드를 포함하는 구매 날짜로 구매 목록을 검색하는 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = PurchaseList.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> purchaseSearch (@Parameter(name = "구매 날짜 키워드", required = true) @PathVariable String keyword, 
			HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		try {
			String token = request.getHeader("Authorization");
			long personId = authSvc.getPersonId(token);
			
			PurchaseList purchases = orderSvc.purchaseListSearch(personId, "purchasedate", keyword);
			return new ResponseEntity<PurchaseList>(purchases, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}
}
