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
import com.digital.v2.schema.ShoppingCart;
import com.digital.v2.schema.ShoppingCartItem;
import com.digital.v2.schema.SuccessMsg;
import com.digital.v2.service.ShoppingCartService;
import com.digital.v2.utils.ExceptionUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "장바구니", description = "ShoppingCart Related API")
@RequestMapping(value = "/rest/shoppingCart")
public class ShoppingCartController {
	
	@Resource
	ShoppingCartService shoppingCartSvc;

	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "장바구니 상품 추가", notes = "상품을 장바구니에 추가하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = SuccessMsg.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> cartItemAdd (@Parameter(name = "장바구니에 상품 추가", description = "", required = true) @RequestBody ShoppingCartItem cartItem,
			HttpServletRequest request, HttpServletResponse response) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		SuccessMsg success = new SuccessMsg();
		
		try {
			List<String> cartItemStringList = getValueList("cart", request);
			// 상품을 장바구니에 추가할 수 있는지 확인
			if (shoppingCartSvc.cartItemCheck(cartItemStringList, cartItem)) {
				String value = cartItem.getProductId() + "/" + cartItem.getPurchaseNumber();
				setCookieValue("cart", value, 1, request, response);
				
				success.setSuccessCode(200);
				success.setSuccessMsg("장바구니에 상품을 담았습니다.");
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<SuccessMsg>(success, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "장바구니 상품 삭제", notes = "상품을 장바구니에서 삭제하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = SuccessMsg.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> cartItemDelete (@Parameter(name = "장바구니에서 상품 삭제", description = "", required = true) @RequestBody ShoppingCartItem cartItem,
			HttpServletRequest request, HttpServletResponse response) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		SuccessMsg success = new SuccessMsg();
		
		try {
			List<String> cartItemStringList = getValueList("cart", request);
			// 상품이 장바구니에 있는지 확인
			if (!shoppingCartSvc.cartItemIsExist(cartItemStringList, cartItem)) {
				return ExceptionUtils.setException(errors, 500, "장바구니에 상품이 없습니다.", header);
			}
			
			String value = "" + cartItem.getProductId();
			deleteCookieValue("cart", value, request, response);
				
			success.setSuccessCode(200);
			success.setSuccessMsg("장바구니에서 상품을 삭제했습니다.");
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<SuccessMsg>(success, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/lookUp", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "장바구니 조회", notes = "장바구니에 담긴 상품 목록을 조회하는 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = ShoppingCart.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)	// 에러를 담고 있는 schema를 따로 생성해서 사용
	})
	public ResponseEntity<?> cartLookUp (HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		ShoppingCart cart = new ShoppingCart();
		try {
			List<String> cartItemStringList = getValueList("cart", request);
			
			if (cartItemStringList != null) {
				cart = shoppingCartSvc.setCart(cartItemStringList);		
			}	
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		return new ResponseEntity<ShoppingCart>(cart, header, HttpStatus.valueOf(200));
	}
}
