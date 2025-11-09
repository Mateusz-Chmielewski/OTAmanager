import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

import { Device } from './device.model';

@Injectable({
  providedIn: 'root'
})
export class DeviceService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  listDevices(): Observable<Device[]> {
    return this.http.get<Device[]>(this.getPath('device/list'));
  }

  detailDevice(device: string): Observable<Device> {
    return this.http.get<Device>(this.getPath('device/' + device));
  }

  navigateToDevice(device: string): void {
    this.router.navigate(['devices', device]);
  }

  private getPath(path: string): string {
    return `http://localhost:8080/${path}`;
  }
}
