package com.pablogb.psychologger.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class HomeViewController {


    @GetMapping("/")
    public String startPage() {
        return "start";
    }
}
