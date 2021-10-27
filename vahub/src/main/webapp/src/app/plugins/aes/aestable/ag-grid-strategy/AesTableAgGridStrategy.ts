import {Injectable} from '@angular/core';
import {GridOptions, ColDef} from 'ag-grid/main';
import {AgGridService, AgGridVersionEnum} from '../../../../common/AgGridService';
import {AesTableServiceEnterprise} from './AesTableServiceEnterprise';
import {AesTableServiceCommunity} from './AesTableServiceCommunity';

export interface IAesTableService {
    getColumnDefs(): ColDef[];
    getGridOptions(): GridOptions;
    prepareTableData(rows: InMemory.AesTable[]): Array<any>;
}

interface IAesTableStrategy {
    aesTableService: IAesTableService;
}

@Injectable()
export class AesTableAgGridStrategy {
    constructor(private agGridService: AgGridService,
                private aesTableServiceEnterprise: AesTableServiceEnterprise,
                private aesTableServiceCommunity: AesTableServiceCommunity) {
    }

    getStrategy(): IAesTableStrategy {
        switch (this.agGridService.getAgGridVersion()) {
            case AgGridVersionEnum.Enterprise:
                return {
                    aesTableService: this.aesTableServiceEnterprise
                };
            case AgGridVersionEnum.Community:
                return {
                    aesTableService: this.aesTableServiceCommunity
                };
        }
    }

}
