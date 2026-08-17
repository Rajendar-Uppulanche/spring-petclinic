import { Component, Input, OnInit, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-pet-visit-badge',
  templateUrl: './pet-visit-badge.component.html',
  styleUrls: ['./pet-visit-badge.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PetVisitBadgeComponent implements OnInit {
  @Input() visitCount: number | null | undefined;

  badgeText: string = '';
  badgeClass: string = '';

  ngOnInit(): void {
    this.updateBadge();
  }

  ngOnChanges(): void {
    this.updateBadge();
  }

  private updateBadge(): void {
    if (this.visitCount === null || this.visitCount === undefined || this.visitCount <= 0) {
      this.badgeText = 'No visits';
      this.badgeClass = 'no-visits';
    } else if (this.visitCount === 1) {
      this.badgeText = '1 visit';
      this.badgeClass = 'one-visit';
    } else {
      this.badgeText = `${this.visitCount} visits`;
      this.badgeClass = 'multiple-visits';
    }
  }
}