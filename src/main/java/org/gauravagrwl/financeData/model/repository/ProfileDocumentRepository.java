package org.gauravagrwl.financeData.model.repository;

import org.gauravagrwl.financeData.model.documents.userProfile.UserProfileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProfileDocumentRepository extends MongoRepository<UserProfileDocument, String> {

    public boolean existsByUserName(String userName);

    public List<UserProfileDocument> findByUserName(String username);
}
