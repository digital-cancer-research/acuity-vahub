import {Pipe, PipeTransform} from '@angular/core';
/**
 * Transform uppercase snake case into sentence case
 * Example:
 * {{ SUBJECT_ID | sentenceCase }}
 * formats to: Subject ID
 */

@Pipe({name: 'roundValue'})
export class RoundValuePipe implements PipeTransform {
    transform(value: number): number {
        return Math.round(value * 100) / 100;
    }

}
