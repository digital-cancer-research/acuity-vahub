package com.acuity.visualisations.rest.config;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.stereotype.Controller;

@Controller
public class AcuityErrorController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
