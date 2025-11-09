import { NgModule } from '@angular/core';
import { Routes } from '@angular/router';
import { LoginFormComponent } from './components/login-form/login-form.component';

export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: LoginFormComponent },
    // { path: 'dashboard', component: DashboardComponent },
    { path: '**', redirectTo: '/login' }
];
