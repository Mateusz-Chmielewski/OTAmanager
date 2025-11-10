import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';

import { Device } from '../../device/device.model';
import { DeviceService } from '../../device/device.service';

@Component({
  selector: 'app-device-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './device-list.component.html',
  styleUrl: './device-list.component.scss'
})
export class DeviceListComponent implements OnInit {
  readonly deviceService = inject(DeviceService);

  readonly devices = signal<Device[]>([]);

  constructor() {
    console.log('DeviceListComponent initialized');
  } 
  
  ngOnInit(): void {
    this.deviceService.listDevices().subscribe({
      next: (response) => {
        console.log('Login successful', response);
        this.devices.set(response);
      },
      error: (error) => {
        console.error('Login failed', error);
      }
    })
  }

  onDetailDevice(deviceId: string): void {
    this.deviceService.navigateToDevice(deviceId);
  }

}
