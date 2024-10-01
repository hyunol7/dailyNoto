package com.daily.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.daily.dto.CommentDTO;
import com.daily.entity.User;
import com.daily.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    
    
    
    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
    
    @PostMapping("/add")
    public ResponseEntity<?> addComment(@RequestBody CommentDTO commentDTO, Authentication authentication) {
        try {
            // 댓글 작성 서비스 호출
            CommentDTO createdComment = commentService.addComment(commentDTO, authentication);

            // 작성된 댓글 정보 출력
            System.out.println("작성된 댓글: " + createdComment);

            // 작성된 댓글이 null인 경우 처리
            if (createdComment == null) {
                return ResponseEntity.badRequest().body("{\"error\": true, \"message\": \"댓글 작성에 실패했습니다.\"}");
            }

            // 정상 응답
            return ResponseEntity.ok(createdComment);
        } catch (IllegalArgumentException e) {
            // 예외가 IllegalArgumentException인 경우 명확한 메시지 출력
            System.out.println("댓글 작성 실패: " + e.getMessage());
            return ResponseEntity.badRequest().body("{\"error\": true, \"message\": \"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            // 그 외의 예외 처리
            System.out.println("댓글 작성 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": true, \"message\": \"서버 내부 오류로 댓글 작성에 실패했습니다.\"}");
        }
    }

    
    // 삭제
    @DeleteMapping("/delete/{cno}")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@PathVariable("cno") Long cno, RedirectAttributes redirectAttributes) {
        try {
            commentService.delete(cno);  // This should cascade to comments
            redirectAttributes.addFlashAttribute("message", "댓글이 성공적으로 삭제되었습니다!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "댓글 삭제 실패: " + e.getMessage());
        }
        return "redirect:/diary/detail";
    }
    
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/update/{cno}")
    public ResponseEntity<?> modComment(@PathVariable("cno") Long cno, @RequestBody CommentDTO commentDTO, Authentication authentication) {
        try {
            User user = commentService.getUserFromAuthentication(authentication);
            int updated = commentService.updateComment(cno, commentDTO, user);
            if (updated > 0) {
                return ResponseEntity.ok().body("댓글이 성공적으로 수정되었습니다!");
            } else {
                return ResponseEntity.badRequest().body("댓글 수정 실패: 해당 댓글을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정 실패: " + e.getMessage());
        }
    }

    
}  // CommentController 클래스 종료 중괄호 추가
