package org.gauravagrwl.financeData.service;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.model.accountCollection.AccountCollection;
import org.gauravagrwl.financeData.model.repositories.AccountRepository;
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

    private AccountRepository accountRepository;

    @Autowired
    MongoTemplate template;
    private ProfileService profileService;

    public AccountService(AccountRepository accountRepository, ProfileService profileService) {
        this.accountRepository = accountRepository;
        this.profileService = profileService;
    }

    public String addUserAccount(AccountCollection accountCollection, String userName) {

        String profileId = profileService.getUserProfileDocument(userName).getId();
        accountCollection.setProfileDocumentId(profileId);

        String profileName = StringUtils.join(accountCollection.getInstitutionName(), "_", accountCollection.getAccountType().getAccountTypeName());
        accountCollection.setProfileType(profileName);

        String accountDispalyName = FinanceDataHelper.getAccountDisplayName(userName, accountCollection.getProfileType(), accountCollection.getAccountNumber());

        accountCollection.setAccountDisplayName(accountDispalyName);

        accountCollection.setAccountStatementCollectionName(
                FinanceDataHelper.getStatementCollectionName(accountDispalyName));
        accountCollection.setAccountTransactionCollectionName(
                FinanceDataHelper.getAccountTransactionCollectionName(accountDispalyName));
        switch (accountCollection.getInstitutionCategory()) {
            case INVESTMENT -> accountCollection.setAccountReportCollectionName(
                    FinanceDataHelper.getHoldingCollectionName(accountDispalyName));
            case BANKING -> accountCollection.setAccountReportCollectionName(
                    FinanceDataHelper.getCashFlowCollectionName(accountDispalyName));
            case ASSETS -> accountCollection.setAccountReportCollectionName(
                    FinanceDataHelper.getAssetsCollectionName(accountDispalyName));
            case LOAN -> accountCollection.setAccountReportCollectionName(
                    FinanceDataHelper.getReportCollectionName(accountDispalyName));
            default -> throw new FinanceDataException("getInstitutionCategory not defined for the account.");
        }

        AccountCollection save = accountRepository.save(accountCollection);

        return save.getId();
    }

    public AccountCollection getAccountDetails(String accountId, String userName) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            return accountRepository.findById(accountId).get();
        }
        throw new FinanceDataException("Account Id is null or not exist.");

    }

    public boolean isUserAccountExist(String accountId, String userName) {
        String profileId = profileService.getUserProfileDocument(userName).getId();
        return accountRepository.existsByIdAndProfileDocumentId(accountId,
                profileId);
    }

    public List<AccountCollection> getUserAccounts(String userName) {
        String profileId = profileService.getUserProfileDocument(userName).getId();
        Sort sort = Sort.by(Direction.DESC, "institutionCurrency").and(Sort.by(Direction.ASC, "institutionName"));
        List<AccountCollection> byProfileDocumentId = accountRepository.findByProfileDocumentId(profileId, sort);
        return byProfileDocumentId;
    }

    public void toggleAccount(String userName, @NotNull String accountId) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            AccountCollection document = accountRepository.findById(accountId).get();
            document.setIsActive(!document.getIsActive());
            accountRepository.save(document);
        }
        throw new FinanceDataException("Account Id is null or not exist.");
    }

    public void deleteProfileAccount(String accountId, String userName) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            AccountCollection accountCollection = accountRepository.findById(accountId).get();
            String accountReportCollectionName = accountCollection.getAccountReportCollectionName();
            String accountStatementCollectionName = accountCollection.getAccountStatementCollectionName();
            template.remove(new Query(), accountReportCollectionName);
            template.remove(new Query(), accountStatementCollectionName);
            accountCollection.resetFields();
            accountRepository.save(accountCollection);

//            accountDocumentRepository.deleteById(accountId);
        } else {
            throw new FinanceDataException("No Account found with id: " + accountId + "for user: " + userName);
        }
    }

    public void setUpdateCalculateBalanceFlag(AccountCollection accountCollection) {
        accountRepository.findAndUpdateNeededFlagById(accountCollection.getId(), accountCollection.getUpdateAccountStatementModelNeeded(),
                accountCollection.getUpdateAccountReportNeeded(), accountCollection.getUpdateCashFlowReportNeeded());

    }

    public void setisBalanceCalculatedeById(AccountCollection accountCollection) {
        accountRepository.findAndUpdateBalanceCalculatedById(accountCollection.getId(), accountCollection.getBalanceCalculated());

    }

}
