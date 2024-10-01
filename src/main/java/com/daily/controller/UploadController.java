package com.daily.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.daily.dto.DiaryImageDTO;
import com.daily.dto.UploadDTO;
import com.daily.entity.DiaryImage;
import com.daily.repository.DiaryImageRepository;
import com.daily.service.FileStorageService;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@RestController
@RequiredArgsConstructor
public class UploadController {
	
	  private static final Logger logger = LoggerFactory.getLogger(UploadController.class);
	    
	    private final FileStorageService fileStorageService;
	    private final DiaryImageRepository diaryImageRepository;


	@Value("${file.upload-dir}")
    private String uploadPath;
	
	 @GetMapping("/files/{folder}/{fileName}")
	    public ResponseEntity<byte[]> displayFile(@PathVariable("folder") String folder, @PathVariable("fileName") String fileName) {
	        try {
	            Path file = Paths.get(uploadPath + File.separator + folder + File.separator + fileName);
	            if (Files.exists(file)) {
	                HttpHeaders headers = new HttpHeaders();
	                String contentType = Files.probeContentType(file);
	                if (contentType == null) {
	                    contentType = "application/octet-stream";  // 기본 MIME 타입 설정
	                }
	                headers.add(HttpHeaders.CONTENT_TYPE, contentType);
	                return new ResponseEntity<>(Files.readAllBytes(file), headers, HttpStatus.OK);
	            } else {
	                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	            }
	        } catch (IOException e) {
	            logger.error("파일을 불러오는 중 오류 발생", e);
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

	

	@GetMapping("/display")
	public ResponseEntity<byte[]> getFile(@RequestParam("filePath") String filePath) {
	    ResponseEntity<byte[]> responseEntity = null;

	    try {
	        // URL에서 파일 경로를 디코딩하여 파일을 찾기
	        String srcFilePath = URLDecoder.decode(filePath, "UTF-8");
	        Path path = Paths.get(srcFilePath);
	        File file = path.toFile();

	        if (!file.exists()) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);  // 파일이 없으면 404 반환
	        }

	        HttpHeaders headers = new HttpHeaders();
	        headers.add("Content-Type", Files.probeContentType(file.toPath()));

	        // 파일을 바이트로 읽어서 반환
	        responseEntity = new ResponseEntity<>(Files.readAllBytes(file.toPath()), headers, HttpStatus.OK);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }

	    return responseEntity;
	}

	@PostMapping("/uploadAjax")
	public ResponseEntity<List<UploadDTO>> uploadFile(@RequestParam("files") MultipartFile[] multipartFiles) {
	    List<UploadDTO> resDTOList = new ArrayList<>();
	    List<DiaryImageDTO> imageDTOList = new ArrayList<>();

	    for (MultipartFile file : multipartFiles) {
	        String oriName = file.getOriginalFilename();
	        if (oriName == null || oriName.isEmpty()) {
	            continue; // 파일 이름이 비어있으면 처리하지 않음
	        }

	        // 파일 이름에서 경로 구분자 제거
	        String fileName = oriName.substring(oriName.lastIndexOf("\\") + 1);

	        // 이미지 파일이 아닌 경우 처리 중단
	        if (!file.getContentType().startsWith("image")) {
	            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
	        }

	        // 파일 저장 경로 설정
	        String folderPath = mkFolder();  // 폴더 경로 생성
	        String uuid = UUID.randomUUID().toString();
	        String savedName = uuid + "_" + fileName;  // UUID와 파일 이름 결합
	        Path savePath = Paths.get(uploadPath, folderPath, savedName);

	        try {
	            // 파일을 지정된 경로에 저장
	            file.transferTo(savePath);

	            // 저장된 파일의 정보를 UploadDTO에 추가
	            resDTOList.add(new UploadDTO(fileName, uuid, folderPath.replace(File.separator, "/")));
	            
	            // DiaryImageDTO 생성
	            DiaryImageDTO imageDTO = DiaryImageDTO.builder()
	                    .uuid(uuid)
	                    .imgName(fileName)
	                    .imgPath(folderPath.replace(File.separator, "/"))  // 경로 정보 설정
	                    .build();
	            imageDTOList.add(imageDTO);  // 이미지 DTO 리스트에 추가

	            // DiaryImage 엔티티 생성 후 DB에 저장
	            DiaryImage diaryImage = imageDTO.toEntity();
	            diaryImage.setFilePath(folderPath.replace(File.separator, "/") + "/" + savedName);  // 전체 파일 경로 설정
	            diaryImageRepository.save(diaryImage);  // DB에 이미지 정보 저장

	        } catch (IllegalStateException | IOException e) {
	            logger.error("파일 저장 중 오류 발생", e);
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

	    // 저장된 파일 정보를 클라이언트로 반환
	    return new ResponseEntity<>(resDTOList, HttpStatus.OK);
	}

    @PostMapping("/removeFile")
    public ResponseEntity<Boolean> delFile(@RequestParam("fileName") String fileName){
        try {
            String targetFile = URLDecoder.decode(fileName, "UTF-8");
            Path filePath = Paths.get(uploadPath, targetFile);
            File file = filePath.toFile();

            if(file.exists() && file.delete()) {
                return new ResponseEntity<>(true, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 날짜 폴더 생성 함수
    private String mkFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", File.separator);

        File uploadPathFolder = new File(uploadPath, folderPath);

        if (!uploadPathFolder.exists()) {
            boolean result = uploadPathFolder.mkdirs();
            if (result) {
                logger.info("폴더 생성됨: " + uploadPathFolder.getPath());
            } else {
                logger.error("폴더 생성 실패: " + uploadPathFolder.getPath());
            }
        }

        return folderPath;
    }
}
