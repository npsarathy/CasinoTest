package org.casino.aggregator.controller;

import org.casino.aggregator.dto.AggregatorLaunchRequestDTO;
import org.casino.aggregator.dto.LoginRequestDTO;
import org.casino.aggregator.exception.UnknownGameIdException;
import org.casino.aggregator.service.GameLaunchService;
import org.casino.aggregator.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aggregator")
public class GameController {

    @Autowired
    UserService userService;
    @Autowired
    GameLaunchService gameLaunchService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        String token = userService.getLoginToken(loginRequestDTO.getUser(), loginRequestDTO.getPassword());

        if (token == null) {
            return new ResponseEntity<>("Login failed", HttpStatus.UNAUTHORIZED);
        }
        return ResponseEntity.ok(token);
    }

    @PostMapping("/launchLink")
    public ResponseEntity<String> launchLink(@RequestBody AggregatorLaunchRequestDTO aggregatorLaunchRequestDTO) {
        //Authenticate the key received
        if (!userService.isTokenValid(aggregatorLaunchRequestDTO.getToken())) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String launchUrl;
        try {
            launchUrl = gameLaunchService.getGameLaunchUrl(aggregatorLaunchRequestDTO);
        } catch (UnknownGameIdException | RuntimeException ex) {
            return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return launchUrl != null ? ResponseEntity.ok(launchUrl) : ResponseEntity.notFound().build();
    }
}
