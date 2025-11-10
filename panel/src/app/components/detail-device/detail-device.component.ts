import { Component, computed, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { DeviceService } from '../../device/device.service';
import { ActivatedRoute } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-detail-device',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './detail-device.component.html',
  styleUrl: './detail-device.component.scss'
})
export class DetailDeviceComponent implements OnInit, OnDestroy {
  private readonly deviceService = inject(DeviceService);
  private readonly route = inject(ActivatedRoute);

  uploadFirmwareForm: FormGroup;

  device = computed(() => this.deviceService.currentDevice());
  firmwareHistory = computed(() => this.deviceService.firmwareHistory());
  selectedFile = signal<File | null>(null);

  constructor() {
    this.uploadFirmwareForm = new FormGroup({
      firmwareFile: new FormControl(null),
      firmwareVersion: new FormControl('')
    });
  }

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

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    this.selectedFile.set(file);
  }

  onUploadFirmware(): void {
    const file = this.selectedFile();
    const deviceId = this.device()?.id;
    const version = this.uploadFirmwareForm.get('firmwareVersion')?.value;

    console.log('Uploading firmware for device:', deviceId, 'File:', file);

    if (file && deviceId) {
      this.deviceService.uploadFirmware(deviceId, file, version ?? "").subscribe({
        next: (progress) => {
          console.log('Upload progress:', progress);
          if (progress === 100) {
            this.loadDeviceDetails(deviceId);
          }
        },
        error: (error) => {
          console.error('Error uploading firmware:', error);
        },
        complete: () => {
          this.loadDeviceDetails(deviceId);
        }
      });
    }
  }

  onBack(): void {
    window.history.back();
  }

  ngOnDestroy(): void {
    this.deviceService.clearCurrentInfo();
  }
}
