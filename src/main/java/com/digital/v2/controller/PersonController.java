package com.digital.v2.controller;

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

import com.digital.v2.schema.Address;
import com.digital.v2.schema.ErrorMsg;
import com.digital.v2.schema.Person;
import com.digital.v2.schema.Phone;
import com.digital.v2.schema.SuccessMsg;
import com.digital.v2.service.PersonService;
import com.digital.v2.utils.ExceptionUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import static com.digital.v2.utils.CookieUtils.deleteCookie;

@RestController
@Tag(name = "고객", description = "Person Related API")
@RequestMapping(value = "/rest/person")
public class PersonController {
	
	@Resource
	private PersonService personSvc;
	
	@RequestMapping(value = "/signUp", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원가입", notes = "회원가입을 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Person.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> signUp (@Parameter(name = "회원가입 정보", required = true) @RequestBody Person person) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();

		Person resPerson = new Person();
		try {
			if (personSvc.signUp(person)) {
				resPerson = personSvc.personSearch("personname", person.getPersonName());
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
	public ResponseEntity<?> login (@Parameter(name = "계정 정보", required = true) @RequestBody Person person) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		SuccessMsg success = new SuccessMsg();
		
		try {
			Person resPerson = personSvc.login(person);
			if (resPerson == null) {
				return ExceptionUtils.setException(errors, 500, "로그인에 실패했습니다.", header);
			}
			success.setSuccessCode(200);
			success.setSuccessMsg("Access Token: " + resPerson.getPersonId());
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<SuccessMsg>(success, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "로그아웃", notes = "로그아웃을 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Person.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> logout (HttpServletResponse response) throws Exception {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		SuccessMsg success = new SuccessMsg();
		
		try {
			// cart cookie 삭제
			deleteCookie("cart", response);
			
			success.setSuccessCode(200);
			success.setSuccessMsg("로그아웃 되었습니다.");
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<SuccessMsg>(success, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/inquiry/{personName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원 본인 검색", notes = "회원명으로 본인 정보를 검색하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Person.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> personSearch (@Parameter(name = "회원명", required = true) @PathVariable String personName,
			HttpServletRequest request) throws Exception {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");
		
		try {
			Person person = personSvc.personSearch("personname", personName);	
			
			// 검색해 온 person 객체가 회원의 정보인지 확인
			if (person.getPersonId() != Long.valueOf(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 접근입니다.", header);
			}
			return new ResponseEntity<Person>(person, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}
	
	@RequestMapping(value = "/partyAddress/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원 주소 추가", notes = "회원의 주소 정보를 추가로 등록하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Person.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> partyAddressAdd (@Parameter(name = "주소 정보", required = true) @RequestBody Address address,
		HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");

		Person person = new Person();
		try {
			// 유효한 token(personId)인지 확인
			if (!personSvc.personIdCheck(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 접근입니다.", header);
			}
			
			if (personSvc.partyAddressWrite(Long.valueOf(token), address.getAddressId())) {
				person = personSvc.personSearch("personid", token);
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Person>(person, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/partyAddress/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원 주소 삭제", notes = "회원의 특정 주소 정보를 삭제하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Person.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> partyAddressDelete (@Parameter(name = "주소 정보", required = true) @RequestBody Address address,
		HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");

		Person person = new Person();
		try {
			// 유효한 token(personId)인지 확인
			if (!personSvc.personIdCheck(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 접근입니다.", header);
			}
			
			if (personSvc.partyAddressDelete(Long.valueOf(token), address.getAddressId())) {
				person = personSvc.personSearch("personid", token);
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Person>(person, header, HttpStatus.valueOf(200));
	}

	@RequestMapping(value = "/partyPhone/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원 전화번호 추가", notes = "회원의 전화번호 정보를 추가로 등록하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Person.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> partyPhoneAdd (@Parameter(name = "전화번호 정보", required = true) @RequestBody Phone phone,
		HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");

		Person person = new Person();
		try {
			// 유효한 token(personId)인지 확인
			if (!personSvc.personIdCheck(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 접근입니다.", header);
			}
			
			if (personSvc.partyPhoneWrite(Long.valueOf(token), phone.getPhoneId())) {
				person = personSvc.personSearch("personid", token);
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Person>(person, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/partyPhone/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원 전화번호 삭제", notes = "회원의 특정 전화번호 정보를 삭제하기 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Person.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> partyPhoneDelete (@Parameter(name = "전화번호 정보", required = true) @RequestBody Phone phone,
		HttpServletRequest request) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		String token = request.getHeader("Authorization");

		Person person = new Person();
		try {
			// 유효한 token(personId)인지 확인
			if (!personSvc.personIdCheck(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 접근입니다.", header);
			}
			
			if (personSvc.partyPhoneDelete(Long.valueOf(token), phone.getPhoneId())) {
				person = personSvc.personSearch("personid", token);
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Person>(person, header, HttpStatus.valueOf(200));
	}
	
}
