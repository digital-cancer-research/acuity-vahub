import {
    HttpEvent,
    HttpInterceptor,
    HttpHandler,
    HttpRequest,
} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';


export class AddDefaultHeadersInterceptor implements HttpInterceptor {
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // Clone the request to add the new header
        const clonedRequest = req.clone({
            headers: req.headers.set('Content-Type', 'application/json;charset=UTF-8')
                .set('X-Requested-With', 'XMLHttpRequest')
        });

        // Pass the cloned request instead of the original request to the next handle
        return next.handle(clonedRequest);
    }
}
