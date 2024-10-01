package com.daily.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.daily.dto.DiaryImageDTO;
import com.daily.dto.UploadDTO;
import com.daily.entity.Diary;
import com.daily.entity.DiaryImage;
import com.daily.repository.DiaryImageRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private DiaryImageRepository diaryImageRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public FileStorageServiceImpl(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;

        // 디렉토리 유효성 확인 및 생성
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Directory created successfully at: " + uploadDir);
            } else {
                throw new RuntimeException("Failed to create upload directory: " + uploadDir);
            }
        } else {
            System.out.println("Directory already exists at: " + uploadDir);
        }
    }
    @Override
    public UploadDTO storeFile(MultipartFile file) {
        String uuid = UUID.randomUUID().toString();
        String fileName = uuid + "_" + file.getOriginalFilename();
        String folderPath = Paths.get(uploadDir, "uploads").toString();  // 실제 저장 경로
        Path filePath = Paths.get(folderPath, fileName);
        
        // 디버깅용 출력 (저장할 파일 경로 및 파일 이름)
        System.out.println("저장할 파일 경로: " + filePath.toString());
        System.out.println("저장할 파일 이름: " + fileName);

        try {
            // 폴더가 없으면 생성
            Files.createDirectories(Paths.get(folderPath));
            // 파일을 지정된 경로에 저장
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

     
            // 올바른 파일 경로를 포함하는 DTO 반환
            return new UploadDTO(fileName, uuid, "/files/uploads/" + fileName); 
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생: " + fileName, e);
        }
    }
    

    @Override
    public void saveImage(DiaryImageDTO imageDTO, Diary diary) {
        if (diary == null) {
            throw new IllegalArgumentException("Diary is null, cannot save image.");
        }

        // 디버그 메시지 추가
        System.out.println("저장할 이미지 정보: " + imageDTO.toString());
        System.out.println("연결된 Diary ID: " + diary.getDno());

        // DiaryImageDTO를 DiaryImage 엔티티로 변환
        DiaryImage diaryImage = DiaryImage.builder()
                .fileName(imageDTO.getFileName())  // 파일 이름 설정
                .filePath(imageDTO.getImgPath())   // 파일 경로 설정
                .uuid(imageDTO.getUuid())          // UUID 설정
                .diary(diary)                      // Diary와 연결
                .build();

        // 이미지 엔티티를 데이터베이스에 저장
        diaryImageRepository.save(diaryImage);

        System.out.println("이미지 저장 완료");
    }


}

