package org.gauravagrwl.financeData.model.repositories;

import io.swagger.v3.oas.annotations.Hidden;
import org.gauravagrwl.financeData.model.profile.UserProfileCollection;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@Hidden
public interface ProfileDocumentRepository extends MongoRepository<UserProfileCollection, String> {

    public boolean existsByUserName(String userName);

    public List<UserProfileCollection> findByUserName(String userName);

}
