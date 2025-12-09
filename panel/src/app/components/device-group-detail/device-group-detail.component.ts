import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatListModule } from '@angular/material/list';
import { MatTooltipModule } from '@angular/material/tooltip';
import { DeviceService } from '../../device/device.service';
import { Device } from '../../device/device.model';

@Component({
  selector: 'app-device-group-detail',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatTableModule,
    MatChipsModule,
    MatProgressBarModule,
    MatSnackBarModule,
    MatDividerModule,
    MatListModule,
    MatTooltipModule
  ],
  templateUrl: './device-group-detail.component.html',
  styleUrl: './device-group-detail.component.scss'
})
export class DeviceGroupDetailComponent {
  private readonly deviceService = inject(DeviceService);
  private readonly route = inject(ActivatedRoute);
  private readonly snackbar = inject(MatSnackBar);

  uploadFirmwareForm: FormGroup;

  deviceGroup = computed(() => this.deviceService.currentDeviceGroup());
  currentDevices = signal<Device[]>([]);
  firmwareHistory = computed(() => this.deviceService.firmwareHistory());
  selectedFile = signal<File | null>(null);
  uploadProgress = signal<number>(0);
  isUploading = signal<boolean>(false);

  displayedColumns: string[] = ['version', 'description', 'uploadedAt', 'status'];
  deviceColumns: string[] = ['name', 'description', 'id', 'actions'];
  
  constructor() {
    this.uploadFirmwareForm = new FormGroup({
      firmwareFile: new FormControl(null, [Validators.required]),
      firmwareVersion: new FormControl('', [Validators.required])
    });
    }    
    ngOnInit(): void {
      const groupId = this.route.snapshot.paramMap.get('id');
      if (groupId) {
        this.loadDeviceDetails(groupId);
      }
    }
  
  private loadDeviceDetails(groupId: string): void {
    this.deviceService.getFirmwareHistory(groupId).subscribe({
      next: (firmwareList) => {
        this.deviceService.firmwareHistory.set(firmwareList);
      },
      error: (error) => {
        console.error('Error loading firmware history', error);
        this.snackbar.open('Failed to load firmware history', 'Close', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      }
    });

    this.deviceService.listDevices().subscribe({
      next: (devices) => {
        this.currentDevices.set(devices.filter(d => d.groupId === groupId));
      },
      error: (error) => {
        console.error('Error loading devices in group', error);
        this.snackbar.open('Failed to load devices in group', 'Close', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onFileSelected(event: any): void {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile.set(file);
      this.uploadFirmwareForm.patchValue({ firmwareFile: file });
    }
  }

  onUploadFirmware(): void {
    if (!this.uploadFirmwareForm.valid) {
      this.snackbar.open('Please fill in all required fields', 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'top',
        panelClass: ['error-snackbar']
      });
      return;
    }


    const file = this.selectedFile();
    const deviceGroupId = this.deviceGroup()?.id;
    const version = this.uploadFirmwareForm.get('firmwareVersion')?.value ?? '';

    console.log('Uploading firmware for group:', deviceGroupId, this.deviceGroup(), 'File:', file);

    if (file && deviceGroupId) {
      this.isUploading.set(true);
      this.deviceService.uploadFirmware(deviceGroupId, file, version).subscribe({
        next: (progress) => {
          console.log('Upload progress:', progress);
          this.uploadProgress.set(progress);
          if (progress === 100) {
            this.onSuccessfulUpload(deviceGroupId);
          }
        },
        error: (error) => {
          this.onErrorUploading(error);
        }
      });
    }
  }

  onSuccessfulUpload(deviceGroupId: string): void {
    this.isUploading.set(false);
    this.snackbar.open('Firmware uploaded successfully!', 'Close', {
      duration: 3000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['success-snackbar']
    });
    this.resetForm();
    this.loadDeviceDetails(deviceGroupId);
  }

  onErrorUploading(error: any): void {
    console.error('Error uploading firmware:', error);
    this.isUploading.set(false);
    this.uploadProgress.set(0);
    this.snackbar.open('Failed to upload firmware', 'Close', {
      duration: 4000,
      horizontalPosition: 'center',
      verticalPosition: 'top',
      panelClass: ['error-snackbar']
    });
  }

  resetForm(): void {
    this.uploadFirmwareForm.reset();
    this.selectedFile.set(null);
    this.uploadProgress.set(0);
  }

  onViewDevice(deviceId: string): void {
    // Navigate to device detail page
    console.log('View device:', deviceId);
    // this.router.navigate(['/devices', deviceId]);
  }
    
  onBack(): void {
    window.history.back();
  }
  
    ngOnDestroy(): void {
      this.deviceService.clearCurrentInfo();
    }

}
