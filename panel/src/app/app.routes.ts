import { Routes } from '@angular/router';
import { LoginFormComponent } from './components/login-form/login-form.component';
import { authenticationGuard } from './authentication/authentication.quard';
import { DeviceGroupListComponent } from './components/device-group-list/device-group-list.component';
import { DeviceGroupDetailComponent } from './components/device-group-detail/device-group-detail.component';

export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: LoginFormComponent, pathMatch: 'full' },
    { path: 'device-groups', component: DeviceGroupListComponent, canActivate: [authenticationGuard], },
    { path: 'device-groups/:id', component: DeviceGroupDetailComponent, canActivate: [authenticationGuard] },
    { path: '**', redirectTo: '/login' }
];
