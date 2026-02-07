import {Pipe, PipeTransform} from '@angular/core';

@Pipe({
  name: 'truncate'
})
export class TruncatePipe implements PipeTransform {

  transform(value: string | undefined, ...args: number[]): unknown {
    if (typeof value !== 'string') return '';
    let len = args[0];

    if (len == null) len = 10;
    if (value.length > len) {
      value = value.substring(0, 10) + '...';
    }
    return value;
  }

}
