package org.gauravagrwl.financeData.helper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class FinanceDataHelper {

    public static final String UNDER_SCORE = "_";

    public static String getAccountDisplayNumber(String accountNumber) {
        return StringUtils.join("XXXX", StringUtils.right(accountNumber, 4));
    }

    public static String getStatementCollectionName(String... elements) {
        return StringUtils.join(elements, UNDER_SCORE).concat("_statement_Collection");
    }

    public static String getLedgerCollectionName(String... elements) {
        return StringUtils.join(elements, UNDER_SCORE).concat("_ledgerDocument");
    }

    public static Query findById(String id) {
        Query query = new Query(
                Criteria.where("id").is(id));
        return query;
    }

}
