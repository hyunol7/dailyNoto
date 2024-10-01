package com.daily.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.daily.dto.TodoDTO;
import com.daily.entity.Todo;
import com.daily.entity.User;
import com.daily.repository.TodoRepository;
import com.daily.security.PrincipalDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoServiceImpl implements TodoService {

    @Autowired
    private final TodoRepository todoRepository;
    
    private static final Logger logger = LoggerFactory.getLogger(TodoServiceImpl.class);
    
    
    private void checkUserPermission(Todo todo, User user) {
        if (!todo.getUser().equals(user)) {
            throw new RuntimeException("이 작업에 대한 권한이 없습니다.");
        }
    }


    private User getUserFromAuthentication(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        return principalDetails.getUser(); // PrincipalDetails에서 User 객체를 얻습니다.
    }

    // 사용자 ID로 투두 리스트를 조회
    @Override
    public List<TodoDTO> getUserTodos(User user) {
        List<Todo> todos = todoRepository.findAllByUserId(user.getId());
        return todos.stream().map(this::entityToDTO).collect(Collectors.toList());
    }


    
    @Override
    public TodoDTO createTodo(TodoDTO todoDTO, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        
        Todo todo = new Todo();
        todo.setTitle(todoDTO.getTitle());
        todo.setDescription(todoDTO.getDescription());
        todo.setDueDate(todoDTO.getDueDate());
        todo.setComplete(false);
        todo.setUser(user); // 설정된 사용자
        todo = todoRepository.save(todo);
        return new TodoDTO(todo);
    }

    @Override
    public List<TodoDTO> findAll(Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<Todo> todos = todoRepository.findByUser(user);
        return todos.stream().map(TodoDTO::new).collect(Collectors.toList());
    }

    @Override
    public TodoDTO updateTodo(Long tno, TodoDTO todoDTO, Authentication authentication) {
        logger.info("Updating todo with tno: {}", tno);

        Todo todo = todoRepository.findById(tno)
            .orElseThrow(() -> new RuntimeException("할 일을 찾을 수 없음"));
        
        User user = getUserFromAuthentication(authentication);
        checkUserPermission(todo, user);

        todo.setTitle(todoDTO.getTitle());
        todo.setDescription(todoDTO.getDescription());
        todo.setDueDate(todoDTO.getDueDate());
        todo.setComplete(todoDTO.isComplete());
        
        todoRepository.save(todo);

        logger.info("Todo updated successfully with tno: {}", tno);
        return new TodoDTO(todo);
    }



    @Override
    public void deleteTodo(Long tno, Authentication authentication) {
        Todo todo = todoRepository.findById(tno).orElseThrow(() ->
                new RuntimeException("할 일을 찾을 수 없음"));
        User user = getUserFromAuthentication(authentication);
        if (!todo.getUser().equals(user)) {
            throw new RuntimeException("이 작업에 대한 권한이 없습니다.");
        }
        todoRepository.delete(todo);
    }

    @Override
    public List<TodoDTO> findTasksByDate(LocalDate date, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        List<Todo> todos = todoRepository.findAllByDueDateAndUser(date, user);
        return todos.stream().map(this::entityToDTO).collect(Collectors.toList());
    }



    @Override
    public void updateComplete(Long tno, boolean complete, Authentication authentication) {
        Todo todo = todoRepository.findById(tno).orElseThrow(() ->
                new RuntimeException("할 일을 찾을 수 없음"));
        User user = getUserFromAuthentication(authentication);
        if (!todo.getUser().equals(user)) {
            throw new RuntimeException("이 작업에 대한 권한이 없습니다.");
        }
        todo.setComplete(complete);
        todoRepository.save(todo);
    }

    @Override
    public TodoDTO create(TodoDTO todoDTO, Authentication authentication) {
        User user = getUserFromAuthentication(authentication);
        Todo todo = dtoToEntity(todoDTO);
        todo.setUser(user); // 설정된 사용자
        todo = todoRepository.save(todo);
        return new TodoDTO(todo);
    }

    @Override
    public TodoDTO findById(Long tno, Authentication authentication) {
        Todo todo = todoRepository.findById(tno).orElseThrow(() ->
                new RuntimeException("할 일을 찾을 수 없음"));
        User user = getUserFromAuthentication(authentication);
        if (!todo.getUser().equals(user)) {
            throw new RuntimeException("이 작업에 대한 권한이 없습니다.");
        }
        return new TodoDTO(todo);
    }

    private Todo dtoToEntity(TodoDTO todoDTO) {
        return Todo.builder()
                .tno(todoDTO.getTno())
                .title(todoDTO.getTitle())
                .description(todoDTO.getDescription())
                .dueDate(todoDTO.getDueDate())
                .complete(todoDTO.isComplete())
                .build();
    }

    private TodoDTO entityToDTO(Todo todo) {
        if (todo == null) return null;
        TodoDTO todoDTO = new TodoDTO(todo);
        return todoDTO;
    }

}
