package com.utilsrv.preader.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PReaderErrorController implements ErrorController {
    @RequestMapping("/error")
    public void error(HttpServletRequest request, HttpServletResponse response) {
        Enumeration<String> attrs =  request.getAttributeNames();
        while(attrs.hasMoreElements()) {
            String attrName = attrs.nextElement();
            Object attr = request.getAttribute(attrName);
            System.out.println(String.format("%s, %s", attrName, attr.getClass().getName()));
            if (attr instanceof String) {
                System.out.println("attr value:" + attr);
            }
        }
        throw new RuntimeException("error");
    }

    @ExceptionHandler({Exception.class})
    public Map<String, String> handleException(Exception ex) {
        HashMap<String, String> map = new HashMap<>();
        map.put("errorMsg", ex.getMessage());
        return map;
    }
}
