import {Pipe, PipeTransform} from '@angular/core';

@Pipe({name: 'removeParentheses'})
export class RemoveParenthesesPipe implements PipeTransform {
    transform(value: string): string {
        if (/^\([a-zA-Z]?\).*/.test(value)) {
            return value.replace(/^\([a-zA-Z]?\)/, '');
        }
        return value;
    }
}
