package com.daily.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.daily.dto.DiaryDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dno;
    private String title;
    private String content;
    private LocalDate date;
    private LocalDateTime modDate;
    private String nickname;
    private String mood;
    private String loginId;
    
    
   
    private List<String> photoUrl = new ArrayList<>(); // Initialize with an empty list

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 이미지들과의 관계 설정
    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryImage> imageList = new ArrayList<>();

    public void addPhoto(DiaryImage image) {
        imageList.add(image);
        image.setDiary(this);  // 양방향 연관 관계 설정
    }

    
    @OneToMany(mappedBy = "diary", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    private List<Comment> comment = new ArrayList<Comment>();

    public List<DiaryImage> getComment() {
        return imageList != null ? imageList : new ArrayList<>();
    }

    
    
    public void removePhoto(DiaryImage image) {
        imageList.remove(image);
        image.setDiary(null); // DiaryImage 엔티티에서 Diary 제거
    }
    
    public int getReplyCount() {
        return getComment().size(); // Use getComment() to avoid NullPointerException
    }

    public String getUserNickname() {
        if (this.user != null) {
            return this.user.getNickname();
        } else {
            return "Unknown User"; // 사용자가 없는 경우
        }
    }
    
    public DiaryDTO toDTO() {
        DiaryDTO dto = new DiaryDTO();
        dto.setDno(this.dno);
        dto.setTitle(this.title);
        // 기타 필드들을 dto에 설정
        return dto;
    }

	public void setPhotoUrl(String string) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	 public void addImage(DiaryImage image) {
	        imageList.add(image);
	        image.setDiary(this);  // 양방향 연관 관계 설정
	    }
	
	
}
