package org.gauravagrwl.financeData.service;

import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.model.profile.UserProfileCollection;
import org.gauravagrwl.financeData.model.repositories.ProfileDocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProfileService {

    private ProfileDocumentRepository profileDocumentRepository;

    ProfileService(ProfileDocumentRepository profileDocumentRepository) {
        this.profileDocumentRepository = profileDocumentRepository;
    }

    public String createProfile(UserProfileCollection userProfile) {
        String userProfileId = profileDocumentRepository.insert(userProfile).getId();
        log.info("Document Created Successfully ID :" + userProfileId);
        return userProfileId;
    }

    public UserProfileCollection getUserProfileDocument(String userName) {
        List<UserProfileCollection> byUserName = profileDocumentRepository.findByUserName(userName);
        if (byUserName.size() != 1) {
            throw new FinanceDataException("No user found!");
        }
        return byUserName.get(0);
    }

    public void deleteUserProfile(String userName) {
        String userProfileID = getUserProfileDocument(userName).getId();
        profileDocumentRepository.deleteById(userProfileID);
    }

    public List<UserProfileCollection> getAllUserProfileDocument() {
        List<UserProfileCollection> allUserProfile = profileDocumentRepository.findAll();
        if (allUserProfile.size() != 1) {
            throw new FinanceDataException("No user found!");
        }
        return allUserProfile;
    }

}
