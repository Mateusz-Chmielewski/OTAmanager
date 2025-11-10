import { HttpClient, HttpEvent, HttpEventType } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { map, Observable } from 'rxjs';

import { Device, Firmware } from './device.model';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  currentDevice = signal<Device | null>(null);
  firmwareHistory = signal<Firmware[]>([]);

  listDevices(): Observable<Device[]> {
    return this.http.get<Device[]>(this.getPath('device/list'));
  }

  detailDevice(device: string): Observable<Device> {
    return this.http.get<Device>(this.getPath('device/' + device));
  }

  getFirmwareHistory(device: string): Observable<Firmware[]> {
    return this.http.get<Firmware[]>(this.getPath('firmware/history/' + device));
  }

  uploadFirmware(device: string, firmware: File): Observable<number> {
    const formData = new FormData();
    formData.append('file', firmware); 
    // formData.append('deviceId', device);

    return this.http.post<any>(
      this.getPath('firmware/upload/' + device), 
      formData
    ).pipe(
      map((event: HttpEvent<any>) => {
        switch (event.type) {
          case HttpEventType.UploadProgress:
            if (!event.total) {
              return 0;
            }
            return Math.round((100 * event.loaded) / event.total);
          case HttpEventType.Response:
            return 100;
          default:
            return 0;
        }
      })
    );
  } 

  clearCurrentInfo(): void {
    this.currentDevice.set(null);
    this.firmwareHistory.set([]);
  }

  navigateToDevice(device: string): void {
    this.router.navigate(['devices', device]);
  }

  private getPath(path: string): string {
    return `http://localhost:8080/${path}`;
  }
}
