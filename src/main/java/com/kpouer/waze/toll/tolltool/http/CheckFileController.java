/*
 * Copyright 2021 Matthieu Casanova
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *  TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.kpouer.waze.toll.tolltool.http;

import com.kpouer.waze.toll.tolltool.pricecatalog.Category;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
public class CheckFileController {
    private static final String LOGIN_URL = "http://127.0.0.1:8080/login";
    private static final String LOGIN_URL2 = "https://tolls.waze.com/login";

    private final RestTemplate restClient;

    public CheckFileController() {
        restClient = new RestTemplate();
    }

    @PostMapping("checkfile")
    public String checkFile(@RequestBody CheckFileRequest checkFileRequest) {
        MultiValueMap<String, String> map           = new LinkedMultiValueMap<String, String>();
        map.add("username", checkFileRequest.getCredencials().getUsername());
        map.add("password", checkFileRequest.getCredencials().getPassword());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>>       requestEntity = new HttpEntity<>(map, headers);
        try {
            ResponseEntity<String>  result        = restClient.postForEntity(LOGIN_URL, requestEntity, String.class);
        } catch (RestClientException e) {
            e.printStackTrace();
            System.out.println();
        }
        return ";";
    }

    @PostMapping(value = "login" ,consumes = "application/x-www-form-urlencoded; charset=UTF-8")
    public String login(@RequestBody String credencials) {

        return ";";
    }
}
