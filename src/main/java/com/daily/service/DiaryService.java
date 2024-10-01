package com.daily.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Pageable;
import com.daily.dto.DiaryDTO;
import com.daily.entity.Diary;
import com.daily.entity.User;

public interface DiaryService {

	List<DiaryDTO> findAllByUser(Authentication authentication);
	
	  int updateDiary(Long dno, DiaryDTO diaryDTO, User user);

	    User getUserFromAuthentication(Authentication authentication);

	   
    // 일기를 추가하는 메소드
    DiaryDTO addDiary(DiaryDTO diaryDTO, MultipartFile[] files, Authentication authentication );

    // 일기를 페이징 처리하여 반환하는 메소드
    Page<DiaryDTO> paging(Pageable pageable);

    // 특정 일기를 ID를 기반으로 찾는 메소드
    DiaryDTO findById(Long dno, Authentication authentication);

 // DiaryService 내에 추가할 메서드
    public Optional<Diary> findDiaryById(Long id);
    
    Diary getDiaryById(Long dno);

    
    Page<DiaryDTO> findAll(Pageable pageable);
    
    // 일기를 삭제하는 메소드
    void delete(Long dno);

    // 모든 일기를 반환하는 메소드
    List<DiaryDTO> findAll(Authentication authentication);

    // 특정 사용자의 모든 일기를 반환하는 메소드
    List<DiaryDTO> getUserDiary(User user);

    // 일기를 업데이트하는 메소드
    DiaryDTO updateDiary(Long dno, DiaryDTO diaryDTO, Authentication authentication);
}
