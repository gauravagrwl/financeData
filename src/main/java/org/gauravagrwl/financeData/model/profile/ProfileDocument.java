package org.gauravagrwl.financeData.model.profile;

import java.util.List;

import org.gauravagrwl.financeData.model.audit.AuditMetadata;
import org.gauravagrwl.financeData.model.profileAccount.accountDocument.AccountDocument;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.MongoId;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "profile_document")
public class ProfileDocument {

    @MongoId
    private String id;

    private String firstName, lastname, secrectKey, emailAddress, phoneNumber;

    @Indexed(unique = true, background = true)
    private String userName;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'profileDocumentId':?#{#self._id} }", lazy = true)
    private List<? extends AccountDocument> userAccounts;

    private AuditMetadata audit = new AuditMetadata();

    @Version
    private Integer version;

}
