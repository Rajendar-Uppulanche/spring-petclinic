import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OwnerDetails } from './owner.model'; // Assuming OwnerDetails interface

@Injectable({
  providedIn: 'root'
})
export class OwnerService {
  private apiUrl = '/api/owners'; // Base URL for owner API

  constructor(private http: HttpClient) { }

  getOwnerDetails(id: number): Observable<OwnerDetails> {
    return this.http.get<OwnerDetails>(`${this.apiUrl}/${id}`);
  }

  // Other owner related methods...
}