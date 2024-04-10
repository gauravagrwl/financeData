package org.gauravagrwl.financeData.controller;

import org.gauravagrwl.financeData.model.documents.userProfile.UserProfileDocument;
import org.gauravagrwl.financeData.service.UserProfileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping(path = "/userProfile")
public class UserProfileController {

    UserProfileService userProfileService;

    Logger LOGGER = LoggerFactory.getLogger(UserProfileController.class);


    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @PostMapping(value = "/createProfile", consumes = "application/json", produces = "application/json")
    ResponseEntity<String> createUserProfileData( @RequestBody UserProfileDocument profile){
        String profileId = userProfileService.createUserProfile(profile);
        return ResponseEntity.ok(profileId);

    }
    @GetMapping(value = "/getProfile", produces = "application/json")
    public ResponseEntity<UserProfileDocument> getProfileData(@RequestParam String userName){
        UserProfileDocument document = userProfileService.getProfileDocumentByUserName(userName);
        return ResponseEntity.ok(document);
    }

    @DeleteMapping(value = "/deleteProfile", produces = "application/json")
    public ResponseEntity<String> deleteProfileData(@RequestParam String userName){
        userProfileService.deleteUserProfile(userName);
        return ResponseEntity.ok("User is removed.");
    }
}
