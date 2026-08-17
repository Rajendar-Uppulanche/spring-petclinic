import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { OwnerDetailsComponent } from './owner-details.component';
import { OwnerService } from '../owner.service';
import { PetVisitBadgeComponent } from '../../shared/pet-visit-badge/pet-visit-badge.component'; // Adjust path as needed
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { By } from '@angular/platform-browser';

describe('OwnerDetailsComponent', () => {
  let component: OwnerDetailsComponent;
  let fixture: ComponentFixture<OwnerDetailsComponent>;
  let ownerServiceSpy: jasmine.SpyObj<OwnerService>;

  const mockOwnerDetails = {
    id: 1,
    firstName: 'John',
    lastName: 'Doe',
    address: '123 Main St',
    city: 'Anytown',
    telephone: '555-1234',
    pets: [
      { id: 10, name: 'Buddy', birthDate: '2020-01-01', typeName: 'dog', ownerId: 1, visitCount: 3 },
      { id: 11, name: 'Lucy', birthDate: '2021-05-10', typeName: 'cat', ownerId: 1, visitCount: 1 },
      { id: 12, name: 'Max', birthDate: '2019-03-15', typeName: 'dog', ownerId: 1, visitCount: 0 }
    ]
  };

  beforeEach(async () => {
    ownerServiceSpy = jasmine.createSpyObj('OwnerService', ['getOwnerDetails']);
    ownerServiceSpy.getOwnerDetails.and.returnValue(of(mockOwnerDetails));

    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule], // For OwnerService
      declarations: [
        OwnerDetailsComponent,
        PetVisitBadgeComponent // Declare the badge component
      ],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of({ get: (key: string) => '1' }) // Mock route param 'id'
          }
        },
        { provide: OwnerService, useValue: ownerServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OwnerDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges(); // Trigger ngOnInit and data fetching
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display owner details', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.querySelector('h2').textContent).toContain('Owner Details');
    expect(compiled.textContent).toContain('John Doe');
    expect(compiled.textContent).toContain('123 Main St');
    expect(compiled.textContent).toContain('Anytown');
    expect(compiled.textContent).toContain('555-1234');
  });

  it('should display pet list with visit badges (FR-083, NFR-079)', () => {
    const compiled = fixture.nativeElement;
    const petCards = compiled.querySelectorAll('.pet-card');
    expect(petCards.length).toBe(3);

    // Check Buddy's badge
    const buddyCard = petCards[0];
    expect(buddyCard.querySelector('h4').textContent).toContain('Buddy');
    const buddyBadge = buddyCard.querySelector('app-pet-visit-badge');
    expect(buddyBadge).toBeTruthy();
    expect(buddyBadge.textContent).toContain('3 visits'); // FR-084
    expect(buddyBadge.querySelector('.multiple-visits')).toBeTruthy(); // NFR-078

    // Check Lucy's badge
    const lucyCard = petCards[1];
    expect(lucyCard.querySelector('h4').textContent).toContain('Lucy');
    const lucyBadge = lucyCard.querySelector('app-pet-visit-badge');
    expect(lucyBadge).toBeTruthy();
    expect(lucyBadge.textContent).toContain('1 visit'); // FR-084
    expect(lucyBadge.querySelector('.one-visit')).toBeTruthy(); // NFR-078

    // Check Max's badge
    const maxCard = petCards[2];
    expect(maxCard.querySelector('h4').textContent).toContain('Max');
    const maxBadge = maxCard.querySelector('app-pet-visit-badge');
    expect(maxBadge).toBeTruthy();
    expect(maxBadge.textContent).toContain('No visits'); // FR-085
    expect(maxBadge.querySelector('.no-visits')).toBeTruthy(); // NFR-078
  });

  it('should call ownerService.getOwnerDetails with correct ID', () => {
    expect(ownerServiceSpy.getOwnerDetails).toHaveBeenCalledWith(1);
  });

  it('should handle no pets gracefully', () => {
    ownerServiceSpy.getOwnerDetails.and.returnValue(of({ ...mockOwnerDetails, pets: [] }));
    fixture.detectChanges(); // Re-render with no pets
    const compiled = fixture.nativeElement;
    expect(compiled.textContent).toContain('No pets registered for this owner.');
    expect(compiled.querySelectorAll('.pet-card').length).toBe(0);
  });
});