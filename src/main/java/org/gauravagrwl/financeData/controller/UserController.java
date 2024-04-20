package org.gauravagrwl.financeData.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.model.profile.UserProfileCollection;
import org.gauravagrwl.financeData.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userProfile")
@Slf4j
@Tag(name = "User Operations")
public class UserController {

    ProfileService profileService;

    /**
     * @param profileService
     */
    UserController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * @param userProfile
     * @return
     */
    @PostMapping(value = "/createUserProfile", consumes = "application/json", produces = "application/json")
    public ResponseEntity<String> createProfile(@RequestBody UserProfileCollection userProfile) {
        return ResponseEntity.ok(profileService.createProfile(userProfile));
    }

    /**
     * @param userName
     * @return
     */
    @GetMapping(value = "/getUserProfile", produces = "application/json")
    public ResponseEntity<?> getProfile(@RequestParam String userName) {
        return ResponseEntity.ok(profileService.getUserProfileDocument(userName));
    }

    // TODO: Only Admin

    /**
     * @param userName
     * @return
     */
    @DeleteMapping(value = "/deleteUserProfile")
    public ResponseEntity<?> deleteUserProfile(@RequestParam String userName) {
        profileService.deleteUserProfile(userName);
        return ResponseEntity.ok("User Profile with user name: " + userName + " is deleted.");
    }

    // TODO: Only Admin

    /**
     * @param param
     * @return
     */
    @GetMapping(value = "/getAllUserProfile")
    public ResponseEntity<?> getAllUserProfile() {
        return ResponseEntity.ok(profileService.getAllUserProfileDocument());
    }

}
