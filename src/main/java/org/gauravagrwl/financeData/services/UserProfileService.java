package org.gauravagrwl.financeData.services;

import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.helper.FinanceAppQuery;
import org.gauravagrwl.financeData.model.user.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserProfileService {

    @Autowired
    MongoTemplate template;

    /**
     * Create a userProfile and returns Profile ID.
     *
     * @param userProfile
     * @return
     */
    public String createUserProfile(UserProfile userProfile) {
        String profileId = template.insert(userProfile).getId();
        log.info("User Profile Created Successfully with ID :" + profileId);
        return profileId;
    }

    public UserProfile getUserProfile(String username) {
        UserProfile userProfile = template.findOne(FinanceAppQuery.findByUsernameQuery(username), UserProfile.class);
        log.info("User Profile found for the user:" + username);
        return userProfile;
    }

}
