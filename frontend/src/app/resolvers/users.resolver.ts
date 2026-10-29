import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { UserService, UserDto } from '../services/user.service';

export const usersResolver: ResolveFn<UserDto[]> = () => {
  return inject(UserService).getAll();
};
