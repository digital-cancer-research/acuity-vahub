package com.acuity.visualisations.rest.resources.study;

import com.acuity.va.security.acl.domain.AcuityObjectIdentity;
import com.acuity.va.security.auth.common.ISecurityResourceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Profile("!local-no-security")
@RequiredArgsConstructor
@Service
public class VASecurityStrategy implements PermissionsStrategy {

    private final ISecurityResourceClient securityResourceClient;

    @Override
    public List<AcuityObjectIdentity> getAcuityObjectIdentities(String userSid) {
        return new ArrayList<>(securityResourceClient.getAclsForUser(userSid));
    }
}
