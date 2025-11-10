import { Component, computed, inject, OnDestroy, OnInit } from '@angular/core';
import { DeviceService } from '../../device/device.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-detail-device',
  standalone: true,
  imports: [],
  templateUrl: './detail-device.component.html',
  styleUrl: './detail-device.component.scss'
})
export class DetailDeviceComponent implements OnInit, OnDestroy {
  private readonly deviceService = inject(DeviceService);
  private readonly route = inject(ActivatedRoute);

  device = computed(() => this.deviceService.currentDevice());
  firmwareHistory = computed(() => this.deviceService.firmwareHistory());

  ngOnInit(): void {
    const deviceId = this.route.snapshot.paramMap.get('id');
    if (deviceId) {
      this.loadDeviceDetails(deviceId);
    }
  }

  private loadDeviceDetails(deviceId: string): void {
    this.deviceService.detailDevice(deviceId).subscribe({
      next: (device) => {
        this.deviceService.currentDevice.set(device);
      },
      error: (error) => {
        console.error('Error loading device details', error);
      }
    });

    this.deviceService.getFirmwareHistory(deviceId).subscribe({
      next: (firmwareList) => {
        this.deviceService.firmwareHistory.set(firmwareList);
      },
      error: (error) => {
        console.error('Error loading firmware history', error);
      }
    });
  }

  onBack(): void {
    window.history.back();
  }

  ngOnDestroy(): void {
    this.deviceService.clearCurrentInfo();
  }
}
