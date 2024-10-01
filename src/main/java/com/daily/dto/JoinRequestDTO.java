package com.daily.dto;

import com.daily.entity.User;
import com.daily.entity.User.UserRole;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequestDTO {

	 @NotBlank(message = "로그인 아이디가 비어있습니다.")
	    private String loginId;

	    @NotBlank(message = "비밀번호가 비어있습니다.")
	    private String password;
	    private String passwordCheck;

	    @NotBlank(message = "닉네임이 비어있습니다.")
	    private String nickname;
	    
	    @NotBlank(message = "이름인 비어있습니다.")
	    private String username;;

	    // 비밀번호 암호화 X
	    public User toEntity() {
	        return User.builder()
	                .loginId(this.loginId)
	                .password(this.password)
	                .nickname(this.nickname)
	                .username(this.username)
	                .role(UserRole.USER)
	                .build();
	    }

	    // 비밀번호 암호화
	    public User toEntity(String encodedPassword) {
	        return User.builder()
	                .loginId(this.loginId)
	                .password(encodedPassword)
	                .nickname(this.nickname)
	                .username(this.username)
	                .role(UserRole.USER)
	                .build();
	    }
}
