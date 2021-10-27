/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

export const EXPORT_DELIMETER = '+++';

export class ExportUtils {

    static convertTreeToCsv(tree) {
        const result = [];
        const h = Math.max(...tree.map(tr => ExportUtils.height(tr)));
        const queue = tree;
        while (queue.length > 0) {
            const child = queue[queue.length - 1];
            if (!child.row) {
                child.row = 0;
            }
            --queue.length;
            const arr = [child.headerName];
            if (!result[child.row]) { result[child.row] = []; }
            if (child.children) {
                queue.unshift(...child.children.map(a => Object.assign({row: (child.row || 0) + 1}, a)));


                const count = this.deepCount(child);
                if (count > 0) {
                    arr.push(...Array(count - 1).fill(''));
                }
                result[child.row].unshift(...arr);
            } else if (child.row !== h && !child.hide) {
                result[child.row].unshift('');
                child.row++;
                queue.unshift(child);
            }

        }
        return result.map(a => a.join(EXPORT_DELIMETER)).join('\n');

    }


    /**
     * Export content for header columns as mimetype to file with filename
     * @param {string} fileName
     * @param {string} content
     * @param {string} mimeType
     */
    static export(fileName: string, content: string, mimeType: string) {
        content = ExportUtils.convertAgGridTextToCsv(content);
        this.download(fileName, content, mimeType);
    }

    private static download(fileName: string, content: string, mimeType: string) {
        // for Excel, we need \ufeff at the start
        // http://stackoverflow.com/questions/17879198/adding-utf-8-bom-to-string-blob
        const blobObject = new Blob(['\ufeff', content], {
            type: mimeType
        });

        // Chrome
        const downloadLink = document.createElement('a');
        downloadLink.href = (<any>window).URL.createObjectURL(blobObject);
        (<any>downloadLink).download = fileName;

        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);

    }

    /**
     * Convert content from ag grid export function to real csv
     * This function is necessary as aggrid has problems with incorrect positions of column headers
     * @param {string} content ag grid csv
     * @returns {string} csv content
     */
    private static convertAgGridTextToCsv(content: string) {
        return content.split('\n').map(
            (row, i) => {
                const cols = row.trim().split(EXPORT_DELIMETER)
                    .map(
                        col => col.replace('""', ''))
                    .map(col => /".+"/
                        .test(col) ? col.substr(1, col.length - 2) : col)
                    .map(col => col.indexOf(',') === -1 ? col : '"' + col + '"');
                return cols.join(',');
            }).join('\n');
    }

    private static deepCount(arr) {
        return arr.children ? arr.children.reduce((l, r) => l + (ExportUtils.deepCount(r)), 0) : 1;
    }

    private static height(arr) {
        if (arr.children) {
            return Math.max(...arr.children.map(e => ExportUtils.height(e))) + 1;
        }
        return 0;
    }
}
