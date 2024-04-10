package org.gauravagrwl.financeData.model.repository;

import org.gauravagrwl.financeData.model.documents.userAccount.AccountDocument;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccountDocumentRepository extends MongoRepository<AccountDocument, String> {
    boolean existsByIdAndProfileDocumentId(String accountId, String profileId);

    List<AccountDocument> findByProfileDocumentId(String profileId, Sort sort);
}