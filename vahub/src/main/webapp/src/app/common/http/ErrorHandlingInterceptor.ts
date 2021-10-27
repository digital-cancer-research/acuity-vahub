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

import {
    HttpEvent,
    HttpInterceptor,
    HttpHandler,
    HttpRequest,
    HttpErrorResponse
} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import * as toastr from 'toastr/build/toastr.min.js';

export class ErrorHandlingInterceptor implements HttpInterceptor {

    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

        return next.handle(request).catch((err: any) => {
            if (err instanceof HttpErrorResponse) {
                const error = {title: '', text: ''};
                switch (err.status) {
                    case 401:
                        error.title = 'Access denied';
                        error.text = 'Sorry, you do not have the necessary access rights to view this (data/page/visualisation)';
                        break;
                    case 404:
                        error.title = 'Not found';
                        error.text = 'Sorry, the page you have requested cannot be found. '
                            + 'Please contact the IT help desk if this error persists';
                        break;
                    case 409:
                        error.title = 'Conflict';
                        error.text = 'Data being added already exists';
                        break;
                    case 419:
                        error.title = 'Session timed out';
                        error.text = 'Sorry, your session has timed out. The page needs to be refreshed and you will need to log in';
                        break;
                    case 501:
                    case 500:
                        error.title = 'Internal server error';
                        error.text = 'Sorry, an error has occurred. Please try again. '
                            + 'If the issue persists then please contact the IT help desk';
                        break;
                    case 0:
                        error.title = 'No network connection';
                        error.text = 'Sorry, an error has occurred. Please check your network connection and try again';
                        break;
                    case 200:
                        return;
                    default:
                        error.title = 'HTTP ' + err.status;
                        error.text = err.statusText;
                        break;
                }
                toastr['error'](error.text, error.title);
                return Observable.throw(err);
            }
        });
    }
}
