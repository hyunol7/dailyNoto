package com.daily.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.daily.entity.Comment;
import com.daily.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

	private Long cno;
	private String content;
	
	 private LocalDateTime modDate;
	    private LocalDate date;
    private User user;
    private String nickname;
    private Long diaryId;  // Add this field
	private String loginId;




 
 public Comment toEntity(Long long1, String string ) {
	 return Comment.builder()
			 .content(content)
			 .date(date)
			 .modDate(modDate != null ? modDate : LocalDateTime.now())
			 .build();
			 
 }
	
 public CommentDTO(Comment comment) {
	    this.cno = comment.getCno();
	    this.content = comment.getContent();
	    this.modDate = comment.getModDate();
	    this.date = comment.getDate();
	    this.nickname = comment.getNickname();  
	    this.loginId = comment.getUserLoginId();  // Ensure this is correctly assigned
	}


	public Long getDno() {
		// TODO Auto-generated method stub
		return null;
	}

	 public static CommentDTO fromEntity(Comment comment) {
	        if (comment == null) {
	            return null;
	        }
	        return CommentDTO.builder()
	                .cno(comment.getCno())
	                .content(comment.getContent())
	                .modDate(comment.getModDate())
	                .date(comment.getDate())
	                .user(comment.getUser())
	                .nickname(comment.getNickname()) // Assuming Comment has a nickname field.
	                .loginId(comment.getUser() != null ? comment.getUser().getLoginId() : "Unknown")  // login_id 추가
	                .build();
	    }
	 
	 

	public static CommentDTO fromComment(Comment comment) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setUser(Long long1) {
		// TODO Auto-generated method stub
		
	}
	public void setUserId(Long id) {
		// TODO Auto-generated method stub
		
	}
	   public String getNickname() {
	        return nickname;
	    }

	    public void setNickname(String nickname) {
	        this.nickname = nickname;
	    }
	public Object getUserId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	  public void setLoginId(String loginId) {
	        this.loginId = loginId;
	    }
	public Object getLoginId() {
		// TODO Auto-generated method stub
		return loginId;
	}
	
	
}
