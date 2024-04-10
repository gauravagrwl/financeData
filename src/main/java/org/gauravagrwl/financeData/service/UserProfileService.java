package org.gauravagrwl.financeData.service;

import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.model.documents.userProfile.UserProfileDocument;
import org.gauravagrwl.financeData.model.repository.ProfileDocumentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileService {
    ProfileDocumentRepository profileDocumentRepository;

    Logger LOGGER = LoggerFactory.getLogger(UserProfileService.class);


    public UserProfileService(ProfileDocumentRepository profileDocumentRepository) {
        this.profileDocumentRepository = profileDocumentRepository;
    }

    public String createUserProfile(UserProfileDocument profileDocument) {
        if (profileDocumentRepository.existsByUserName(profileDocument.getUserName()))
            throw new FinanceDataException("User already exist!");
        UserProfileDocument insert = profileDocumentRepository.insert(profileDocument);
        return ("Document Inserted with id: " + insert.getId());
    }

    public void deleteUserProfile(String userName) {

    }

    public UserProfileDocument getProfileDocumentByUserName(String userName) {
        List<UserProfileDocument> userDocuments = profileDocumentRepository.findByUserName(userName);
        if (userDocuments.size() != 1) {
            throw new FinanceDataException("No user found!");
        }
        return userDocuments.get(0);
    }

}
