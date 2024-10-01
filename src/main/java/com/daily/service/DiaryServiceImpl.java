package com.daily.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.daily.dto.DiaryDTO;
import com.daily.dto.DiaryImageDTO;
import com.daily.dto.UploadDTO;
import com.daily.entity.Diary;
import com.daily.entity.DiaryImage;
import com.daily.entity.User;
import com.daily.repository.DiaryRepository;
import com.daily.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiaryServiceImpl implements DiaryService {

	   private final DiaryRepository diaryRepository;
	    private final FileStorageService fileStorageService;
	    
	    

    public Page<DiaryDTO> findAll(Pageable pageable) {
        return diaryRepository.findAll(pageable).map(diary -> {
            DiaryDTO dto = new DiaryDTO();
            dto.setDno(diary.getDno());
            dto.setTitle(diary.getTitle());
            dto.setContent(diary.getContent());
            dto.setNickname(diary.getUser().getNickname()); // User 엔티티에서 닉네임 가져오기
            dto.setLoginId(diary.getUser().getLoginId());
            dto.setDate(diary.getDate()); // 날짜 설정
            return dto;
        });
    }

 // DiaryService 내에 추가할 메서드
    public Optional<Diary> findDiaryById(Long id) {
        return diaryRepository.findById(id);  // 이 코드는 Optional<Diary>를 반환
    }

   
    @Override
    public DiaryDTO addDiary(DiaryDTO diaryDTO, MultipartFile[] files, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);

        // Set the user details in the DiaryDTO
        diaryDTO.setNickname(user.getNickname()); // Set the nickname from the authenticated user
        diaryDTO.setLoginId(user.getLoginId());   // Set the loginId from the authenticated user

        // Create a new Diary entity from the DTO
        Diary diary = new Diary();
        diary.setTitle(diaryDTO.getTitle());
        diary.setContent(diaryDTO.getContent());
        diary.setMood(diaryDTO.getMood());
        diary.setUser(user);  // Assign the user (author) to the diary
        diary.setDate(LocalDate.now());  // Set the current date
        diary.setModDate(LocalDateTime.now());  // Set the modification date
        diary.setLoginId(diaryDTO.getLoginId());
        diary.setNickname(diaryDTO.getNickname());
        // Handle any images if provided
        if (diaryDTO.getImageList() != null && !diaryDTO.getImageList().isEmpty()) {
            for (DiaryImageDTO imageDTO : diaryDTO.getImageList()) {
                DiaryImage image = new DiaryImage();
                image.setFileName(imageDTO.getFileName());
                image.setFilePath(imageDTO.getImgPath());
                image.setUuid(imageDTO.getUuid());
                diary.addPhoto(image); // Add the image to the diary
            }
        }

        // Save the diary to the repository
        diary = diaryRepository.save(diary);

        // Return the DiaryDTO with the registered diary details
        return DiaryDTO.fromDiary(diary);
    }


    private User getUserFromAuthentication1(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            User user = principalDetails.getUser();
            System.out.println("Authenticated User: " + user.getLoginId() + ", " + user.getNickname());
            return user;
        }
        throw new IllegalArgumentException("Authentication is required to access user details.");
    }

    @Override
    public Page<DiaryDTO> paging(Pageable pageable) {
        Page<Diary> diaries = diaryRepository.findAll(pageable);

        return diaries.map(diary -> {
            // User 정보에서 nickname과 loginId 가져오기 (null 안전)
            String nickname = diary.getUser() != null ? diary.getUser().getNickname() : "Unknown";
            String loginId = diary.getUser() != null ? diary.getUser().getLoginId() : "Unknown";

            // Diary -> DiaryDTO 변환
            return DiaryDTO.builder()
                .dno(diary.getDno())
                .title(diary.getTitle())
                .content(diary.getContent())
                .nickname(nickname)
                .loginId(loginId)
                .mood(diary.getMood())
                .photoUrl(diary.getPhotoUrl() != null ? diary.getPhotoUrl() : new ArrayList<>()) // Null 방지
                .date(diary.getDate())
                .modDate(diary.getModDate())
                .replyCount(diary.getReplyCount())
                .imageList(diary.getImageList() != null ? diary.getImageList().stream()
                    .map(image -> DiaryImageDTO.builder()
                        .id(image.getId())
                        .fileName(image.getFileName())
                        .imgName(image.getImgName())
                        .imgPath(image.getFilePath())
                        .uuid(image.getUuid())
                        .photoUrl(image.getPhotoUrl())
                        .build())
                    .collect(Collectors.toList()) : new ArrayList<>()) // 이미지 리스트 처리
                .build();
        });
    }





    
    public void saveDiary(DiaryDTO diaryDTO, MultipartFile file, User user) {
        // 다이어리 엔티티 생성 및 사용자 설정
        Diary diary = diaryDTO.toEntity(user);

        // 파일을 저장하고 해당 파일의 URL을 반환
        String photoUrl = storeAndGetPhotoUrl(file, diary);  // 다이어리 엔티티를 함께 전달

        // DiaryDTO에 photoUrl 추가
        diaryDTO.setPhotoUrl(Collections.singletonList(photoUrl));

        // 이미지가 다이어리와 연결되도록 엔티티에 추가
        if (photoUrl != null) {
            DiaryImage image = new DiaryImage();
            image.setFilePath(photoUrl);
            image.setFileName(file.getOriginalFilename());
            image.setUuid(UUID.randomUUID().toString());  // UUID 설정
            diary.addPhoto(image);  // 다이어리 엔티티에 이미지 추가
        }

        // Diary 엔티티 저장
        diaryRepository.save(diary);
    }



    public DiaryDTO entityToDTO(Diary diary) {
        if (diary == null) {
            return null;
        }

        // 이미지 리스트를 DTO 리스트로 변환
        List<DiaryImageDTO> imageDTOList = diary.getImageList() != null ? diary.getImageList().stream()
            .map(image -> DiaryImageDTO.builder()
                .id(image.getId())
                .fileName(image.getFileName())
                .imgName(image.getImgName())
                .imgPath(image.getFilePath())
                .uuid(image.getUuid())
                .photoUrl(image.getPhotoUrl() != null ? new ArrayList<>(image.getPhotoUrl()) : new ArrayList<>())
                .build())
            .collect(Collectors.toList()) : new ArrayList<>();

        // Diary 엔티티 -> DiaryDTO로 변환
        return DiaryDTO.builder()
            .dno(diary.getDno())
            .title(diary.getTitle())
            .content(diary.getContent())
            .nickname(diary.getUser() != null ? diary.getUser().getNickname() : "Unknown")
            .loginId(diary.getUser() != null ? diary.getUser().getLoginId() : "Unknown")
            .mood(diary.getMood())
            .photoUrl(diary.getPhotoUrl() != null ? new ArrayList<>(diary.getPhotoUrl()) : new ArrayList<>()) // Null 안전 처리
            .date(diary.getDate())
            .modDate(diary.getModDate())
            .replyCount(diary.getReplyCount())
            .imageList(imageDTOList) // 변환된 이미지 리스트 추가
            .build();
    }


    private String storeAndGetPhotoUrl(MultipartFile file, Diary diary) {
        if (!file.isEmpty()) {
            // 파일 저장 및 UploadDTO 반환
            UploadDTO uploadDTO = fileStorageService.storeFile(file);

            // DiaryImageDTO 생성
            DiaryImageDTO imageDTO = new DiaryImageDTO();
            imageDTO.setFileName(uploadDTO.getFileName());
            imageDTO.setUuid(uploadDTO.getUuid());
            imageDTO.setImgPath("/files/" + uploadDTO.getUuid() + "_" + uploadDTO.getFileName());

            // 이미지 정보 저장 (saveImage 호출)
            fileStorageService.saveImage(imageDTO, diary);

            // 이미지 URL 반환
            return imageDTO.getImgPath();
        }
        return null;
    }




	@Override
    public DiaryDTO findById(Long dno, Authentication authentication) {
        Optional<Diary> diaryOptional = diaryRepository.findById(dno);
        if (!diaryOptional.isPresent()) {
            System.out.println("일기가 데이터베이스에 존재하지 않습니다: " + dno);
            return null;
        }
        Diary diary = diaryOptional.get();
        DiaryDTO diaryDTO = DiaryDTO.fromEntity(diary);
        System.out.println("조회된 일기: " + diaryDTO);
        return diaryDTO;
    }


    @Override
    public void delete(Long dno) {
        diaryRepository.deleteById(dno);
    }

    @Override
    public List<DiaryDTO> findAll(Authentication authentication) {
        return null;
    }

    @Override
    public List<DiaryDTO> getUserDiary(User user) {
        List<Diary> diarys = diaryRepository.findAllByUserId(user.getId());
        return diarys.stream().map(this::entityToDTO).collect(Collectors.toList());
    }

    public Diary dtoToEntity(DiaryDTO dto, User user) {
        if (dto == null) {
            return null;
        }

        // 이미지 리스트 변환
        List<DiaryImage> imageList = dto.getImageList() != null ? dto.getImageList().stream()
            .map(imageDTO -> {
                DiaryImage image = new DiaryImage();
                image.setId(imageDTO.getId());
                image.setFileName(imageDTO.getFileName());
                image.setImgName(imageDTO.getImgName());
                image.setFilePath(imageDTO.getImgPath());
                image.setUuid(imageDTO.getUuid());
                image.setPhotoUrl(imageDTO.getPhotoUrl());
                return image;
            })
            .collect(Collectors.toList()) : new ArrayList<>();

        // DTO에서 엔티티로 변환
        Diary diary = Diary.builder()
            .dno(dto.getDno())
            .title(dto.getTitle())
            .content(dto.getContent())
            .mood(dto.getMood())
            .photoUrl(dto.getPhotoUrl() != null ? new ArrayList<>(dto.getPhotoUrl()) : new ArrayList<>()) // null일 경우 빈 리스트
            .date(dto.getDate() != null ? dto.getDate() : LocalDate.now()) // null일 경우 현재 날짜 설정
            .modDate(dto.getModDate() != null ? dto.getModDate() : LocalDateTime.now()) // null일 경우 현재 시간 설정
            .user(user) // User 설정
            .imageList(imageList)
            .build();

        // 엔티티에 이미지 리스트 추가
        imageList.forEach(image -> image.setDiary(diary));

        return diary;
    }


    @Override
    public DiaryDTO updateDiary(Long dno, DiaryDTO diaryDTO, Authentication authentication) {
        Diary diary = diaryRepository.findById(dno)
            .orElseThrow(() -> new RuntimeException("Diary not found"));

        User user = getUserFromAuthentication1(authentication);
        if (!diary.getUser().equals(user)) {
            throw new RuntimeException("이 작업에 대한 권한이 없습니다.");
        }
        diary.setTitle(diaryDTO.getTitle());
        diary.setContent(diaryDTO.getContent());
        diary.setMood(diaryDTO.getMood());
        diary.setModDate(LocalDateTime.now()); // 수정 날짜를 현재로 설정

        // 이미지 관리 로직 (예시: 기존 이미지를 모두 삭제하고 새로 추가)
        diary.getImageList().clear();
        if (diaryDTO.getPhotoUrl() != null) {
            for (String photoUrl : diaryDTO.getPhotoUrl()) {
                DiaryImage image = new DiaryImage();
                image.setFilePath(photoUrl);
                diary.addPhoto(image);
            }
        }

        diary = diaryRepository.save(diary);
        return new DiaryDTO(diary);
    


    }

	@Override
    public int updateDiary(Long dno, DiaryDTO diaryDTO, User user) {
        return diaryRepository.updateDiary(dno, diaryDTO.getTitle(), diaryDTO.getContent(), diaryDTO.getMood(), user);
    }

    @Override
    public User getUserFromAuthentication(Authentication authentication) {
        // Authentication 객체에서 User 정보를 가져옵니다.
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return principalDetails.getUser();
    }

	@Override
	 public List<DiaryDTO> findAllByUser(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser();  // PrincipalDetails에서 User 가져오기

        // 현재 사용자의 일기만 조회
        List<Diary> diaries = diaryRepository.findAllByUserId(user.getId());
        
        // Diary 엔티티를 DiaryDTO로 변환
        return diaries.stream()
                .map(diary -> new DiaryDTO(diary.getDno(), diary.getTitle(), null, null, null, null, diary.getDate(), null, 0, null, diary.getContent()))
                .collect(Collectors.toList());
    }

	   // 일기를 ID로 조회하는 메서드
    public Diary getDiaryById(Long dno) {
        Optional<Diary> diary = diaryRepository.findById(dno);
        
        // 일기가 존재하지 않으면 예외를 발생시킬 수 있음
        if (diary.isPresent()) {
            return diary.get();
        } else {
            throw new RuntimeException("Diary not found with id: " + dno);
        }
    }
}
