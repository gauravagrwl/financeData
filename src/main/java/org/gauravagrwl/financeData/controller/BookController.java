package org.gauravagrwl.financeData.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookController {

    @PostMapping("/add")
    public void addTweet() {

    }

    @GetMapping("/hello")
    public String hello(){
        return "Hello World!";
    }
}
