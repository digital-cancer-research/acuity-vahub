import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {getServerPath} from '../common/utils/Utils';
import ConfigurationResponse = Request.ConfigurationResponse;
import BrandingProperties = Request.BrandingProperties;
import QnaMakerProperties = Request.QnaMakerProperties;

@Injectable()
export class ConfigurationService {
    public brandingProperties: BrandingProperties;
    public cbioportalUrl: string;
    public pathToStatic: string;
    public qnaMakerProperties: QnaMakerProperties;
    public agGridKey: string;

    constructor(private http: HttpClient) {
    }

    getConfigurationProperties(): Observable<ConfigurationResponse> {
        return this.http.get(getServerPath('configuration', 'properties')).map((res: ConfigurationResponse) => {
            this.setProperties(res);
            return res;
        });
    }

    setProperties(properties: ConfigurationResponse): void {
        this.brandingProperties = properties.branding;
        this.pathToStatic = this.brandingProperties.pathToImages || 'images';
        this.cbioportalUrl = properties.cbioportalUrl;
        this.qnaMakerProperties = properties.qnaMaker;
        this.agGridKey = properties.agGridKey;
    }
}
