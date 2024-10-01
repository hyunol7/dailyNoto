package com.daily.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.daily.dto.JoinRequestDTO;
import com.daily.dto.LoginRequestDto;
import com.daily.entity.User;
import com.daily.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/login")
public class SecurityLoginController {

    private final UserService userService;

    @GetMapping
    public String home(Authentication authentication, Model model) {
        model.addAttribute("loginType", "login");
        model.addAttribute("pageName", "Security 로그인");

        if (authentication != null) {
            User loginUser = userService.getLoginUserByLoginId(authentication.getName());
            if (loginUser != null) {
                model.addAttribute("nickname", loginUser.getNickname());
            }
        }
        return "home";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {
        model.addAttribute("joinRequest", new JoinRequestDTO());
        return "join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("joinRequest") JoinRequestDTO joinRequest, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "join";
        }
        if (userService.checkLoginIdDuplicate(joinRequest.getLoginId())) {
            bindingResult.rejectValue("loginId", "loginId.duplicate", "로그인 아이디가 중복됩니다.");
            return "join";
        }
        if (userService.checkNicknameDuplicate(joinRequest.getNickname())) {
            bindingResult.rejectValue("nickname", "nickname.duplicate", "닉네임이 중복됩니다.");
            return "join";
        }
        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.rejectValue("passwordCheck", "passwordCheck.mismatch", "비밀번호가 일치하지 않습니다.");
            return "join";
        }

        userService.join2(joinRequest); // 데이터베이스에 저장
        redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다. 로그인해 주세요.");
        return "redirect:/login/login"; // 로그인 페이지로 리디렉션
    }


    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequestDto());
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequestDto loginRequest, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        try {
            userService.login(loginRequest); // 서비스 레이어에서 로그인 로직 처리
            return "redirect:/main"; // 로그인 성공 시 메인 페이지로 리디렉션
        } catch (RuntimeException e) {
            // Add error message to the model
            model.addAttribute("errorMessage", "아이디 및 비밀번호를 다시 확인해 주세요");
            return "login"; // 실패 시 로그인 페이지에 머물며 에러 메시지 표시
        }
    }


    @GetMapping("/info")
    public String userInfo(Authentication authentication, Model model) {
        User loginUser = userService.getLoginUserByLoginId(authentication.getName());

        if (loginUser == null) {
            return "redirect:/login/login";
        }

        model.addAttribute("user", loginUser);
        return "info";
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin";
    }
}
