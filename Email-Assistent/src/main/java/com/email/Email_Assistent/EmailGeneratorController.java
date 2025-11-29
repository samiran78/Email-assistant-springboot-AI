package com.email.Email_Assistent;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
public class EmailGeneratorController {

//    @Autowired
//    private   EmailGeneratorService emailGeneratorService;
    //we need the object of the service
    private final  EmailGeneratorService emailGeneratorService;
    @PostMapping("/generate")
  public ResponseEntity<String> generateEmail(@RequestBody EmailRequest emailRequest){
       String response =  emailGeneratorService.generateEmailReply(emailRequest);
      return  ResponseEntity.ok(response);
  }
}
