package com.daily.repository;


import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.daily.entity.Diary;
import com.daily.entity.User;

import jakarta.transaction.Transactional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
	 List<Diary> findByUser(User user);

	    List<Diary> findAllByUserId(Long userId);

	    List<Diary> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);

	    Page<Diary> findByUser(User user, org.springframework.data.domain.Pageable pageable);

	    @Query("SELECT d FROM Diary d WHERE d.user = :user AND (d.title LIKE %:keyword% OR d.content LIKE %:keyword%)")
	    List<Diary> searchByUserAndKeyword(@Param("user") User user, @Param("keyword") String keyword);

	    @Transactional
	    @Modifying
	    @Query("UPDATE Diary d SET d.title = :title, d.content = :content, d.mood = :mood WHERE d.id = :dno AND d.user = :user")
	    int updateDiary(@Param("dno") Long dno, @Param("title") String title, @Param("content") String content, @Param("mood") String mood, @Param("user") User user);

	    @Transactional
	    @Modifying
	    @Query("DELETE FROM Diary d WHERE d.id = :dno AND d.user = :user")
	    void deleteDiary(@Param("dno") Long dno, @Param("user") User user);

		void deleteByUserId(Long userId);
	    
	   
}

