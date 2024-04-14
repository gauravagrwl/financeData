package org.gauravagrwl.financeData.service;

import java.util.List;

import org.gauravagrwl.financeData.exception.FinanceDataException;
import org.gauravagrwl.financeData.helper.FinanceDataHelper;
import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.gauravagrwl.financeData.model.repositories.AccountDocumentRepository;
import org.gauravagrwl.financeData.model.repositories.AccountStatementDocumentRepository;
import org.gauravagrwl.financeData.model.repositories.CashFlowReportDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AccountService {

    private AccountDocumentRepository accountDocumentRepository;

    private ProfileService profileService;

    @Autowired
    CashFlowReportDocumentRepository cashFlowTransactionDocumentRepository;

    public AccountService(AccountDocumentRepository accountDocumentRepository, ProfileService profileService,
            AccountStatementDocumentRepository accountTransactionDocumentRepository) {
        this.accountDocumentRepository = accountDocumentRepository;
        this.profileService = profileService;
    }

    public String addUserAccount(AccountDocument accountDocument, String userName) {

        String profileId = profileService.getUserProfileDocument(userName).getId();
        accountDocument.setProfileDocumentId(profileId);
        String accountDisplayNumber = FinanceDataHelper.getAccountDisplayNumber(accountDocument.getAccountNumber());
        accountDocument.setAccountStatementCollectionName(
                FinanceDataHelper.getStatementCollectionName(accountDisplayNumber));
        AccountDocument save = accountDocumentRepository.save(accountDocument);

        return save.getId();
    }

    public AccountDocument getAccountDocument(String accountId, String userName) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            return accountDocumentRepository.findById(accountId).get();
        }
        throw new FinanceDataException("Account Id is null or not exist.");

    }

    public boolean isUserAccountExist(String accountId, String userName) {
        String profileId = profileService.getUserProfileDocument(userName).getId();
        return accountDocumentRepository.existsByIdAndProfileDocumentId(accountId,
                profileId);
    }

    public List<AccountDocument> getUserAccounts(String userName) {
        String profileId = profileService.getUserProfileDocument(userName).getId();
        Sort sort = Sort.by(Direction.DESC, "institutionCurrency");
        List<AccountDocument> byProfileDocumentId = accountDocumentRepository.findByProfileDocumentId(profileId, sort);
        return byProfileDocumentId;
    }

    public void toggleAccount(String userName, @NotNull String accountId) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            AccountDocument document = accountDocumentRepository.findById(accountId).get();
            document.setIsActive(!document.getIsActive());
            accountDocumentRepository.save(document);
        }
        throw new FinanceDataException("Account Id is null or not exist.");
    }

    public void deleteProfileAccount(String accountId, String userName) {
        if (accountId != null && isUserAccountExist(accountId, userName)) {
            accountDocumentRepository.deleteById(accountId);
        } else {
            throw new FinanceDataException("No Account found with id: " + accountId + "for user: " + userName);
        }
    }

    public void setUpdateCalculateBalance(AccountDocument accountDocument, Boolean bool) {
        accountDocumentRepository.findAndUpdateIsBalanceCalculatedById(accountDocument.getId(), bool);
    }

    // NEED TO CHECK::::

    // public Boolean deleteAccountStatementDocument(AccountDocument
    // accountDocument, @NonNull String accountStatementId) {
    // AccountStatementDocument statement =
    // accountStatementDocumentRepository.findById(accountStatementId).get();

    // if (accountStatementDocumentRepository.existsById(accountStatementId)) {
    // deleteCashFlowDocument(accountStatementId);
    // accountStatementDocumentRepository.deleteById(accountStatementId);
    // log.info("Account Statment deleted for id: " + accountStatementId
    // + !accountStatementDocumentRepository.existsById(accountStatementId));
    // }
    // List<AccountStatementDocument> duplicateStatementList =
    // findAllByStatementDocument(statement);
    // if (duplicateStatementList.size() == 1) {
    // accountStatementDocumentRepository.findAndUpdateDuplicateById(duplicateStatementList.get(0).getId(),
    // Boolean.FALSE);
    // }
    // accountDocument.setIsBalanceCalculated(Boolean.FALSE);
    // accountDocumentRepository.save(accountDocument);
    // performAccountProcessing(accountDocument);
    // return true;
    // }

    // private boolean deleteCashFlowDocument(@NonNull String accountStatementId) {
    // if
    // (cashFlowTransactionDocumentRepository.existsByAccountStatementId(accountStatementId))
    // {
    // log.info("CashFlow Transaction deleted for id: " + accountStatementId + "and
    // Total delete count is: "
    // +
    // cashFlowTransactionDocumentRepository.deleteByAccountStatementId(accountStatementId));
    // return true;
    // }
    // return false;
    // }

    // // @Scheduled(cron = "${updateCashFlowStatement}")
    // public void performAccountProcessing() {
    // List<AccountDocument> profileAccountDocuments = new ArrayList<>();
    // profileService.getAllUserProfileDocument()
    // .forEach(profile ->
    // profileAccountDocuments.addAll(getUserAccounts(profile.getUserName())));

    // profileAccountDocuments.forEach(accountDocument ->
    // performAccountProcessing(accountDocument));

    // }

    // // TODO:
    // // Add filter for which all account balance can be calculated and be added
    // // Balance calculation: Bank Account expect credit (primary account)
    // // to cashflow statement for all cash in and cash out (primary account)
    // @SuppressWarnings("unchecked")
    // private void performAccountProcessing(AccountDocument accountDocument) {
    // if
    // (InstitutionCategoryEnum.BANKING.compareTo(accountDocument.getInstitutionCategory())
    // == 0) {
    // List<BankAccountStatementDocument> bankAccountStatementList =
    // (List<BankAccountStatementDocument>) getAccountStatementDocuments(
    // accountDocument);
    // if (AccountTypeEnum.CREDIT.compareTo(accountDocument.getAccountType()) != 0)
    // {
    // accountAsyncService.calculateAccountStatementBalance(accountDocument,
    // bankAccountStatementList);
    // }

    // accountAsyncService.updateCashFlowDocuments(accountDocument,
    // bankAccountStatementList);
    // }
    // }

}
