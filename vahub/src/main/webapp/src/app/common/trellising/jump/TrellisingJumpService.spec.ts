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
