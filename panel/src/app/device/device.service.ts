import { HttpClient, HttpEvent, HttpEventType } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { map, Observable } from 'rxjs';

import { Device, DeviceGroup, Firmware } from './device.model';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  currentDeviceGroup = signal<DeviceGroup | null>(null);
  currentDeviceList = signal<Device[] | null>(null);
  firmwareHistory = signal<Firmware[]>([]);

  listDevices(): Observable<Device[]> {
    return this.http.get<Device[]>(this.getPath('device/list'));
  }

  listDeviceGroups(): Observable<DeviceGroup[]> {
    return this.http.get<DeviceGroup[]>(this.getPath('devicegroup/list'));
  }

  detailDevice(device: string): Observable<Device> {
    return this.http.get<Device>(this.getPath('device/' + device));
  }

  getFirmwareHistory(device: string): Observable<Firmware[]> {
    return this.http.get<Firmware[]>(this.getPath('firmware/history/' + device));
  }

  uploadFirmware(deviceGroup: string, firmware: File, version: string): Observable<number> {
    const formData = new FormData();
    formData.append('file', firmware);
    formData.append('version', version);

    return this.http.post<any>(
      this.getPath('firmware/upload/' + deviceGroup), 
      formData, 
      { reportProgress: true, observe: 'events' }
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
    this.currentDeviceGroup.set(null);
    this.currentDeviceList.set(null);
    this.firmwareHistory.set([]);
  }

  navigateToDeviceGroup(deviceGroup: DeviceGroup): void {
    this.currentDeviceGroup.set(deviceGroup);
    this.router.navigate(['device-groups', deviceGroup.id]);
  }

  private getPath(path: string): string {
    return `http://localhost:8080/${path}`;
  }
}
