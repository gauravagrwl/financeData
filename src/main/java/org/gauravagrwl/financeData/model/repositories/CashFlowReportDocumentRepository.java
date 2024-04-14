package org.gauravagrwl.financeData.model.repositories;

import io.swagger.v3.oas.annotations.Hidden;
import org.gauravagrwl.financeData.model.reports.CashFlowReportDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

@Hidden
public interface CashFlowReportDocumentRepository
                extends MongoRepository<CashFlowReportDocument, String> {

        CashFlowReportDocument findByAccountStatementId(String accountStatementId);

        Boolean existsByAccountStatementId(String accountStatementId);

        Long deleteByAccountStatementId(String accountStatementId);

}
