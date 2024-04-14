package org.gauravagrwl.financeData.service;

import java.util.List;

import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.model.profile.UserProfileDocument;
import org.gauravagrwl.financeData.model.repositories.ProfileDocumentRepository;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProfileService {

    private ProfileDocumentRepository profileDocumentRepository;

    ProfileService(ProfileDocumentRepository profileDocumentRepository) {
        this.profileDocumentRepository = profileDocumentRepository;
    }

    public String createProfile(UserProfileDocument userProfile) {
        String userProfileId = profileDocumentRepository.insert(userProfile).getId();
        log.info("Document Created Successfully ID :" + userProfileId);
        return userProfileId;
    }

    public UserProfileDocument getUserProfileDocument(String userName) {
        List<UserProfileDocument> byUserName = profileDocumentRepository.findByUserName(userName);
        if (byUserName.size() != 1) {
            throw new FinanceDataException("No user found!");
        }
        return byUserName.get(0);
    }

    public void deleteUserProfile(String userName) {
        String userProfileID = getUserProfileDocument(userName).getId();
        profileDocumentRepository.deleteById(userProfileID);
    }

    public List<UserProfileDocument> getAllUserProfileDocument() {
        List<UserProfileDocument> allUserProfile = profileDocumentRepository.findAll();
        if (allUserProfile.size() != 1) {
            throw new FinanceDataException("No user found!");
        }
        return allUserProfile;
    }

}
