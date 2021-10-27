import {Injectable} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import {
    SpirometryYAxisValue, EcgYAxisValue, EcgWarnings, VitalsYAxisValue, LabsYAxisValue
} from '../../store/ITimeline';

@Injectable()
export class TimelineConfigurationService {

    public updateSpirometryYAxisValue: Subject<SpirometryYAxisValue> = new Subject<SpirometryYAxisValue>();
    public updateEcgYAxisValue: Subject<EcgYAxisValue> = new Subject<EcgYAxisValue>();
    public updateEcgWarnings: Subject<EcgWarnings> = new Subject<EcgWarnings>();
    public updateVitalsYAxisValue: Subject<VitalsYAxisValue> = new Subject<VitalsYAxisValue>();
    public updateLabsYAxisValue: Subject<LabsYAxisValue> = new Subject<LabsYAxisValue>();

    constructor() {
    }

    setSpirometryYAxisValue(value: SpirometryYAxisValue): void {
        this.updateSpirometryYAxisValue.next(value);
    }

    setEcgYAxisValue(value: EcgYAxisValue): void {
        this.updateEcgYAxisValue.next(value);
    }

    setEcgWarnings(value: EcgWarnings): void {
        this.updateEcgWarnings.next(value);
    }

    setVitalsYAxisValue(value: VitalsYAxisValue): void {
        this.updateVitalsYAxisValue.next(value);
    }

    setLabsYAxisValue(value: LabsYAxisValue): void {
        this.updateLabsYAxisValue.next(value);
    }
}
