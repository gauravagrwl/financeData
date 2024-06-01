package org.gauravagrwl.financeData.service;

import org.gauravagrwl.financeData.model.accountReportsModel.ReportCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    MongoTemplate template;


    public List<ReportCollection> getAccountReports(String accountReportCollectionName) {
        Sort sort = Sort.by(Sort.Direction.DESC, "totalQuantity");
        Query query = new Query(Criteria.where("totalQuantity").gt(0));
        query.with(sort);
        List<ReportCollection> holdings = template.find(query, ReportCollection.class, accountReportCollectionName);
        return holdings;

    }
}
