package com.daily.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.daily.entity.Todo;
import com.daily.entity.User;

import jakarta.transaction.Transactional;

public interface TodoRepository extends JpaRepository<Todo, Long>{
	List<Todo> findAllByDueDate(LocalDate date);
	
	  List<Todo> findByUser(User user);
	  
	 @Modifying
	    @Transactional
	    @Query("UPDATE Todo t SET t.complete = :complete WHERE t.tno = :tno")
	    void updateComplete(Long tno, boolean complete);

	    @Modifying
	    @Transactional
	    @Query("UPDATE Todo t SET t.title = :title WHERE t.tno = :tno")
	    void updateTitle(Long tno, String title);

		List<Todo> findAllByDueDateAndUser(LocalDate date, User user);
		List<Todo> findAllByUserId(Long userId);

		void deleteByUserId(Long userId);

}
