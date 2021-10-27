import {Pipe, PipeTransform} from '@angular/core';

@Pipe({ name: 'dodTitlePipe' })
export class DodTitlePipe implements PipeTransform {
    transform(name: string): string {
        switch (name) {
            case 'ctdna':
                return 'VAF of ctDNA';
            case 'biomarker':
                return 'Genomic Profile';
            case 'Dose Proportionality':
                return 'PK Parameters (Dose)';
            case 'pkResultWithResponse':
                    return 'PK Parameters (Response)';
            case 'recist-pk':
                return 'RECIST';
            default:
                return name;
        }
    }
}
