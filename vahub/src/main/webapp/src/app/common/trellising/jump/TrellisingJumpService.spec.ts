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

import {TrellisingJumpService} from './TrellisingJumpService';
import {DatasetViews} from '../../../security/DatasetViews';
import {TabId} from '../store/ITrellising';

export class MockDatasetViews extends DatasetViews {

    constructor() {
        super(null);
    }

    getJumpFilterValues(originTab: string, filterItemKey: string): string[] {
        switch (originTab) {
            case 'cardiac':
                switch (filterItemKey) {
                    case 'socs':
                        return ['CARDIAC DISORDERS'];

                    default:
                        return [];
                }
            case 'liver':
                switch (filterItemKey) {
                    case 'socs':
                        return ['LIVER DISORDERS'];
                    case 'labNames':
                        return [];
                    default:
                        return [];
                }
            default:
                return [];
        }
    }
}

describe('GIVEN the TrellisingJumpService WHEN getLinksForCurrentTab', () => {
    const service = new TrellisingJumpService(null, null, new MockDatasetViews());

    it('THEN removes jumps with no presets', () => {
        const result = service.getLinksForCurrentTab(TabId.LIVER_HYSLAW);
        expect(result.length).toBe(1);
        expect(result[0].metadataFilterKey).toEqual('socs');
    });


    it('THEN includes jumps with no presets', () => {
        const result = service.getLinksForCurrentTab(TabId.CARDIAC_BOXPLOT);
        expect(result.length).toBe(1);
    });
});
