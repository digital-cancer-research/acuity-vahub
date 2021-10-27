'use strict';

/* global __dirname:false */
/* eslint angular/log: 0 */

const fsExtra = require('fs-extra');
const path = require('path');

const srcFile = path.join(__dirname, '../../../../../vahub-config/src/main/resources', 'pre-commit');
const trgFile = path.join(__dirname, '../../../../..', '.git', 'hooks', 'pre-commit');

fsExtra.copySync(srcFile, trgFile);

console.log('Pre-commit hook installed successfully');