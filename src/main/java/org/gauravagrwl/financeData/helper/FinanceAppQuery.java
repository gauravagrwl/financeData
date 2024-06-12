package org.gauravagrwl.financeData.helper;

import org.gauravagrwl.financeData.exceptions.FinanceAppException;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

public class FinanceAppQuery {


    public static Update updateBooleanValueIndicator(String key, Boolean bool) {
        return Update.update(key, bool);
    }

    public static Query findByUsernameQuery(String username) {
        return new Query(
                Criteria.where("userName").is(username)
        );
    }

    public static Query findByIdQuery(String id) {
        return new Query(
                Criteria.where("id").is(id));
    }

    public static Query findAllDataQuery() {
        return new Query();
    }

    public static Query findAccountsByUserProfileIdQuery(String userProfileId) {
        return new Query(Criteria.where("userProfileId").is(userProfileId));
    }

    public static Query findAndSortAllBankStatementQuery(String accountId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "transactionDate").and(Sort.by(Sort.Direction.ASC, "type"));
        return new Query(Criteria.where("accountId").is(accountId)).with(sort);
    }

    public static Aggregation findDuplicateTransactionAggregationQuery(UserAccount userAccount) {
        MatchOperation matchAccountId = Aggregation.match(Criteria.where("accountId").is(userAccount.getId()));
        switch (userAccount.getInstitutionCategory()) {
            case BANKING -> {
                GroupOperation groupOperation = group("transactionDate", "instrument", "description", "transactionType", "quantity", "price", "amount", "fee")
                        .push("_id").as("ids").count().as("count");
                MatchOperation filterGroups = Aggregation.match(Criteria.where("count").gt(1));
                UnwindOperation unwindOperation = Aggregation.unwind("ids");
                ProjectionOperation projectToUser = Aggregation.project("count", "ids");
                return newAggregation(matchAccountId, groupOperation, filterGroups, projectToUser);
            }
            case INVESTMENT -> {
                GroupOperation groupOperation = group("transactionDate", "description", "amount", "type")
                        .push("_id").as("ids").count().as("count");
                MatchOperation filterGroups = Aggregation.match(Criteria.where("count").gt(1));
                UnwindOperation unwindOperation = Aggregation.unwind("ids");
                ProjectionOperation projectToUser = Aggregation.project("count", "ids");
                return newAggregation(matchAccountId, groupOperation, filterGroups, projectToUser);
            }
            default ->
                    throw new FinanceAppException("No duplicate query is defined for profile: %s", userAccount.getInstitutionCategory());
        }
    }

}
