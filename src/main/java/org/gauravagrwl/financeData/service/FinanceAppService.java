package org.gauravagrwl.financeData.service;

import lombok.extern.slf4j.Slf4j;
import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class FinanceAppService {

    private MongoTemplate template;

    public FinanceAppService(MongoTemplate template) {
        this.template = template;
    }

    /**
     *
     */
    public void dropAllCollections() {
        template.getCollectionNames().forEach(col ->
                template.remove(new Query(), col));
        log.warn("All collection is dropped.");
    }

    /**
     * @param collectionName
     */
    public void dropCollection(String collectionName) {
        if (template.collectionExists(collectionName)) {
            template.dropCollection(collectionName);
            log.warn("Collection " + collectionName + " is dropped.");
        } else {
            log.error("No collection found by name: " + collectionName);
            throw new FinanceDataException("No collection found by name " + collectionName);
        }
    }

    /**
     * @return
     */
    public Set<String> getAllCollections() {
        return template.getCollectionNames();
    }

}
