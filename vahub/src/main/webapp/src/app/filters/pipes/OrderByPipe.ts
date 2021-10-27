import {Pipe, PipeTransform} from '@angular/core';

@Pipe({ name: 'sort' })
export class OrderByPipe implements PipeTransform {
    transform(array: Array<string>): Array<string> {
        const compare = (a, b) => {
            const result = a - b;
            if (isNaN(result)) {
                return a.localeCompare(b);
            }
            return result;
        };
        return array.sort(compare);
    }
}
