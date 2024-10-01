package com.daily.entity;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class  User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String loginId;
	private String password;
	private String nickname;
	private String username;
	private UserRole role;
	
	public enum UserRole {
	    USER, ADMIN;
	}
	
	   // equals 및 hashCode 추가
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);  // ID로 비교
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
	
	 public Long getId() {
	        return id;
	    }

	 public void setUsername(String username) {
		 this.username = username;
	 }
	 
	 public String getUsername() {
		 return username;
	 }
	 
	    public void setId(Long id) {
	        this.id = id;
	    }

	    public String getLoginId() {
	        return loginId;
	    }

	    public void setLoginId(String loginId) {
	        this.loginId = loginId;
	    }

	    public String getPassword() {
	        return password;
	    }

	    public void setPassword(String password) {
	        this.password = password;
	    }

	    public String getNickname() {
	        return nickname;
	    }

	    public void setNickname(String nickname) {
	        this.nickname = nickname;
	    }
	

}
