import { Injectable } from '@angular/core';
import { Subscription } from 'rxjs/Subscription';
import { SessionEventService } from '../../../session/module';

/**
 * Service for showing modal window on every dataset change
 */
@Injectable()
export class ApplySafetyFiltersModalService {

    private sessionSubscription: Subscription;
    private reShowMessage = false;

    constructor(private sessionEventService: SessionEventService) {
        this.subscribeToStudyChange();
    }

    private subscribeToStudyChange(): void {
        this.unsubscribeToStudyChange();

        // everytime the user swaps datasets, show the message again
        if (this.sessionEventService != null) {
            this.sessionSubscription = this.sessionEventService.currentDatasets.subscribe((roi) => {
                this.reShowMessage = true;
            });
        }
    }

    private unsubscribeToStudyChange(): void {
        if (this.sessionSubscription) {
            this.sessionSubscription.unsubscribe();
        }
    }

    setReShowMessage(reShowMessage: boolean): void {
        this.reShowMessage = reShowMessage;
    }

    isReShowMessage(): boolean {
        return this.reShowMessage;
    }
}
