package org.gauravagrwl.financeData.model.repositories;

import java.math.BigDecimal;
import java.util.List;

import io.swagger.v3.oas.annotations.Hidden;
import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Update;

@Hidden
public interface AccountDocumentRepository extends MongoRepository<AccountDocument, String> {

    public List<AccountDocument> findByProfileDocumentId(String profileDocumentId, Sort sort);

    public Boolean existsByIdAndProfileDocumentId(String id, String profileDocumentId);

    @Update("{ '$set' : { 'accountBalance' : ?#{[1]} } }")
    void findAndUpdateAccountBalanceById(String id, BigDecimal value);

    @Update("{ '$set' : { 'isBalanceCalculated' : ?#{[1]} } }")
    void findAndUpdateIsBalanceCalculatedById(String id, Boolean bool);

}
