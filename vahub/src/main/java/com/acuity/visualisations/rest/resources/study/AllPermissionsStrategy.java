package com.acuity.visualisations.rest.resources.study;

import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.va.security.acl.domain.AcuityObjectIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Profile("local-no-security")
@RequiredArgsConstructor
@Service
public class AllPermissionsStrategy implements PermissionsStrategy {

    private final InfoService infoService;

    @Override
    public List<AcuityObjectIdentity> getAcuityObjectIdentities(String userSid) {
        return infoService.generateObjectIdentities();
    }
}
