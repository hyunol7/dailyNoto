package com.daily.service;

import com.daily.dto.JoinRequestDTO;
import com.daily.dto.LoginRequestDto;
import com.daily.dto.UserRegistrationDto;
import com.daily.entity.User;

public interface UserService {
	 boolean checkLoginIdDuplicate(String loginId);
	    boolean checkNicknameDuplicate(String nickname);
	    void join(JoinRequestDTO req);
	    void join2(JoinRequestDTO req);
	    User login(LoginRequestDto req);
	    User getLoginUserById(Long userId);
	    User getLoginUserByLoginId(String loginId);
		void deleteUser(Long userId);
		void updateUser(Long userId, UserRegistrationDto dto);
}
