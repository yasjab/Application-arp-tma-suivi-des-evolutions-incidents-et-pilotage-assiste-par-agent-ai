import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { ProfileService } from '../services/profile.service';
import { UserDto } from '../services/user.service';

export const profileResolver: ResolveFn<UserDto> = () => {
  return inject(ProfileService).getProfile();
};
