package org.gauravagrwl.financeData.model.reports;

import org.springframework.data.mongodb.core.mapping.MongoId;

public abstract class ReportDocument {
    @MongoId
    private String id;

}
