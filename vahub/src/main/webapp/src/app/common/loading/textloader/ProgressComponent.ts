import {Component, Input, OnInit, OnChanges, OnDestroy, SimpleChanges} from '@angular/core';
import {ProgressService} from './ProgressService';
import {TabId, MAX_NUMBER} from '../../trellising/store';

/**
 * Component for progress loader with text messages
 *
 * @property {[{}]} messages         - loading messages
 * @property {boolean} loading       - input parameter, loading is shown if it is set to true
 * @property {TabId} tabId           - id of the currently opened tab
 * @property {number} height         - height
 * @property {string} backDropHeight - height in px
 * @property {string} topOfMessage   - margin top
 * @property {number} intervalId     - id of the current setInteval
 */
@Component({
    selector: 'progress-messages',
    templateUrl: 'ProgressComponent.html',
    styleUrls: ['ProgressComponent.css']
})
export class ProgressComponent implements OnInit, OnChanges, OnDestroy {
    public messages = [
        {text: ''},
        {text: ''},
        {text: ''},
        {text: ''},
        {text: ''},
        {text: ''},
        {text: ''},
        {text: ''},
        {text: ''},
        {text: ''}];

    @Input() loading: boolean;
    @Input() tabId: TabId;
    @Input() height: number;

    backDropHeight = '';
    topOfMessage = '';
    intervalId: number;

    constructor(private progressService: ProgressService) {
    }

    ngOnInit(): void {
        if (this.loading) {
            this.updateMessages();
        }
    }

    /**
     * Adds messages or removes them when the time is up or when downloading is over, sets bounds
     * @param change
     */
    ngOnChanges(change: SimpleChanges): void {
        if (this.loading) {
            this.updateMessages();
        } else {
            clearInterval(this.intervalId);
        }
        if (this.height) {
            this.getBounds();
        }
    }

    ngOnDestroy(): void {
        clearInterval(this.intervalId);
    }

    private updateMessages(): void {
        let counter = 0;
        clearInterval(this.intervalId);
        const frame = () => {
            if (counter >= MAX_NUMBER) {
                clearInterval(this.intervalId);
            } else {
                counter++;
                for (let i = 0; i < this.messages.length - 1; i++) {
                    this.messages[i].text = this.messages[i + 1].text;
                }
                this.messages[this.messages.length - 1].text = this.progressService.generateMessage(this.tabId);
            }
        };
        this.intervalId = window.setInterval(frame, 200);
    }

    /**
     * Calculates top margin and component height
     */
    public getBounds(): void {
        this.backDropHeight = this.height.toString() + 'px';
        this.topOfMessage = ((this.height / 2) - 100).toString() + 'px';
    }

}
