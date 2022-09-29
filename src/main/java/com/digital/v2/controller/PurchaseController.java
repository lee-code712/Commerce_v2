package com.digital.v2.controller;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digital.v2.service.PurchaseService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "구매", description = "Purchase Related API")
@RequestMapping(value = "/rest/purchase")
public class PurchaseController {
	
	@Resource
	PurchaseService purchaseSvc;
	
}
