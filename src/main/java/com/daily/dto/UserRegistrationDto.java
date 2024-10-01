package com.daily.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class  UserRegistrationDto {
	private String name;
    private String nickname;
    private String newNickname;
	public Object getPassword() {
		// TODO Auto-generated method stub
		return null;
	}
	
	   public UserRegistrationDto(String username, String nickname) {
	        this.name = username;
	        this.nickname = nickname;
	    }
}
