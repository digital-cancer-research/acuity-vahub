import {AesTableAgGridStrategy} from './AesTableAgGridStrategy';
import {AesTableServiceCommunity} from './AesTableServiceCommunity';
import {AesTableServiceEnterprise} from './AesTableServiceEnterprise';
import {AgGridVersionEnum} from '../../../../common/AgGridService';

describe('GIVEN AesTableAgGridStrategy', () => {
    describe('WHEN selecting strategy', () => {
        let agGridServiceSpy;
        let aesTableAgGridStrategy;

        beforeEach(() => {
            agGridServiceSpy = jasmine.createSpyObj('AgGridService', ['getAgGridVersion']);

            aesTableAgGridStrategy = new AesTableAgGridStrategy(
                agGridServiceSpy,
                new AesTableServiceEnterprise(),
                new AesTableServiceCommunity());
        });

        it('THEN correct strategy returned when ag-grid Community version is used', () => {
            agGridServiceSpy.getAgGridVersion.and.returnValue(AgGridVersionEnum.Community);

            const strategy = aesTableAgGridStrategy.getStrategy();
            expect(strategy.aesTableService).toEqual(jasmine.any(AesTableServiceCommunity));
        });

        it('THEN correct strategy returned when ag-grid Enterprise version is used', () => {
            agGridServiceSpy.getAgGridVersion.and.returnValue(AgGridVersionEnum.Enterprise);

            const strategy = aesTableAgGridStrategy.getStrategy();
            expect(strategy.aesTableService).toEqual(jasmine.any(AesTableServiceEnterprise));
        });
    });
});
