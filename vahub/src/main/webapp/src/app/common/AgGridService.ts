import {Injectable} from '@angular/core';
import {LicenseManager} from 'ag-grid-enterprise/main';
import {ConfigurationService} from '../configuration/module';

export enum AgGridVersionEnum {
    Community,
    Enterprise
}

@Injectable()
export class AgGridService {

    private agGridKey: string;

    constructor(configurationService: ConfigurationService) {
        this.agGridKey = configurationService.agGridKey;
    }

    // get ag-grid version (Community or Enterprise) installed
    getAgGridVersion(): AgGridVersionEnum {
        if (this.agGridKey && LicenseManager.setLicenseKey) {
            return AgGridVersionEnum.Enterprise;
        } else {
            return AgGridVersionEnum.Community;
        }
    }

    initAgGridLicense(): void {
        if (this.agGridKey && LicenseManager.setLicenseKey) {   // check LicenseManager.setLicenseKey in case
                                                                // license key is provided but 'ag-grid-enterprise'
                                                                // package is not installed;
                                                                // run 'npm run ag-grid-enterprise -- on|off' from
                                                                // the webapp root to toggle 'ag-grid-enterprise'
                                                                // installation
            LicenseManager.setLicenseKey(this.agGridKey);
        }
    }
}
