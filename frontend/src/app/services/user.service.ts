import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserDto {
  id: number;
  name: string;
  firstName: string;
  login: string;
  email: string;
  role: string;
  dateCreation: string;
}

export interface UserRequest {
  name: string;
  firstName: string;
  login: string;
  password?: string;
  email: string;
  role: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {
  private url = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getAll(): Observable<UserDto[]> {
    return this.http.get<UserDto[]>(this.url);
  }

  create(req: UserRequest): Observable<UserDto> {
    return this.http.post<UserDto>(this.url, req);
  }

  update(id: number, req: UserRequest): Observable<UserDto> {
    return this.http.put<UserDto>(`${this.url}/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
