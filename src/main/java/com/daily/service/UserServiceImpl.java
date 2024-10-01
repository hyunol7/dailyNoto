package com.daily.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.daily.dto.JoinRequestDTO;
import com.daily.dto.LoginRequestDto;
import com.daily.dto.UserRegistrationDto;
import com.daily.entity.User;
import com.daily.repository.CommentRepository;
import com.daily.repository.DiaryRepository;
import com.daily.repository.TodoRepository;
import com.daily.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	 @Autowired
	    private UserRepository userRepository;
	    @Autowired
	    private PasswordEncoder passwordEncoder;
	    @Autowired
	    private CommentRepository commentRepository;
	    @Autowired
	    private DiaryRepository diaryRepository;
	    @Autowired
	    private TodoRepository todoRepository;


    @Override
    public boolean checkLoginIdDuplicate(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    @Override
    public boolean checkNicknameDuplicate(String nickname) {
        boolean exists = userRepository.existsByNickname(nickname);
        if (exists) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다: " + nickname);
        }
        return false;  // 닉네임이 중복되지 않으면 false를 반환
    }


    @Override
    public void join(JoinRequestDTO req) {
        userRepository.save(req.toEntity());
    }

    @Override
    public void join2(JoinRequestDTO req) {
        userRepository.save(req.toEntity(passwordEncoder.encode(req.getPassword())));
    }

    @Override
    public User login(LoginRequestDto req) {
        Optional<User> optionalUser = userRepository.findByLoginId(req.getLoginId());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                return user;
            } else {
                throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
    }

    @Override
    public User getLoginUserById(Long userId) {
        if(userId == null) return null;

        Optional<User> optionalUser = userRepository.findById(userId);
        if(optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }

    @Override
    public User getLoginUserByLoginId(String loginId) {
        if(loginId == null) return null;

        Optional<User> optionalUser = userRepository.findByLoginId(loginId);
        if(optionalUser.isEmpty()) return null;

        return optionalUser.get();
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        // 사용자가 DB에 존재하는지 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

     // 먼저 사용자 ID를 참조하는 모든 외래키 삭제
        commentRepository.deleteByUserId(userId);
        todoRepository.deleteByUserId(userId);
        diaryRepository.deleteByUserId(userId);
        
        // 회원 삭제
        userRepository.delete(user);
    }

	@Override
	public void updateUser(Long userId, UserRegistrationDto dto) {
	    User user = userRepository.findById(userId)
	        .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

	    // 이름이 제공된 경우 업데이트
	    if (dto.getName() != null && !dto.getName().isEmpty()) {
	        user.setUsername(dto.getName());
	    }

	    // 새 닉네임이 제공된 경우 닉네임 업데이트
	    if (dto.getNewNickname() != null && !dto.getNewNickname().isEmpty()) {
	        user.setNickname(dto.getNewNickname());
	    }

	    // 변경된 사용자 정보를 저장
	    userRepository.save(user);
	}


}
