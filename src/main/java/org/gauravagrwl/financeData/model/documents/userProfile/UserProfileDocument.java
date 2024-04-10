package org.gauravagrwl.financeData.model.documents.userProfile;

import lombok.Data;
import org.gauravagrwl.financeData.model.documents.base.AuditMetaData;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Document(collection = "profileDocument")
public class UserProfileDocument {
    @MongoId
    private String id;

    private String firstName;

    private String lastname;

    private String secrectKey;

    private String emailAddress;

    private String phoneNumber;

    @Indexed(unique = true, background = true)
    private String userName;

    private AuditMetaData audit = new AuditMetaData();

    @Version
    private Integer version;

}
