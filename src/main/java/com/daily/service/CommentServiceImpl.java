package com.daily.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.daily.dto.CommentDTO;
import com.daily.entity.Comment;
import com.daily.entity.Diary;
import com.daily.entity.User;
import com.daily.repository.CommentRepository;
import com.daily.repository.DiaryRepository;
import com.daily.security.PrincipalDetails;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
	
	private final CommentRepository commentRepository;
	private final DiaryRepository diaryRepository;
	
	
	
	@Override
	public int updateComment(Long cno, CommentDTO commentDTO, User user) {
		return commentRepository.update(cno, commentDTO.getContent(), user);
	}

	 @Override
	    public User getUserFromAuthentication(Authentication authentication) {
	        // Authentication 객체에서 User 정보를 가져옵니다.
	        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
	        return principalDetails.getUser();
	    }

	 @Override
	 public CommentDTO addComment(CommentDTO commentDTO, Authentication authentication) {
	     User user = getUserFromAuthentication(authentication);
	     if (user == null) {
	         throw new IllegalArgumentException("Authentication is required.");
	     }

	     // 다이어리 객체를 데이터베이스에서 조회
	     Long diaryId = commentDTO.getDiaryId();
	     Diary diary = diaryRepository.findById(diaryId)
	         .orElseThrow(() -> new IllegalArgumentException("Diary not found with ID: " + diaryId));

	     // 새 댓글 객체 생성 및 설정
	     Comment comment = new Comment();
	     comment.setContent(commentDTO.getContent());
	     comment.setNickname(user.getNickname());  // 로그인한 사용자 닉네임 설정
	     comment.setUser(user);  // 로그인한 사용자 설정
	     comment.setDiary(diary);  // 다이어리 설정
	     comment.setDate(LocalDate.now());
	     comment.setModDate(LocalDateTime.now());

	     // 댓글 저장
	     comment = commentRepository.save(comment);

	     // CommentDTO 반환
	     return CommentDTO.builder()
	         .cno(comment.getCno())
	         .content(comment.getContent())
	         .nickname(user.getNickname())  // nickname 추가
	         .loginId(user.getLoginId())  // loginId 추가
	         .diaryId(diary.getDno())
	         .build();
	 }


	@Override
	public CommentDTO findById(Long cno, Authentication authentication) {
		Optional<Comment> commentOptional = commentRepository.findById(cno);
		if (!commentOptional.isPresent()) {
			System.out.println("댓글이 존재하지 않습니다 : " + cno);
			return null;
		}
		Comment comment = commentOptional.get();
		CommentDTO commentDTO = CommentDTO.fromComment(comment);
		System.out.println("조회된 댓글 : " + commentDTO);
		return commentDTO;
	}

	@Override
	public Optional<Comment> findCommentById(Long id) {
		return commentRepository.findById(id);
	}

	@Override
	public void delete(Long cno) {
		commentRepository.deleteById(cno);
	}

	@Override
	public List<CommentDTO> findAll(Authentication authentication) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CommentDTO> getUserComment(User user) {
		List<Comment> comments = commentRepository.findAllByUserId(user.getId());
		return comments.stream().map(this::entityToDTO).collect(Collectors.toList());
	}

	private CommentDTO entityToDTO(Comment comment) {
        if (comment == null) return null;
        CommentDTO commentDTO = new CommentDTO(comment);
        return commentDTO;
    }
	
	@Override
	public CommentDTO updateComment(Long cno, CommentDTO commentDTO, Authentication authentication) {
		Comment comment = commentRepository.findById(cno)
			      .orElseThrow(() -> new RuntimeException("comment not found"));      
		
		 User user = getUserFromAuthentication(authentication);
	        if (!comment.getUser().equals(user)) {
	            throw new RuntimeException("이 작업에 대한 권한이 없습니다.");
	        }
	        comment.setContent(commentDTO.getContent());
	        comment.setModDate(LocalDateTime.now());
	        
	       comment = commentRepository.save(comment);
		return new CommentDTO(comment);
	}

	@Override
	public List<CommentDTO> getCommentByDiaryDno(Long Dno) {
	    List<Comment> comments = commentRepository.findByDiary_Dno(Dno);
	    return comments.stream().map(comment -> CommentDTO.builder()
	            .cno(comment.getCno())
	            .content(comment.getContent())
	            .date(comment.getDate())
	            .modDate(comment.getModDate())
	            .nickname(comment.getUser() != null ? comment.getUser().getNickname() : "Unknown User")  // 유저가 없는 경우 "Unknown User"로 처리
	            .loginId(comment.getUserLoginId())
	            .build())
	        .collect(Collectors.toList());
	}
	
	@Override
	public List<CommentDTO> findCommentsByUser(Authentication authentication) {
	    // 인증된 사용자 정보 가져오기
	    PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
	    User user = principalDetails.getUser();  // PrincipalDetails에서 User 가져오기

	    // 해당 사용자 ID로 댓글 조회
	    List<Comment> comments = commentRepository.findAllByUserId(user.getId());

	    // Comment 엔티티를 CommentDTO로 변환
	    return comments.stream()
	            .map(comment -> new CommentDTO(comment.getCno(), comment.getContent(), comment.getCreatedDate(), null, user, user.getLoginId(), null, null))
	            .collect(Collectors.toList());  // List<CommentDTO>로 변환 후 반환
	}


	

	
}
