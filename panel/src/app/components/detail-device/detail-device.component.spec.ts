import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailDeviceComponent } from './detail-device.component';

describe('DetailDeviceComponent', () => {
  let component: DetailDeviceComponent;
  let fixture: ComponentFixture<DetailDeviceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailDeviceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DetailDeviceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
