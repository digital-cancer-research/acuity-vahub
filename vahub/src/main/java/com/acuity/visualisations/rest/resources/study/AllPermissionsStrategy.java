/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
