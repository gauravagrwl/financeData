package org.gauravagrwl.financeData.model.repositories;

import io.swagger.v3.oas.annotations.Hidden;
import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Update;

import java.util.List;

@Hidden
public interface AccountDocumentRepository extends MongoRepository<AccountDocument, String> {

    public List<AccountDocument> findByProfileDocumentId(String profileDocumentId, Sort sort);

    public Boolean existsByIdAndProfileDocumentId(String id, String profileDocumentId);

    @Update("{ '$set' : { 'updateAccountStatement' : ?#{[1]}, 'updateAccountReport' : ?#{[2]} } }")
    void findAndUpdateUpdateAccountStatementAndUpdateAccountReportById(String id, Boolean updateAccountStatement, Boolean updateAccountReport);

    @Update("{ '$set' : { 'balanceCalculated' : ?#{[1]} } }")
    void findAndUpdateBalanceCalculatedById(String id, Boolean isBalanceCalculated);

}
