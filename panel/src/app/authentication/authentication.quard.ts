import { inject } from "@angular/core";
import { Router } from "@angular/router";

import { AuthenticationService } from "./authentication.service";

export const authenticationGuard = () => {
    const router = inject(Router);
    const authenticationService = inject(AuthenticationService);

    if (!authenticationService.isAuthenticated()) {
        console.log('User is not authenticated');
        router.navigate(['/login']);
        return false;
    }

    return true;
};