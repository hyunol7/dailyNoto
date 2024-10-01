package com.daily.dto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.daily.entity.DiaryImage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DiaryImageDTO 클래스는 일기에 첨부된 이미지의 데이터 전송 객체입니다.
 * 이 클래스는 이미지 파일의 이름, 저장 경로 및 고유 식별자(UUID)를 관리합니다.
 * 또한, 이 정보를 사용하여 웹에서 접근 가능한 URL을 생성합니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class DiaryImageDTO {

	 private Long id;                // 이미지의 데이터베이스 ID
	    private String fileName;        // 파일 이름
	    private String imgName;         // 추가된 필드: 이미지 이름
	    private String imgPath;         // 이미지 파일이 저장된 서버상의 경로
	    private String uuid;            // 이미지 파일의 고유 식별자(UUID)
	    private List<String> photoUrl;  // 이미지 URL 리스트
	    private List<DiaryImageDTO> imageList;  // 이미지 DTO 리스트

	    // 이미지의 전체 URL을 반환
	    public String getImageURL() {
	        return URLEncoder.encode(imgPath + "/" + uuid + "_" + fileName, StandardCharsets.UTF_8);
	    }


	    public static DiaryImageDTO fromEntity(DiaryImage diaryImage) {
	        return DiaryImageDTO.builder()
	                .id(diaryImage.getId())
	                .fileName(diaryImage.getFileName())
	                .imgPath(diaryImage.getFilePath())
	                .uuid(diaryImage.getUuid())
	                .build();
	    }

	    
	    public DiaryImageDTO(Long id, String fileName, String imgName, String filePath, String uuid, List<String> photoUrls, String imgPath, List<String> photoUrl) {
	        this.id = id;
	        this.fileName = fileName;
	        this.imgName = imgName;
	        this.imgPath =  imgPath;
	        this.uuid = uuid;
	        this.photoUrl = photoUrl;
	    }
	    
	    public DiaryImage toEntity() {
	        return DiaryImage.builder()
	                .fileName(this.fileName)
	                .imgName(this.imgName)
	                .filePath(this.imgPath)
	                .uuid(this.uuid)
	                .build();
	    }


}
