package org.gauravagrwl.financeData.service;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.model.profileAccount.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.repositories.AccountDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AccountService {

    private AccountDocumentRepository accountDocumentRepository;

    @Autowired
    MongoTemplate template;
    private ProfileService profileService;

    public AccountService(AccountDocumentRepository accountDocumentRepository, ProfileService profileService) {
        this.accountDocumentRepository = accountDocumentRepository;
        this.profileService = profileService;
    }

    public String addUserAccount(AccountCollection accountCollection, String userName) {

        String profileId = profileService.getUserProfileDocument(userName).getId();
        accountCollection.setProfileDocumentId(profileId);
        String profileName = StringUtils.join(accountCollection.getInstitutionName(), "_", accountCollection.getAccountType().getAccountTypeName());
        accountCollection.setCsvProfile(profileName);
        String accountDisplayNumber = FinanceDataHelper.getAccountDisplayNumber(accountCollection.getAccountNumber());
        accountCollection.setAccountStatementCollectionName(
                FinanceDataHelper.getStatementCollectionName(accountDisplayNumber));
        accountCollection.setAccountReportCollectionName(
                FinanceDataHelper.getReportCollectionName(accountDisplayNumber));
        AccountCollection save = accountDocumentRepository.save(accountCollection);

        return save.getId();
    }

    public AccountCollection getAccountDocument(String accountId, String userName) {
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

    public List<AccountCollection> getUserAccounts(String userName) {
        String profileId = profileService.getUserProfileDocument(userName).getId();
        Sort sort = Sort.by(Direction.DESC, "institutionCurrency").and(Sort.by(Direction.ASC, "institutionName"));
        List<AccountCollection> byProfileDocumentId = accountDocumentRepository.findByProfileDocumentId(profileId, sort);
        return byProfileDocumentId;
    }

    public void toggleAccount(String userName, @NotNull String accountId) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            AccountCollection document = accountDocumentRepository.findById(accountId).get();
            document.setIsActive(!document.getIsActive());
            accountDocumentRepository.save(document);
        }
        throw new FinanceDataException("Account Id is null or not exist.");
    }

    public void deleteProfileAccount(String accountId, String userName) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            AccountCollection accountCollection = accountDocumentRepository.findById(accountId).get();
            String accountReportCollectionName = accountCollection.getAccountReportCollectionName();
            String accountStatementCollectionName = accountCollection.getAccountStatementCollectionName();
            template.remove(new Query(), accountReportCollectionName);
            template.remove(new Query(), accountStatementCollectionName);
            accountCollection.resetFields();
            accountDocumentRepository.save(accountCollection);

//            accountDocumentRepository.deleteById(accountId);
        } else {
            throw new FinanceDataException("No Account found with id: " + accountId + "for user: " + userName);
        }
    }

    public void setUpdateCalculateBalanceFlag(AccountCollection accountCollection) {
        accountDocumentRepository.findAndUpdateNeededFlagById(accountCollection.getId(), accountCollection.getUpdateAccountStatementNeeded(),
                accountCollection.getUpdateAccountReportNeeded(), accountCollection.getUpdateCashFlowReportNeeded());

    }

    public void setisBalanceCalculatedeById(AccountCollection accountCollection) {
        accountDocumentRepository.findAndUpdateBalanceCalculatedById(accountCollection.getId(), accountCollection.getBalanceCalculated());

    }

}
