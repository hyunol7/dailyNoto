package com.daily.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.daily.dto.CommentDTO;
import com.daily.dto.DiaryDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cno;
    private String content;
    private LocalDateTime modDate;
    private LocalDate date;
    private String nickname;
 

    public String getUserLoginId() {
        if (this.user != null) {
            return this.user.getLoginId();  // User 엔티티에 loginId가 있다고 가정
        } else {
            return "Unknown User";
        }
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public String getUserNickname() {
        if (this.user != null) {
            return this.user.getNickname();
        } else {
            return "Unknown User"; // 사용자가 없는 경우
        }
    }
    public void setComments(String content) {
        this.content = content;
    }


	public void update(String content) {
		 this.content = content;
		
	}
	public LocalDateTime getCreatedDate() {
		// TODO Auto-generated method stub
		return null;
	}

}
