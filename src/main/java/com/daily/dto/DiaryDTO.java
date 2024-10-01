package com.daily.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.daily.entity.Diary;
import com.daily.entity.DiaryImage;
import com.daily.entity.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DiaryDTO {

    private Long dno;
    private String title;
    private String content;
    private String nickname; // 사용자 닉네임 필드 추가
    private String mood;
    private List<String> photoUrl;
    private LocalDate date;
    private LocalDateTime modDate;
    private int replyCount;
    private List<DiaryImageDTO> imageList; 
    private String loginId;

    public Diary toEntity(User user) {
        Diary diary = Diary.builder()
            .dno(this.dno)
            .title(this.title)
            .content(this.content)
            .mood(this.mood)
            .user(user)
            .date(this.date != null ? this.date : LocalDate.now())  // Null 안전성
            .modDate(this.modDate != null ? this.modDate : LocalDateTime.now())  // Null 안전성
            .photoUrl(this.photoUrl != null ? new ArrayList<>(this.photoUrl) : new ArrayList<>())  // Null 안전성
            .build();

        // 이미지 리스트 추가
        if (this.imageList != null) {
            List<DiaryImage> imageEntities = this.imageList.stream()
                .map(DiaryImageDTO::toEntity)  // DiaryImageDTO에서 DiaryImage로 변환
                .collect(Collectors.toList());
            imageEntities.forEach(diary::addImage);  // Diary 엔티티에 이미지 추가
        }

        return diary;
    }
   
    
    public DiaryDTO(Long dno, String title, String content, String nickname, String loginId, String mood,
            List<String> photoUrl, LocalDate date, LocalDateTime modDate, int replyCount, List<DiaryImageDTO> imageList) {
this.dno = dno;
this.title = title;
this.content = content;
this.nickname = nickname;
this.loginId = loginId;
this.mood = mood;
this.photoUrl = photoUrl != null ? new ArrayList<>(photoUrl) : new ArrayList<>();  // Null safety check for photoUrl
this.date = date;
this.modDate = modDate;
this.replyCount = replyCount;
this.imageList = imageList != null ? new ArrayList<>(imageList) : new ArrayList<>();  // Null safety check for imageList
}

 
    
	public void setUser(Long long1) {
		// TODO Auto-generated method stub
		
	}

	public DiaryDTO(Diary diary) {
		// TODO Auto-generated constructor stub
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
	    
	    
	    public String getLoginId() {
	        return loginId;
	    }

	    public void setLoginId(String loginId) {
	        this.loginId = loginId;
	    }
	public Object getUserId() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	public static DiaryDTO fromDiary(Diary diary) {
		// TODO Auto-generated method stub
		return null;
	}

	   public static DiaryDTO fromEntity(Diary diary) {
	        if (diary == null) {
	            System.out.println("fromEntity 메소드에 전달된 Diary 객체가 null입니다.");
	            return null;
	        }

	        List<DiaryImageDTO> imageDTOList = diary.getImageList() != null ? diary.getImageList().stream()
	            .map(DiaryImageDTO::fromEntity)  // DiaryImage에서 DiaryImageDTO로 변환
	            .collect(Collectors.toList()) : new ArrayList<>();

	        String nickname = diary.getUser() != null ? diary.getUser().getNickname() : "Unknown";
	        String loginId = diary.getUser() != null ? diary.getUser().getLoginId() : "Unknown";

	        return DiaryDTO.builder()
	            .dno(diary.getDno())
	            .title(diary.getTitle())
	            .content(diary.getContent())
	            .nickname(nickname)
	            .loginId(loginId)
	            .mood(diary.getMood())
	            .photoUrl(diary.getPhotoUrl() != null ? new ArrayList<>(diary.getPhotoUrl()) : new ArrayList<>())
	            .date(diary.getDate())
	            .modDate(diary.getModDate())
	            .replyCount(diary.getReplyCount())
	            .imageList(imageDTOList)
	            .build();
	    }





	   // Setter for image list
	    public void setImageList(List<DiaryImageDTO> imageList) {
	        this.imageList = imageList != null ? new ArrayList<>(imageList) : new ArrayList<>();  // Null safety for image list
	    }

	    // Get the first photo URL (optional)
	    public String getFirstPhotoUrl() {
	        return this.photoUrl != null && !this.photoUrl.isEmpty() ? this.photoUrl.get(0) : null;
	    }

	    // Photo URL 리스트 설정 메서드
	 // Photo URL 리스트 설정 메서드
	    public void setPhotoUrl(List<String> photoUrl) {
	        this.photoUrl = photoUrl != null ? new ArrayList<>(photoUrl) : new ArrayList<>();
	    }


	   

	    // 이미지 리스트 getter
	    public List<DiaryImageDTO> getImageList() {
	        return imageList;
	    }


	
}
