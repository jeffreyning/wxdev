package com.nh.wx.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.nh.micro.controller.MicroControllerMap;

@RestController
public class ShowController {
    @RequestMapping(value = "/showmap")
    @ResponseBody
    public void getCompanyInfo(HttpServletRequest request) {
    	System.out.println(MicroControllerMap.urlMap.toString());
    }
}
