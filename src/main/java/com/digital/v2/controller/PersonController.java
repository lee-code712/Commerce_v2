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
	
	/**
	 * @description 회원가입
	 * @params person: 회원가입 정보 (personName, password, gender, phoneNumber(s), addressDetail(s))
	 */
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
	
	/**
	 * @description 로그인
	 * @params person: 회원 계정 정보 (personName, password)
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "로그인", notes = "로그인을 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = SuccessMsg.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> login (@Parameter(name = "회원 계정 정보", required = true) @RequestBody Person person) {
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
	
	/**
	 * @description 로그아웃
	 * @params response: 삭제하는 쿠키정보를 저장할 response 객체
	 */
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
			deleteCookie("cart", response);	// 장바구니 관련 cookie 삭제
			
			success.setSuccessCode(200);
			success.setSuccessMsg("로그아웃 되었습니다.");
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<SuccessMsg>(success, header, HttpStatus.valueOf(200));
	}
	
	/**
	 * @description 회원 본인 검색
	 * @params personName: 검색 키워드, request: personId token 값을 가져오기 위한 request 객체
	 */
	@RequestMapping(value = "/inquiry/{personName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원 본인 검색", notes = "회원명으로 회원 본인 정보를 검색하기 위한 API.")
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
			if (person.getPersonName() != null && person.getPersonId() != Long.valueOf(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 token 사용으로 접근할 수 없습니다.", header);
			}
			return new ResponseEntity<Person>(person, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}
	
	/**
	 * @description 회원 주소 정보 추가
	 * @params address: 주소 정보 (addressId), request: personId token 값을 가져오기 위한 request 객체
	 */
	@RequestMapping(value = "/address/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원 주소 정보 추가", notes = "회원의 주소 정보를 추가하기 위한 API.")
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
			if (!personSvc.isValidPerson(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 token 사용으로 접근할 수 없습니다.", header);
			}
			
			if (personSvc.partyAddressWrite(Long.valueOf(token), address.getAddressId())) {
				person = personSvc.personSearch("personid", token);
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Person>(person, header, HttpStatus.valueOf(200));
	}
	
	/**
	 * @description 회원 주소정보 삭제
	 * @params address: 주소정보 (addressId), request: personId token 값을 가져오기 위한 request 객체
	 */
	@RequestMapping(value = "/address/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원 주소 정보 삭제", notes = "회원의 주소 정보를 삭제하기 위한 API.")
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
			if (!personSvc.isValidPerson(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 token 사용으로 접근할 수 없습니다.", header);
			}
			
			if (personSvc.partyAddressDelete(Long.valueOf(token), address.getAddressId())) {
				person = personSvc.personSearch("personid", token);
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Person>(person, header, HttpStatus.valueOf(200));
	}

	/**
	 * @description 회원 전화번호 정보 추가
	 * @params phone: 전화번호 정보 (phoneId), request: personId token 값을 가져오기 위한 request 객체
	 */
	@RequestMapping(value = "/phone/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원 전화번호 정보 추가", notes = "회원의 전화번호 정보를 추가하기 위한 API.")
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
			if (!personSvc.isValidPerson(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 token 사용으로 접근할 수 없습니다.", header);
			}
			
			if (personSvc.partyPhoneWrite(Long.valueOf(token), phone.getPhoneId())) {
				person = personSvc.personSearch("personid", token);
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
		
		return new ResponseEntity<Person>(person, header, HttpStatus.valueOf(200));
	}
	
	/**
	 * @description 회원 전화번호 정보 삭제
	 * @params phone: 전화번호 정보 (phoneId), request: personId token 값을 가져오기 위한 request 객체
	 */
	@RequestMapping(value = "/phone/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "회원 전화번호 정보 삭제", notes = "회원의 전화번호 정보를 삭제하기 위한 API.")
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
			if (!personSvc.isValidPerson(token)) {
				return ExceptionUtils.setException(errors, 401, "유효하지 않은 token 사용으로 접근할 수 없습니다.", header);
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
