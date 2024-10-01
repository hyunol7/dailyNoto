package com.daily.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.daily.dto.TodoDTO;
import com.daily.entity.User;
import com.daily.repository.TodoRepository;
import com.daily.security.PrincipalDetails;
import com.daily.service.TodoService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/todo")
public class TodoController {

    private final TodoService todoService;
    private final TodoRepository todoRepository;

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    //리스트 목록
    @GetMapping("/todolist")
    public String showTodos(Model model) {
        Authentication authentication = getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login"; // 로그인 페이지로 리다이렉트
        }
        model.addAttribute("todos", todoService.findAll(authentication));
        return "todo/todolist";  // templates 폴더 아래의 todo 폴더를 참조
    }

    //투두 등록
    @PostMapping("/todolist")
    public ResponseEntity<TodoDTO> addTask(@RequestBody TodoDTO todoDTO) {
        Authentication authentication = getAuthentication();
        TodoDTO newTask = todoService.create(todoDTO, authentication);
        return ResponseEntity.ok(newTask);
    }

    //투두 체크
    @PutMapping("/todos/{tno}/complete")
    public ResponseEntity<Void> toggleCompletion(@PathVariable("tno") Long tno, @RequestBody Map<String, Boolean> body) {
        boolean isComplete = body.get("complete");
        Authentication authentication = getAuthentication();
        todoService.updateComplete(tno, isComplete, authentication);
        return ResponseEntity.ok().build();
    }

    //수정
    @PutMapping("/update/{tno}")
    public ResponseEntity<String> updateTodo(@PathVariable("tno") Long tno, @RequestBody TodoDTO todoDTO, Authentication authentication) {
        todoService.updateTodo(tno, todoDTO, authentication);
        return new ResponseEntity<>("Todo updated successfully", HttpStatus.OK);
    }



    //삭제
    @DeleteMapping("/todolist/delete/{tno}")
    public ResponseEntity<Void> deleteTodo(@PathVariable("tno") Long tno) {
        Authentication authentication = getAuthentication();
        todoService.deleteTodo(tno, authentication);
        return ResponseEntity.noContent().build();
    }

    //날짜
    @GetMapping("/date/{date}")
    public ResponseEntity<List<TodoDTO>> findTasksByDate(@PathVariable("date") String date) {
        LocalDate parsedDate = LocalDate.parse(date);
        Authentication authentication = getAuthentication();
        List<TodoDTO> tasks = todoService.findTasksByDate(parsedDate, authentication);
        return ResponseEntity.ok(tasks);
    }

    
    @GetMapping("/user-todos")
    public ResponseEntity<List<TodoDTO>> getUserTodos(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User user = principalDetails.getUser(); // 사용자 객체를 가져옵니다
        List<TodoDTO> userTodos = todoService.getUserTodos(user);
        return ResponseEntity.ok(userTodos);
    }



    private User getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof PrincipalDetails) {
            PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
            return principalDetails.getUser();
        }
        throw new RuntimeException("User not authenticated");
    }
}
