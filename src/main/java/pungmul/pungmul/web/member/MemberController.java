package pungmul.pungmul.web.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pungmul.pungmul.domain.member.SessionUser;
import pungmul.pungmul.service.member.CreateMemberService;
import pungmul.pungmul.service.member.LoginService;
import pungmul.pungmul.web.member.dto.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final CreateMemberService createMemberService;
    private final LoginService loginService;

    @PostMapping("/create")
    public ResponseEntity<CreateAccountResponseDTO> createMember(@Validated @RequestBody CreateAccountRequestDTO createAccountRequestDto) {
        CreateAccountResponseDTO accountResponseDto = createMemberService.createMember(createAccountRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDto);
    }

//    @PostMapping("/login")
//    public ResponseEntity<LoginResponseDTO> login(@Validated @RequestBody LoginDTO loginDto,
//                                                  HttpServletRequest request){
//        HttpSession session = request.getSession(false);
//
//        if (session != null)
//            session.invalidate();
//
//        SessionUser sessionUser = loginService.login(loginDto);
//
//         session = request.getSession(true);
//         session.setAttribute(SessionConst.SESSION_USER, sessionUser);
//
//
//
//         return ResponseEntity.ok(sessionUser);
//    }
}
