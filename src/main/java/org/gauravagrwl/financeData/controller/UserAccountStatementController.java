package org.gauravagrwl.financeData.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/accountStatement")
public class UserAccountStatementController {

    @GetMapping(value = "/getStringOne")
    void getStringOne(){

    }
}
