export interface Pet {
  id: number;
  name: string;
  birthDate: string; // Assuming ISO date string
  typeName: string;
  ownerId: number;
  visitCount: number; // New field
}

export interface OwnerDetails {
  id: number;
  firstName: string;
  lastName: string;
  address: string;
  city: string;
  telephone: string;
  pets: Pet[];
}