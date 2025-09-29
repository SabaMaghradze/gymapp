package com.gymapp.controller;

import com.gymapp.dto.request.auth.LoginRequest;
import com.gymapp.dto.request.auth.PasswordChangeRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Api(tags = "User Authentication")
public class UserController {

    @ApiOperation(
            value = "Change user password",
            notes = "Allows a user to change their password by providing old and new passwords."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "Password successfully changed"),
            @ApiResponse(code = 400, message = "Invalid credentials or bad request")
    })
    @PutMapping("/password-renewal")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest req,
                                            @RequestHeader String username) {
//        authenticationService.changePassword(username, req.oldPassword(), req.newPassword());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}












