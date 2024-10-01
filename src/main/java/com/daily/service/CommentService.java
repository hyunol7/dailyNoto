package com.daily.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;

import com.daily.dto.CommentDTO;
import com.daily.entity.Comment;
import com.daily.entity.User;

public interface CommentService {
		
	int updateComment(Long cno, CommentDTO commentDTO, User user);
	
	  User getUserFromAuthentication(Authentication authentication);

	  List<CommentDTO> getCommentByDiaryDno(Long Dno);
	  
	  CommentDTO addComment(CommentDTO commentDTO, Authentication authentication);
	  
	  CommentDTO findById(Long cno, Authentication authentication);
	  
	  public Optional<Comment> findCommentById(Long id);
	  
	  void delete(Long cno);
	  
	  List<CommentDTO> findAll(Authentication authentication);
	  
	  List<CommentDTO> getUserComment(User user);
	  
	  CommentDTO updateComment(Long cno, CommentDTO commentDTO, Authentication authentication);

	 

	List<CommentDTO> findCommentsByUser(Authentication authentication); 
}
