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

import com.digital.v2.schema.Address;
import com.digital.v2.schema.ErrorMsg;
import com.digital.v2.schema.SuccessMsg;
import com.digital.v2.service.AddressService;
import com.digital.v2.utils.ExceptionUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "주소", description = "Address Related API")
@RequestMapping(value = "/rest/address")
public class AddressController {
	
	@Resource
	private AddressService addressSvc;
	
	@RequestMapping(value = "/write", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "주소 등록", notes = "주소를 등록하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Address.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> addressWrite (@Parameter(name = "주소 등록", description = "", required = true) @RequestBody Address address) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();

		Address resAddress = new Address();
		try {
			if (addressSvc.addressWrite(address)) {
				resAddress = addressSvc.addressSearch(address.getAddressDetail());
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Address>(resAddress, header, HttpStatus.valueOf(200));
	}
	
	// 사용자 주소 추가
	// 사용자 주소 삭제
	
	@RequestMapping(value = "/inquiry/{addressDetail}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "주소 검색", notes = "주소 상세로 주소 정보를 검색하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Address.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> addressSearch (@PathVariable String addressDetail) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		try {
			Address address = addressSvc.addressSearch(addressDetail);
			return new ResponseEntity<Address>(address, header, HttpStatus.valueOf(200));		
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}

}
