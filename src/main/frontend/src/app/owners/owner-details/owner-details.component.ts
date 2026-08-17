import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { OwnerService } from '../owner.service'; // Assuming an owner service
import { OwnerDetails } from '../owner.model'; // Assuming a model for OwnerDetailsDTO

@Component({
  selector: 'app-owner-details',
  templateUrl: './owner-details.component.html',
  styleUrls: ['./owner-details.component.css']
})
export class OwnerDetailsComponent implements OnInit {
  ownerDetails: OwnerDetails | undefined;

  constructor(
    private route: ActivatedRoute,
    private ownerService: OwnerService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const ownerId = params.get('id');
      if (ownerId) {
        this.ownerService.getOwnerDetails(+ownerId).subscribe(
          data => {
            this.ownerDetails = data;
          },
          error => {
            console.error('Error fetching owner details', error);
          }
        );
      }
    });
  }
}