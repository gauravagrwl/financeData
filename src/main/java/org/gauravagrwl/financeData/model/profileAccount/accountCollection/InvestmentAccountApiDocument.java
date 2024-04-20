package org.gauravagrwl.financeData.model.profileAccount.accountCollection;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InvestmentAccountApiDocument {

    private String userId;
    private String userSecrect;
    private String brokerProdApiUrl;
    private String brokerProdApiKey;
    private String brokerProdApiSecrect;
    private String brokerTestApiUrl;
    private String brokerTestApiKey;
    private String brokerTestApiSecrect;

}
