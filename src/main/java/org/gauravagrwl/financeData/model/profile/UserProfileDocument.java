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

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "profile_collections")
public class UserProfileDocument {

    @MongoId
    private String id;

    @NotBlank(message = "First Name is Required.")
    private String firstName;

    @NotBlank(message = "Last Name is Required.")
    private String lastname;

    private String secrectKey;

    @NotBlank(message = "Email-Address Name is Required.")
    private String emailAddress;

    @NotBlank(message = "Phone Name is Required.")
    private String phoneNumber;

    @Indexed(unique = true, background = true)
    private String userName;

    @ReadOnlyProperty
    @DocumentReference(lookup = "{'profileDocumentId':?#{#self._id} }", lazy = true)
    private List<? extends AccountDocument> userAccounts;

    private AuditMetadata audit = new AuditMetadata();

    @Version
    private Integer version;

}
