import {Observable} from 'rxjs/Observable';
import {Resolve} from '@angular/router';
import {Injectable} from '@angular/core';
import {ConfigurationService} from '../configuration/ConfigurationService';
import ConfigurationResponse = Request.ConfigurationResponse;

@Injectable()
export class PropertiesResolver implements Resolve<Observable<ConfigurationResponse>> {
    constructor(private configurationService: ConfigurationService) {}

    resolve() {
        return this.configurationService.getConfigurationProperties();
    }
}
