package com.daily.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.core.Authentication;

import com.daily.dto.TodoDTO;
import com.daily.entity.User;

public interface TodoService {
   
    TodoDTO createTodo(TodoDTO todoDTO, Authentication authentication);
    TodoDTO updateTodo(Long tno, TodoDTO todoDTO, Authentication authentication);
    void deleteTodo(Long tno, Authentication authentication);
    List<TodoDTO> findTasksByDate(LocalDate date, Authentication authentication);
    List<TodoDTO> findAll(Authentication authentication);
    TodoDTO create(TodoDTO todoDTO, Authentication authentication);
    TodoDTO findById(Long tno, Authentication authentication);
    void updateComplete(Long tno, boolean complete, Authentication authentication);
    List<TodoDTO> getUserTodos(User user);

}
