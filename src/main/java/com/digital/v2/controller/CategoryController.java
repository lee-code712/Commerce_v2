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

import com.digital.v2.schema.Category;
import com.digital.v2.schema.ErrorMsg;
import com.digital.v2.service.CategoryService;
import com.digital.v2.utils.ExceptionUtils;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "카테고리", description = "Category Related API")
@RequestMapping(value = "/rest/category")
public class CategoryController {

	@Resource
	CategoryService categorySvc;

	@RequestMapping(value = "/write", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "카테고리 등록", notes = "카테고리 등록을 위한 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Category.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> categoryWrite (@Parameter(name = "카테고리 정보", required = true) @RequestBody Category category) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		Category resCategory = new Category();
		try {
			if (categorySvc.categoryWrite(category)) {
				resCategory = categorySvc.categorySearch("categoryname", category.getCategoryName());
			}
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}

		return new ResponseEntity<Category>(resCategory, header, HttpStatus.valueOf(200));
	}
	
	@RequestMapping(value = "/inquiry/{categoryName}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "카테고리 검색", notes = "카테고리명으로 카테고리 정보를 검색하는 API.")
	@ApiResponses({
		@ApiResponse(code = 200, message = "성공", response = Category.class),
		@ApiResponse(code = 500, message = "실패", response = ErrorMsg.class)
	})
	public ResponseEntity<?> categorySearch (@Parameter(name = "카테고리명", required = true) @PathVariable String categoryName) {
		MultiValueMap<String, String> header = new LinkedMultiValueMap<String, String>();
		ErrorMsg errors = new ErrorMsg();
		
		try {
			Category category = categorySvc.categorySearch("categoryname", categoryName);
			return new ResponseEntity<Category>(category, header, HttpStatus.valueOf(200));
		} catch (Exception e) {
			return ExceptionUtils.setException(errors, 500, e.getMessage(), header);
		}
	}
	
}
