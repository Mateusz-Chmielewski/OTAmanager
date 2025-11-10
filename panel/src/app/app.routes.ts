import { NgModule } from '@angular/core';
import { Routes } from '@angular/router';
import { LoginFormComponent } from './components/login-form/login-form.component';
import { DeviceListComponent } from './components/device-list/device-list.component';
import { authenticationGuard } from './authentication/authentication.quard';
import { DetailDeviceComponent } from './components/detail-device/detail-device.component';

export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: LoginFormComponent, pathMatch: 'full' },
    { path: 'devices', component: DeviceListComponent, canActivate: [authenticationGuard], children: [
        
    ] },
    { path: 'devices/:id', component: DetailDeviceComponent, canActivate: [authenticationGuard] },
    { path: '**', redirectTo: '/login' }
];
