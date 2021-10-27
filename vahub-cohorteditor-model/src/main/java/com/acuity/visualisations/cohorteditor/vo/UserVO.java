package com.acuity.visualisations.cohorteditor.vo;

import com.acuity.va.security.acl.domain.AcuitySidDetails;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserVO extends AcuitySidDetails {

    /**
     * We could just use {@link AcuitySidDetails}, but we need an ID passed to Hibernate
     * so that it knows whether to insert or update
     */
    private Long id;

    public UserVO(Long id, String prid, String fullName) {
        super(prid, fullName);
        this.id = id;
    }
}
