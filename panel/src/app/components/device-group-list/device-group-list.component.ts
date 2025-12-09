import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { DeviceService } from '../../device/device.service';
import { DeviceGroup } from '../../device/device.model';

@Component({
  selector: 'app-device-group-list',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatProgressSpinnerModule,
    MatSnackBarModule
  ],
  templateUrl: './device-group-list.component.html',
  styleUrl: './device-group-list.component.scss'
})
export class DeviceGroupListComponent {
  private readonly deviceService = inject(DeviceService);
  private readonly router = inject(Router);
  private readonly snackbar = inject(MatSnackBar);

  deviceGroups = signal<DeviceGroup[]>([]);
  isLoading = signal<boolean>(true);

  ngOnInit(): void {
    this.loadDeviceGroups();
  }

  loadDeviceGroups(): void {
    this.isLoading.set(true);
    this.deviceService.listDeviceGroups().subscribe({
      next: (response) => {
        console.log('Device groups loaded', response);
        this.deviceGroups.set(response);
        this.isLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load device groups', error);
        this.isLoading.set(false);
        this.snackbar.open('Failed to load device groups', 'Close', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onDetailDeviceGroup(group: DeviceGroup): void {
    this.deviceService.navigateToDeviceGroup(group);
  }
}
