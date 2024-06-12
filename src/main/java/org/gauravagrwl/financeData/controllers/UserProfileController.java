package org.gauravagrwl.financeData.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.user.UserProfile;
import org.gauravagrwl.financeData.services.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userProfile")
@Slf4j
@Tag(name = "User Operations")
public class UserProfileController {

    //TODO: 1. POST Create user profile
    //TODO: 2. GET user profile
    //TODO: 3. PUT modify user profile
    //TODO: 4. DELETE DELETE User profile

    @Autowired
    UserProfileService userProfileService;

    @PostMapping(value = "/createUserProfile", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createProfile(@RequestBody UserProfile userProfile) {
        return ResponseEntity.ok(userProfileService.createUserProfile(userProfile));
    }

    @GetMapping(value = "/getUserProfile", produces = "application/json")
    public ResponseEntity<?> getProfile(@RequestParam String userName) {
        return ResponseEntity.ok(userProfileService.getUserProfile(userName));
    }


}
