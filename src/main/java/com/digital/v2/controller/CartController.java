package com.digital.v2.controller;

import static com.digital.v2.utils.CookieUtils.getValueList;
import static com.digital.v2.utils.CookieUtils.setCookieValue;
import static com.digital.v2.utils.CookieUtils.deleteCookieValue;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.digital.v2.schema.ErrorMsg;
import com.digital.v2.schema.Cart;
import com.digital.v2.schema.CartProduct;
import com.digital.v2.schema.SuccessMsg;
import com.digital.v2.service.CartService;
import com.digital.v2.utils.ExceptionUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "장바구니", description = "Cart Related API")
@RequestMapping(value = "/rest/cart")
public class CartController {
	
	@Resource
	CartService cartSvc;

	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "장바구니 상품 추가", notes = "특정 상품을 장바구니에 추가하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = SuccessMsg.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> cartProductAdd (@Parameter(name = "상품 정보", required = true) @RequestBody CartProduct cartProduct,
			HttpServletRequest request, HttpServletResponse response) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		SuccessMsg success = new SuccessMsg();
		
		try {
			List<String> cartValueList = getValueList("cart", request);	// cart cookie 값 가져오기
			
			// 상품 정보 유효성 검사
			if (cartSvc.cartProductCheck(cartValueList, cartProduct)) {
				String value = cartProduct.getProductId() + "/" + cartProduct.getPurchaseNumber();
				setCookieValue("cart", value, 1, request, response);	// cart cookie에 값 추가
				
				success.setSuccessCode(200);
				success.setSuccessMsg("장바구니에 상품을 담았습니다.");
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<SuccessMsg>(success, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "장바구니 상품 삭제", notes = "특정 상품을 장바구니에서 삭제하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = SuccessMsg.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> cartProductDelete (@Parameter(name = "상품 정보", required = true) @RequestBody CartProduct cartItem,
			HttpServletRequest request, HttpServletResponse response) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		SuccessMsg success = new SuccessMsg();
		
		try {
			List<String> cartValueList = getValueList("cart", request);	// cart cookie 값 가져오기
			
			// 상품 정보 존재 여부 확인
			if (cartSvc.isExistCartProduct(cartValueList, cartItem)) {
				String value = "" + cartItem.getProductId();
				deleteCookieValue("cart", value, request, response);	// cart cookie에서 값 제거
					
				success.setSuccessCode(200);
				success.setSuccessMsg("장바구니에서 상품을 삭제했습니다.");
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<SuccessMsg>(success, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/lookUp", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "장바구니 조회", notes = "장바구니에 담긴 상품 목록을 조회하는 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Cart.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> cartLookUp (HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		try {
			Cart cart = new Cart();
			List<String> cartValueList = getValueList("cart", request);	// cart cookie 값 가져오기
			
			if (cartValueList != null) {
				cart = cartSvc.setCart(cartValueList);	// cart 객체 set
			}
			return new ResponseEntity<Cart>(cart, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}	
	}
}
