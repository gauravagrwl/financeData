package org.gauravagrwl.financeData.model.repositories;

import io.swagger.v3.oas.annotations.Hidden;
import org.gauravagrwl.financeData.model.profile.UserProfileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

@Hidden
public interface ProfileDocumentRepository extends MongoRepository<UserProfileDocument, String> {

    public boolean existsByUserName(String userName);

    public List<UserProfileDocument> findByUserName(String userName);

}
