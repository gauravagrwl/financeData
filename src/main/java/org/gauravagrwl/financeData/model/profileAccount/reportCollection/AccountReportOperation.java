package org.gauravagrwl.financeData.model.profileAccount.reportCollection;

import org.springframework.data.mongodb.core.query.Query;

public interface AccountReportOperation {

    //Not Needed
    Query findByNameQuery();

    //Not needed
    void addUpdateTransaction(HoldingCollection ard);
}
