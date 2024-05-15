package org.gauravagrwl.financeData.model.repositories;

import io.swagger.v3.oas.annotations.Hidden;
import org.gauravagrwl.financeData.model.accountCollection.AccountCollection;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

@Hidden
public interface AccountRepository extends MongoRepository<AccountCollection, String> {

    public List<AccountCollection> findByProfileDocumentId(String profileDocumentId, Sort sort);

    public Boolean existsByIdAndProfileDocumentId(String id, String profileDocumentId);

//    @Update("{ '$set' : { 'updateAccountStatement' : ?#{[1]}, 'updateAccountReport' : ?#{[2]} } }")
//    void findAndUpdateUpdateAccountStatementAndUpdateAccountReportById(String id, Boolean updateAccountStatement, Boolean updateAccountReport);

    @Update("{ '$set' : { 'balanceCalculated' : ?#{[1]} } }")
    void findAndUpdateBalanceCalculatedById(String id, Boolean isBalanceCalculated);

    @Update("{ '$set' : { 'updateAccountStatementNeeded' : ?#{[1]}, 'updateAccountReportNeeded' : ?#{[2]}, 'updateCashFlowReportNeeded' : ?#{[3]} } }")
    void findAndUpdateNeededFlagById(String id, Boolean updateAccountStatementNeeded, Boolean updateAccountReportNeeded, Boolean updateCashFlowReportNeeded);
}
