package com.daily.dto;

import java.time.LocalDate;

import com.daily.entity.Todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoDTO {
	
	private Long tno;
	private String title;
	private String description;
	private LocalDate dueDate; 
	private boolean complete;
	
    public TodoDTO(Todo todo) {
        this.tno = todo.getTno();
        this.title = todo.getTitle();
        this.description = todo.getDescription();
        this.dueDate = todo.getDueDate();
        this.complete = todo.isComplete();
    }
    
    

}
