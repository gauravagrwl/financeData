package org.gauravagrwl.financeData.model.userAccounts.accounts;

import com.opencsv.bean.CsvToBean;
import org.gauravagrwl.financeData.model.userAccounts.transactions.AccountTransaction;

import java.io.InputStreamReader;

public interface UserAccountOperations {
    /**
     *
     * @param reader
     * @return
     */
    CsvToBean<AccountTransaction> getCsvTransactionMapperToBean(InputStreamReader reader);
}
