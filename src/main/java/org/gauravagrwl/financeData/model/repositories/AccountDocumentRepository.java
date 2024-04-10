package org.gauravagrwl.financeData.model.repositories;

import java.util.List;

import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AccountDocumentRepository extends MongoRepository<AccountDocument, String> {


    public List<AccountDocument> findByProfileDocumentId(String profileDocumentId, Sort sort);

    public Boolean existsByIdAndProfileDocumentId(String id, String profileDocumentId);

}
