import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {ConfigurationService} from '../configuration/ConfigurationService';
import ExtendedOptions = Request.ExtendedOptions;

@Component({
    selector: 'home',
    templateUrl: 'HomeComponent.html',
    styleUrls: ['./HomeComponent.css']
})
export class HomeComponent {
    loading: boolean;
    extendedOptions: ExtendedOptions;
    hasCompanyLogo = false;
    pathToImages: string;

    constructor(private router: Router,
                private configurationService: ConfigurationService) {
        this.pathToImages = configurationService.pathToStatic;
        this.checkIfHasCompany(configurationService.pathToStatic);
        this.extendedOptions = configurationService.brandingProperties.extendedOptions;
    }

    public navigate(url: string): void {
        this.router.navigateByUrl(url);
    }

    getTooltipText(hasOption: boolean): string {
        return !hasOption
            ? 'You are not subscribed to this service, please contact \nsupport@digitalecmt.org for more information' : '';
    }

    private checkIfHasCompany(path: string): void {
        const imageData = new Image();

        imageData.onload = () => {
            if (this.hasCompanyLogo) {
                return;
            }
            this.hasCompanyLogo = true;
        };
        imageData.onerror = () => {
            this.hasCompanyLogo = false;
        };
        imageData.src = `/assets/${path}/company.png`;
    }
}
