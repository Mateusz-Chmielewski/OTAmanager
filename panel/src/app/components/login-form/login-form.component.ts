import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

import { AuthenticationService } from '../../authentication/authentication.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login-form.component.html',
  styleUrl: './login-form.component.scss'
})
export class LoginFormComponent {
  private readonly authenticationService = inject(AuthenticationService);
  private readonly router = inject(Router);
  
  loginForm: FormGroup;

  constructor() {
    console.log('LoginFormComponent initialized');
    this.loginForm = new FormGroup({
      username: new FormControl('', [Validators.required]),
      password: new FormControl('', [Validators.required])
    });
  }

  onSubmit(): void {
    if (!this.loginForm.valid) {
      console.log('Form is invalid');
      return;
    }
    
    // TODO: Implement login logic here
    this.authenticationService.login(this.loginForm.value).subscribe({
      next: (response) => {
        console.log('Login successful', response);
        this.router.navigate(['/devices']);
      },
      error: (error) => {
        console.error('Login failed', error);
      }
    });
  }
  
}
