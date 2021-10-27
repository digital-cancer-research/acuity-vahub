package com.acuity.visualisations.rest.resources.study;

import com.acuity.va.security.acl.domain.AcuityObjectIdentity;

import java.util.List;

/**
 * Strategy allows to bypass security during local development
 * (see {@link AllPermissionsStrategy}
 */
public interface PermissionsStrategy  {
    List<AcuityObjectIdentity> getAcuityObjectIdentities(String userSid);
}
