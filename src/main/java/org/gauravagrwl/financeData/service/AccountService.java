package org.gauravagrwl.financeData.service;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.gauravagrwl.financeData.model.repositories.AccountDocumentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AccountService {

    private AccountDocumentRepository accountDocumentRepository;
    private ProfileService profileService;

    public AccountService(AccountDocumentRepository accountDocumentRepository, ProfileService profileService) {
        this.accountDocumentRepository = accountDocumentRepository;
        this.profileService = profileService;
    }

    public String addUserAccount(AccountDocument accountDocument, String userName) {

        String profileId = profileService.getUserProfileDocument(userName).getId();
        accountDocument.setProfileDocumentId(profileId);
        String profileName = StringUtils.join(accountDocument.getInstitutionName(), "_", accountDocument.getAccountType().getAccountTypeName());
        accountDocument.setCsvProfile(profileName);
        String accountDisplayNumber = FinanceDataHelper.getAccountDisplayNumber(accountDocument.getAccountNumber());
        accountDocument.setAccountStatementCollectionName(
                FinanceDataHelper.getStatementCollectionName(accountDisplayNumber));
        AccountDocument save = accountDocumentRepository.save(accountDocument);

        return save.getId();
    }

    public AccountDocument getAccountDocument(String accountId, String userName) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            return accountDocumentRepository.findById(accountId).get();
        }
        throw new FinanceDataException("Account Id is null or not exist.");

    }

    public boolean isUserAccountExist(String accountId, String userName) {
        String profileId = profileService.getUserProfileDocument(userName).getId();
        return accountDocumentRepository.existsByIdAndProfileDocumentId(accountId,
                profileId);
    }

    public List<AccountDocument> getUserAccounts(String userName) {
        String profileId = profileService.getUserProfileDocument(userName).getId();
        Sort sort = Sort.by(Direction.DESC, "institutionCurrency");
        List<AccountDocument> byProfileDocumentId = accountDocumentRepository.findByProfileDocumentId(profileId, sort);
        return byProfileDocumentId;
    }

    public void toggleAccount(String userName, @NotNull String accountId) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            AccountDocument document = accountDocumentRepository.findById(accountId).get();
            document.setIsActive(!document.getIsActive());
            accountDocumentRepository.save(document);
        }
        throw new FinanceDataException("Account Id is null or not exist.");
    }

    public void deleteProfileAccount(String accountId, String userName) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            accountDocumentRepository.deleteById(accountId);
        } else {
            throw new FinanceDataException("No Account found with id: " + accountId + "for user: " + userName);
        }
    }

    public void setUpdateCalculateBalanceFlag(AccountDocument accountDocument) {
        accountDocumentRepository.findAndUpdateUpdateAccountStatementAndUpdateAccountReportById(accountDocument.getId(),
                accountDocument.getUpdateAccountStatement(), accountDocument.getUpdateAccountReport());

    }

    public void setUpdateBlanceCalculatedIndicator(AccountDocument accountDocument) {
        accountDocumentRepository.findAndUpdateUpdateAccountStatementAndUpdateAccountReportById(accountDocument.getId(),
                accountDocument.getUpdateAccountStatement(), accountDocument.getUpdateAccountReport());
    }


    public void setisBalanceCalculatedeById(AccountDocument accountDocument) {
        accountDocumentRepository.findAndUpdateBalanceCalculatedById(accountDocument.getId(), accountDocument.getBalanceCalculated());

    }

}
