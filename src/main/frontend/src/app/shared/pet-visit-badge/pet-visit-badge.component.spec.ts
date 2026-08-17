import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PetVisitBadgeComponent } from './pet-visit-badge.component';
import { By } from '@angular/platform-browser';

describe('PetVisitBadgeComponent', () => {
  let component: PetVisitBadgeComponent;
  let fixture: ComponentFixture<PetVisitBadgeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PetVisitBadgeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PetVisitBadgeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // Initial change detection
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display "No visits" for null visitCount (FR-085)', () => {
    component.visitCount = null;
    fixture.detectChanges();
    const badgeElement = fixture.debugElement.query(By.css('.pet-visit-badge')).nativeElement;
    expect(badgeElement.textContent.trim()).toBe('No visits');
    expect(badgeElement).toHaveClass('no-visits'); // NFR-078
  });

  it('should display "No visits" for undefined visitCount (FR-085)', () => {
    component.visitCount = undefined;
    fixture.detectChanges();
    const badgeElement = fixture.debugElement.query(By.css('.pet-visit-badge')).nativeElement;
    expect(badgeElement.textContent.trim()).toBe('No visits');
    expect(badgeElement).toHaveClass('no-visits'); // NFR-078
  });

  it('should display "No visits" for 0 visitCount (FR-085)', () => {
    component.visitCount = 0;
    fixture.detectChanges();
    const badgeElement = fixture.debugElement.query(By.css('.pet-visit-badge')).nativeElement;
    expect(badgeElement.textContent.trim()).toBe('No visits');
    expect(badgeElement).toHaveClass('no-visits'); // NFR-078
  });

  it('should display "1 visit" for 1 visitCount (FR-084)', () => {
    component.visitCount = 1;
    fixture.detectChanges();
    const badgeElement = fixture.debugElement.query(By.css('.pet-visit-badge')).nativeElement;
    expect(badgeElement.textContent.trim()).toBe('1 visit');
    expect(badgeElement).toHaveClass('one-visit'); // NFR-078
  });

  it('should display "N visits" for N > 1 visitCount (FR-084)', () => {
    component.visitCount = 5;
    fixture.detectChanges();
    const badgeElement = fixture.debugElement.query(By.css('.pet-visit-badge')).nativeElement;
    expect(badgeElement.textContent.trim()).toBe('5 visits');
    expect(badgeElement).toHaveClass('multiple-visits'); // NFR-078
  });

  it('should update badge text and class when visitCount changes', () => {
    component.visitCount = 1;
    fixture.detectChanges();
    let badgeElement = fixture.debugElement.query(By.css('.pet-visit-badge')).nativeElement;
    expect(badgeElement.textContent.trim()).toBe('1 visit');
    expect(badgeElement).toHaveClass('one-visit');

    component.visitCount = 10;
    fixture.detectChanges();
    badgeElement = fixture.debugElement.query(By.css('.pet-visit-badge')).nativeElement;
    expect(badgeElement.textContent.trim()).toBe('10 visits');
    expect(badgeElement).toHaveClass('multiple-visits');

    component.visitCount = 0;
    fixture.detectChanges();
    badgeElement = fixture.debugElement.query(By.css('.pet-visit-badge')).nativeElement;
    expect(badgeElement.textContent.trim()).toBe('No visits');
    expect(badgeElement).toHaveClass('no-visits');
  });
});