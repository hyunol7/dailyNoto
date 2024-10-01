package com.daily.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.daily.entity.Comment;
import com.daily.entity.Diary;
import com.daily.entity.User;

import jakarta.transaction.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByUser(User user);
    
    
    List<Comment> findAllByUserId(Long userId);
    List<Comment> findByDiary(Diary diary);
    List<Comment> findByDiary_Dno(Long dno);
    List<Comment> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    
    @Transactional
    @Modifying
    @Query("UPDATE Comment c SET c.content = :content WHERE c.cno = :cno AND c.user = :user")
    int update(@Param("cno") Long cno, @Param("content") String content, @Param("user") User user);

    @Transactional
    @Modifying
    @Query("DELETE FROM Comment c WHERE c.id = :cno AND c.user = :user")
    void deleteComment(@Param("cno") Long cno, @Param("user") User user);


	void deleteByUserId(Long userId);
}
