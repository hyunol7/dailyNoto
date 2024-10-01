package com.daily.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class IndexController {


	@GetMapping("/main")
	public String main() {
		System.out.println("메인페이지");
		return "main";
	}
}
