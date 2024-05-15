package org.gauravagrwl.financeData.helper;

import jakarta.validation.constraints.NotBlank;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class FinanceDataHelper {

    public static final String UNDER_SCORE = "_";

    public static String getAccountDisplayNumber(String accountNumber) {
        return StringUtils.join("XXXX", StringUtils.right(accountNumber, 3));
    }

    public static String getStatementCollectionName(String... elements) {
        return StringUtils.join(elements, UNDER_SCORE).concat("_statement_Collection");
    }

    public static String getAccountTransactionCollectionName(String... elements) {
        return StringUtils.join(elements, UNDER_SCORE).concat("_transaction_Collection");
    }

    public static String getReportCollectionName(String... elements) {
        return StringUtils.join(elements, UNDER_SCORE).concat("_report_Collection");
    }

    public static String getAssetsCollectionName(String... elements) {
        return StringUtils.join(elements, UNDER_SCORE).concat("_rental_Collection");
    }

    public static String getCashFlowCollectionName(String... elements) {
        return StringUtils.join(elements, UNDER_SCORE).concat("_cashFlow_Collection");
    }

    public static String getHoldingCollectionName(String... elements) {
        return StringUtils.join(elements, UNDER_SCORE).concat("_holding_Collection");
    }

    public static Query findById(String id) {
        Query query = new Query(
                Criteria.where("id").is(id));
        return query;
    }

    public static Update updateDuplicateIndicatorDefination = Update.update("duplicate", Boolean.TRUE);


    public static Update updateReconcileIndicatorDefination = Update.update("reconciled", Boolean.TRUE);

    public static String getAccountDisplayName(String userName, String profile, @NotBlank(message = "Account Number is required.") String number) {
        return StringUtils.join(userName, "_", profile, StringUtils.right(number, 3));
    }
}
