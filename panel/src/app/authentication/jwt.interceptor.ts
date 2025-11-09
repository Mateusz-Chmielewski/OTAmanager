import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";

import { AuthenticationService } from "./authentication.service";

export const jwtInterceptor: HttpInterceptorFn = (httpRequest, next) => {
    const authenticationService = inject(AuthenticationService);
    const token = authenticationService.getToken();

    if (token) {
        httpRequest = httpRequest.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        });
    }

    return next(httpRequest);
};