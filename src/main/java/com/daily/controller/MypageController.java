package com.daily.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.daily.dto.CommentDTO;
import com.daily.dto.DiaryDTO;
import com.daily.dto.TodoDTO;
import com.daily.dto.UserRegistrationDto;
import com.daily.entity.User;
import com.daily.security.PrincipalDetails;
import com.daily.service.CommentService;
import com.daily.service.DiaryService;
import com.daily.service.TodoService;
import com.daily.service.UserService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {

	    private final UserService userService;
	    private final TodoService todoService;
	    private final DiaryService diaryService;
	    private final CommentService commentService;

	    private Authentication getAuthentication() {
	        return SecurityContextHolder.getContext().getAuthentication();
	    }
	    


	    @DeleteMapping("/deleteUser")
	    @PreAuthorize("isAuthenticated()")
	    public ResponseEntity<String> deleteUser(Authentication authentication) {
	        try {
	            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
	            User user = principalDetails.getUser();  // 로그인한 사용자 정보 가져오기
	            
	            userService.deleteUser(user.getId());  // 회원 삭제 서비스 호출

	            return ResponseEntity.ok("회원 탈퇴가 성공적으로 처리되었습니다.");
	        } catch (Exception e) {
	            // 에러가 발생한 경우, 자세한 오류 메시지를 콘솔에 출력
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 탈퇴에 실패했습니다.");
	        }
	    }


	    
	    //회원 수정
	    
	    
	    
	    
	    @GetMapping("")
	    @PreAuthorize("isAuthenticated()")
	    public String mypage(Model model, Authentication authentication, @PageableDefault(size = 10) Pageable pageable) {
	        // 투두리스트 가져오기
	        List<TodoDTO> todos = todoService.findAll(authentication);
	        model.addAttribute("todos", todos);

	        // 로그인한 사용자의 일기 목록 가져오기
	        List<DiaryDTO> userDiaries = diaryService.findAllByUser(authentication);
	        model.addAttribute("userDiaries", userDiaries);

	        // 댓글 목록 가져오기
	        List<CommentDTO> comments = commentService.findCommentsByUser(authentication);
	        model.addAttribute("comments", comments);

	        return "mypage";  // mypage.html로 이동
	    }



	    private void setupPageModel(Model model, Page<DiaryDTO> diaryList) {
	        model.addAttribute("diaryList", diaryList);
	    }
	    
	    // 회원 정보 수정 페이지
	    @GetMapping("/modUser")
	    @PreAuthorize("isAuthenticated()")
	    public String modUser(Model model, Authentication authentication) {
	        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
	        User user = principalDetails.getUser();  // 로그인한 사용자 정보 가져오기
	        
	        // 사용자 정보를 UserRegistrationDto에 담아서 Thymeleaf 템플릿으로 전달
	        UserRegistrationDto userDTO = new UserRegistrationDto(user.getUsername(), user.getNickname(), null);
	        model.addAttribute("userDTO", userDTO);
	        
	        return "modUser";  // modUser.html로 이동
	    }

	    
	    // 회원 정보 수정 처리
	    @PostMapping("/modUser")
	    @PreAuthorize("isAuthenticated()")
	    @ResponseBody
	    public Map<String, Object> mUser(@RequestBody UserRegistrationDto userDTO, Authentication authentication) {
	        Map<String, Object> response = new HashMap<>();
	        try {
	            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
	            User user = principalDetails.getUser();

	            // 사용자 정보 업데이트 (DTO 전체를 넘김)
	            userService.updateUser(user.getId(), userDTO);

	            // 성공 응답
	            response.put("success", true);
	            response.put("nickname", userDTO.getNewNickname());  // 변경된 닉네임을 반환
	            response.put("message", "회원 정보가 성공적으로 수정되었습니다.");
	        } catch (Exception e) {
	            // 오류 발생 시 응답
	            response.put("success", false);
	            response.put("message", "회원 정보 수정에 실패했습니다: " + e.getMessage());
	        }
	        return response;
	    }






    
    
    
}

