package com.adm.lucas.posts.adapter.inbound.controllers;

import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@CrossOrigin
@Hidden
public class BasePageController {
    @RequestMapping("/")
    public RedirectView redirect() {
        return new RedirectView("/swagger-ui.html");
    }
}