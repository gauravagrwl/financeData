package org.gauravagrwl.financeData.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FinanceDataService {
    MongoTemplate template;

    public FinanceDataService(MongoTemplate template) {
        this.template = template;
    }

    public String dropAllCollection(){
        template.getCollectionNames().stream().forEach(col -> template.dropCollection(col));
        return "Warning All Database is dropped";
    }

    public String dropCollection(String collectionName){
        template.getCollectionNames().stream().forEach(
                col -> {
        if(StringUtils.equalsIgnoreCase(collectionName, col))
            template.dropCollection(col);
                }
        );
        return collectionName + " is dropped";
    }

    public Set<String> getAllCollections() {
        return template.getCollectionNames();
    }
}
