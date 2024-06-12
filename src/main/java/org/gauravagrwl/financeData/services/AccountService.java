package org.gauravagrwl.financeData.services;

import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.exceptions.FinanceAppException;
import org.gauravagrwl.financeData.helper.FinanceAppHelper;
import org.gauravagrwl.financeData.helper.FinanceAppQuery;
import org.gauravagrwl.financeData.model.user.UserProfile;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AccountService {
    @Autowired
    MongoTemplate template;

    public String addUserAccount(String username, UserAccount userAccount) {
        String userProfileId = template.findOne(FinanceAppQuery.findByUsernameQuery(username), UserProfile.class).getId();
        userAccount.setUserProfileId(userProfileId);

        userAccount.setProfileType(FinanceAppHelper.getAccountProfileName(userAccount));
        userAccount.setAccountDisplayName(FinanceAppHelper.getAccountDisplayName(userAccount));

        userAccount.setAccountTransactionCollectionName(
                FinanceAppHelper.getAccountTransactionCollectionName(userAccount));

        userAccount.setAccountDisplayNumber(FinanceAppHelper.getAccountDisplayNumber(userAccount.getAccountNumber()));
        return template.insert(userAccount).getId();
    }

    public UserAccount getAccountDetails(String accountId, String username) {
        if (accountExistForUser(accountId, username)) {
            return template.findOne(FinanceAppQuery.findByIdQuery(accountId), UserAccount.class);
        } else {
            throw new FinanceAppException("This account %s do not belong to the user %s.", accountId, username);
        }
    }

    public List<UserAccount> getUserAccounts(String username) {
        String userProfileId = template.findOne(FinanceAppQuery.findByUsernameQuery(username), UserProfile.class).getId();
        return template.find(FinanceAppQuery.findAccountsByUserProfileIdQuery(userProfileId), UserAccount.class);
    }

    public Boolean accountExistForUser(String accountId, String username) {
        return template.findOne(FinanceAppQuery.findByUsernameQuery(username), UserProfile.class).getUserAccounts().stream().anyMatch(
                userAccount -> userAccount.getId().equalsIgnoreCase(accountId));
    }
}
