package org.gauravagrwl.financeData.service;

import org.gauravagrwl.financeData.model.accountReportsModel.ReportCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportService {

    @Autowired
    MongoTemplate template;


    public List<ReportCollection> getAccountReports(String accountReportCollectionName) {
        List<ReportCollection> holdings = template.findAll(ReportCollection.class, accountReportCollectionName);
        return holdings;

    }
}
