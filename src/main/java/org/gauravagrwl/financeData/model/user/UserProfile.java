package org.gauravagrwl.financeData.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gauravagrwl.financeData.model.common.AuditMetadata;
import org.gauravagrwl.financeData.model.userAccounts.accounts.UserAccount;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "UserProfiles")
public class UserProfile {
    @MongoId
    private String id;

    @NotBlank(message = "First Name is Required.")
    private String firstName;

    @NotBlank(message = "Last Name is Required.")
    private String lastName;

//    @JsonIgnore
    private String secrectKey;

    @NotBlank(message = "Email-Address Name is Required.")
    private String emailAddress;

    @NotBlank(message = "Phone Name is Required.")
    private String phoneNumber;

    @Indexed(unique = true, background = true)
    private String userName;

    @JsonIgnore
    @ReadOnlyProperty
    @DocumentReference(lookup = "{'userProfileId':?#{#self._id} }", lazy = true)
    private List<? extends UserAccount> userAccounts;

    @JsonIgnore
    private AuditMetadata audit = new AuditMetadata();

    @JsonIgnore
    @Version
    private Integer version;

}
