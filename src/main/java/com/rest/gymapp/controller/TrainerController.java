package com.rest.gymapp.controller;


import com.rest.gymapp.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class TrainerController {

    private final TrainerService trainerService;


}
