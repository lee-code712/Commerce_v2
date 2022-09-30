package com.digital.v2.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class PurchaseService {
	
	@Resource
	ProductService productSvc;

}
