package com.daily.dto;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadDTO {
	
	  private String fileName;    // 파일 이름
	    private String uuid;        // UUID
	    private String folderPath;  // 파일이 저장된 폴더 경로

	    // 이미지의 전체 URL을 반환하는 메서드
	    public String getImageURL() {
	        try {
	            // 브라우저에서 접근 가능한 URL을 생성 (경로 인코딩)
	            return URLEncoder.encode("/files/" + folderPath + "/" + uuid + "_" + fileName, StandardCharsets.UTF_8.toString());
	        } catch (Exception e) {
	            throw new RuntimeException("URL 인코딩 중 오류 발생", e);
	        }
	    }

}
