import {Injectable} from '@angular/core';

export interface Env {
    isLocalHost: boolean;
}

/**
 * Determines the env of the application.
 *
 * Ie test, dev, prod
 */
@Injectable()
export class EnvService {

    env: Env;

    static getHostName(): string {
        return window.location.hostname;
    }

    constructor() {

        console.log('EnvService');
        console.log('host ', EnvService.getHostName());
        const host = EnvService.getHostName();

        this.env = {
            isLocalHost: (host.indexOf('localhost') !== -1)
        };

        console.log('env is ', this.env);
    }
}
