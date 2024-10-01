package com.daily.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.daily.dto.CommentDTO;
import com.daily.dto.DiaryDTO;
import com.daily.dto.DiaryImageDTO;
import com.daily.dto.UploadDTO;
import com.daily.entity.User;
import com.daily.service.CommentService;
import com.daily.service.DiaryService;
import com.daily.service.FileStorageService;


import java.io.IOException;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/diary")
@RequiredArgsConstructor
public class DiaryController {
	
    private final FileStorageService fileStorageService;
    private final DiaryService diaryService;
    private final CommentService commentService;
    
    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    

 


    @PostMapping("/addDiary")  // 경로 수정
    public String addDiary(
            @ModelAttribute DiaryDTO diaryDTO,
            @RequestParam("files") MultipartFile[] files,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {

        try {
            // 파일 저장 처리
            List<DiaryImageDTO> imageDTOList = processFiles(files); // 파일 저장 메서드 호출
            diaryDTO.setImageList(imageDTOList);

            // 일기 저장 처리
            diaryService.addDiary(diaryDTO,files, authentication);
            redirectAttributes.addFlashAttribute("message", "일기가 성공적으로 추가되었습니다!");
            return "redirect:/diary/diaries";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "일기 추가 실패: " + e.getMessage());
            return "redirect:/diary/addDiary";
        }
    }
    private List<DiaryImageDTO> processFiles(MultipartFile[] files) throws IOException {
        List<DiaryImageDTO> imageDTOList = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                // 파일 저장 처리
                UploadDTO uploadDTO = fileStorageService.storeFile(file);

                // 저장된 파일 정보를 DiaryImageDTO로 변환하여 리스트에 추가
                DiaryImageDTO imageDTO = new DiaryImageDTO();
                imageDTO.setFileName(uploadDTO.getFileName());
                imageDTO.setUuid(uploadDTO.getUuid());
                imageDTO.setImgPath(uploadDTO.getFolderPath());
                imageDTOList.add(imageDTO);
            }
            System.out.println("파일 저장 : " + imageDTOList);
        }
        return imageDTOList;
    }



    @GetMapping("/api/diary/images")
    @ResponseBody
    public ResponseEntity<List<String>> getDiaryImages(@RequestParam Long diaryId) {
        try {
            DiaryDTO diary = diaryService.findById(diaryId, null);  // null 인자가 필요한지 확인 필요
            List<String> photoUrls = diary.getPhotoUrl() != null ? diary.getPhotoUrl() : new ArrayList<>();
            return ResponseEntity.ok(photoUrls);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<>());
        }
    }


   
    // 일기 목록 페이지 메서드
    @GetMapping("/diaries")
    public String diaries(Model model, @PageableDefault(size = 10) Pageable pageable) {
        Page<DiaryDTO> diaryList = diaryService.findAll(pageable);
        setupPageModel(model, diaryList);
        return "diary/diaries";
    }
    

    @GetMapping("/detail/{dno}")
    public String getDiaryDetail(@PathVariable("dno") Long dno, Model model, Authentication authentication) {
        DiaryDTO diary = diaryService.findById(dno, authentication);
        if (diary == null) {
            model.addAttribute("errorMessage", "해당 일기가 존재하지 않습니다.");
            return "redirect:/diary/diaries";
        }

        // 이미지 리스트가 없으면 빈 리스트로 처리
        if (diary.getImageList() == null) {
            diary.setImageList(new ArrayList<>());
        }

        // URL에 null 값이 포함되지 않도록 경로 확인
        List<String> validPhotoUrls = diary.getPhotoUrl() != null ? diary.getPhotoUrl() : new ArrayList<>();
        diary.setPhotoUrl(validPhotoUrls);

        List<CommentDTO> comment = commentService.getCommentByDiaryDno(dno);
        model.addAttribute("diary", diary);  // 이 부분에서 diary가 템플릿으로 전달됨
        model.addAttribute("comment", comment);
        return "diary/detail";  // templates/diary/detail.html 템플릿
    }



    
   




    // 일기 삭제 메서드
    @PostMapping("/delete/{dno}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String deleteDiary(@PathVariable("dno") Long dno, RedirectAttributes redirectAttributes) {
        try {
            diaryService.delete(dno);  // This should cascade to comments
            redirectAttributes.addFlashAttribute("message", "일기와 관련된 모든 댓글이 성공적으로 삭제되었습니다!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "일기 삭제 실패: " + e.getMessage());
        }
        return "redirect:/diary/diaries";
    }
    
    


    // 일기 추가 페이지 이동 메서드
    @GetMapping("/addDiary")
    public String addDiaryForm(Model model) {
        model.addAttribute("diaryDTO", new DiaryDTO());
        return "diary/addDiary";
    }

    // 일기 수정 페이지 이동 메서드
    @GetMapping("/modDiary/{dno}")
    @PreAuthorize("isAuthenticated()")
    public String modDiaryForm(@PathVariable("dno") Long dno, Model model, Authentication authentication) {
        DiaryDTO diary = diaryService.findById(dno, authentication);
        model.addAttribute("diaryDTO", diary);
        System.out.println(dno);
        return "diary/modDiary"; // templates/diary/modDiary.html 템플릿을 반환
    }
    // 일기 수정 메서드
    @PostMapping("/modDiary/{dno}")
    @PreAuthorize("isAuthenticated()")
    public String modDiary(@PathVariable("dno") Long dno, @ModelAttribute DiaryDTO diaryDTO, @RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes, Authentication authentication) {
        System.out.println("modDiary 메서드 호출됨. dno: " + dno);
        try {
            // 사용자의 정보 가져오기
            User user = diaryService.getUserFromAuthentication(authentication);
            
            if (!file.isEmpty()) {
            	 UploadDTO uploadDTO = fileStorageService.storeFile(file);
                 List<String> photoUrls = new ArrayList<>();
                 photoUrls.add("/files/" + uploadDTO.getUuid() + "_" + uploadDTO.getFileName());
                 diaryDTO.setPhotoUrl(photoUrls);
            }

            // 기존 일기 업데이트
            int updated = diaryService.updateDiary(dno, diaryDTO, user);
            
            if (updated > 0) {
                redirectAttributes.addFlashAttribute("message", "일기가 성공적으로 수정되었습니다!");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "일기 수정 실패: 해당 일기를 찾을 수 없습니다.");
            }
            return "redirect:/diary/diaries";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "일기 수정 실패: " + e.getMessage());
            return "redirect:/diary/modDiary/" + dno;
        }
    }
    

    
    private void setupPageModel(Model model, Page<DiaryDTO> diaryList) {
        model.addAttribute("diaryList", diaryList);

        int blockLimit = 5;
        int startPage = (diaryList.getNumber() / blockLimit) * blockLimit + 1;
        int endPage = Math.min(startPage + blockLimit - 1, diaryList.getTotalPages());

        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
    }
}
