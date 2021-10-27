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

import {Component, OnDestroy, Renderer2} from '@angular/core';
import {Subscription} from 'rxjs/Subscription';
import {ActivatedRoute} from '@angular/router';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {ConfigurationService} from '../configuration/module';
import {isEmpty} from 'lodash';
import QnaMakerProperties = Request.QnaMakerProperties;

@Component({
    selector: 'manual-component',
    templateUrl: 'ManualComponent.html',
    styleUrls: ['./ManualComponent.css']
})
export class ManualComponent implements OnDestroy {
    private routeFragmentSubscription: Subscription;
    question: string;
    answer = '';
    qnaMakerProperties: QnaMakerProperties;
    loading = false;

    constructor(private route: ActivatedRoute,
                private renderer: Renderer2,
                private http: HttpClient,
                private configurationService: ConfigurationService) {
        this.qnaMakerProperties = configurationService.qnaMakerProperties;
    }

    ngOnDestroy(): void {
        if (this.routeFragmentSubscription) {
            this.routeFragmentSubscription.unsubscribe();
        }
    }

    onAnchorClick(): void {
        this.routeFragmentSubscription = this.route.fragment.subscribe(f => {
            const element = document.querySelector('#' + f);
            if (element) {
                element.scrollIntoView();
            }
        });
    }

    collapse(event: any): void {
        if (event.currentTarget.classList.contains('opened')) {
            this.renderer.removeClass(event.currentTarget, 'opened');
        } else {
            this.renderer.addClass(event.currentTarget, 'opened');
        }
    }

    makeBotRequest(event): any {
        if (event && event.keyCode !== 13) {
            return;
        }
        const questionValue = this.question ? this.question.trim() : null;
        if (isEmpty(questionValue)) {
            this.answer = '';
        } else {
            this.loading = true;
            const request = JSON.stringify({'question': questionValue});
            const httpOptions = {
                headers: new HttpHeaders({
                    'Content-Type': 'application/json',
                })
            };

            Object.keys(this.qnaMakerProperties.headers).forEach(key => {
                httpOptions.headers = httpOptions.headers.append(key, this.qnaMakerProperties.headers[key]);
            });

            this.http.post(this.qnaMakerProperties.url, request, httpOptions).subscribe((res: any) => {
                    this.answer = res.answers[0].answer;
                    this.loading = false;
                },
                error => {
                    console.log(error);
                    this.loading = false;
                });
        }
    }

    clearBotAnswer(): any {
        const questionValue = this.question ? this.question.trim() : null;
        if (isEmpty(questionValue)) {
            this.answer = '';
        }
    }
}
