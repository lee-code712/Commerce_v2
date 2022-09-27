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
import com.digital.v2.schema.Person;
import com.digital.v2.schema.SuccessMsg;
import com.digital.v2.service.PersonService;
import com.digital.v2.utils.ExceptionUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "고객", description = "Person Related API")
@RequestMapping(value = "/rest/person")
public class PersonController {
	
	@Resource
	private PersonService personSvc;
	
	@RequestMapping(value = "/signUp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원가입", notes = "회원가입 정보를 등록하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Person.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> signUp (@Parameter(name = "회원 정보 등록", description = "", required = true) @RequestBody Person person) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();

		Person resPerson = new Person();
		try {
			if (personSvc.signUp(person)) {
				resPerson = personSvc.personSearch(person.getLoginId());
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Person>(resPerson, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "로그인", notes = "로그인을 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = SuccessMsg.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> login (@Parameter(name = "로그인 검증", description = "", required = true) @RequestBody Person person) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		SuccessMsg success = new SuccessMsg();
		
		try {
			if (!personSvc.login(person)) {
				return ExceptionUtils.setException(errors, 500, "로그인에 실패했습니다.", header);
			}
			success.setSuccessCode(200);
			success.setSuccessMsg("로그인에 성공했습니다.");
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<SuccessMsg>(success, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/inquiry/{loginId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원 검색", notes = "회원의 ID로 회원정보를 검색하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Person.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> personSearch (@PathVariable String loginId) throws Exception {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		try {
			Person person = personSvc.personSearch(loginId);
			return new ResponseEntity<Person>(person, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}

}
