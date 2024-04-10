package org.gauravagrwl.financeData.service;

import org.apache.catalina.User;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.model.documents.userAccount.AccountDocument;
import org.gauravagrwl.financeData.model.repository.AccountDocumentRepository;
import org.gauravagrwl.financeData.utility.FinanceDataHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAccountService {
    AccountDocumentRepository accountDocumentRepository;

    Logger LOGGER = LoggerFactory.getLogger(UserAccountService.class);

    UserProfileService profileService;

    public UserAccountService(AccountDocumentRepository accountDocumentRepository, UserProfileService profileService) {
        this.accountDocumentRepository = accountDocumentRepository;
        this.profileService = profileService;
    }

    public String addUserAccount(AccountDocument accountDocument, String userName) {
        String profileId = profileService.getProfileDocumentByUserName(userName).getId();
        accountDocument.setProfileDocumentId(profileId);

        accountDocument.setCollectionName(
                FinanceDataHelper.getStatementCollectionName(accountDocument.getAccountNumber())
        );
        AccountDocument save = accountDocumentRepository.save(accountDocument);

        return save.getId();
    }

    public AccountDocument getUserAccountDocument(String accountId, String userName) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            return accountDocumentRepository.findById(accountId).get();
        }
        throw new FinanceDataException("Account Id is null or not exist.");
    }

    public boolean isUserAccountExist(String accountId, String userName) {
        String profileId = profileService.getProfileDocumentByUserName(userName).getId();
        return accountDocumentRepository.existsByIdAndProfileDocumentId(accountId,
                profileId);
    }

    public List<AccountDocument> getUserAccounts(String userName) {
        String profileId = profileService.getProfileDocumentByUserName(userName).getId();
        Sort sort = Sort.by(Sort.Direction.DESC, "institutionCurrency");
        List<AccountDocument> byProfileDocumentId = accountDocumentRepository.findByProfileDocumentId(profileId, sort);
        return byProfileDocumentId;
    }

    public AccountDocument deleteUserAccountDocument(String accountId, String userName) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            accountDocumentRepository.deleteById(accountId);
        }
        throw new FinanceDataException("Account Id is null or not exist.");
    }

    public void toggleAccountActiveStatus(String userName, String accountId) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            AccountDocument document = accountDocumentRepository.findById(accountId).get();
            document.setIsActive(!document.getIsActive());
            accountDocumentRepository.save(document);
        }
        throw new FinanceDataException("Account Id is null or not exist.");
    }
}
