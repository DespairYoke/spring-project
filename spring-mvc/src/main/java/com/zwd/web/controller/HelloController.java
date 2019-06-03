package com.zwd.web.controller;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.MyGetMapping;
import org.springframework.web.bind.annotation.MyRequestMapping;
import org.springframework.web.servlet.MyModelAndView;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-22
 **/
@Controller
public class HelloController {

    @MyGetMapping(value = "hello")
    public MyModelAndView index(String name, MyModelAndView modelAndView) {

        modelAndView.addObject("message",name);
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @MyRequestMapping(value = "getname")
    public String getName() {

        return "index";
    }
}
