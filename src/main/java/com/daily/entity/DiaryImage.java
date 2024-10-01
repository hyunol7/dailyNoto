package com.daily.entity;

import java.util.List;

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
public class DiaryImage {
	
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	    private String fileName; // 파일 이름
	    private String filePath; // 파일 경로
	    private String imgName;
	    private String uuid;
	    private List<String> photoUrl;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "diary_id")
	    private Diary diary;
	    
	    public void setDiary(Diary diary) {
	        this.diary = diary;
	    }

}
