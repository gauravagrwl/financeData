package org.gauravagrwl.financeData.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/accountReport")
public class UserAccountReportController {
    @GetMapping(value = "/getString")
    void getString(){

    }


}


