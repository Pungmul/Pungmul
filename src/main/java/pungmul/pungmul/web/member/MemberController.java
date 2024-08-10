package pungmul.pungmul.web.member;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pungmul.pungmul.dto.member.CreateAccountRequestDTO;
import pungmul.pungmul.dto.member.CreateAccountResponseDTO;
import pungmul.pungmul.dto.member.LoginDTO;
import pungmul.pungmul.dto.member.LoginResponseDTO;
import pungmul.pungmul.service.member.CreateMemberService;
import pungmul.pungmul.service.member.LoginService;

import javax.naming.AuthenticationException;

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

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Validated @RequestBody LoginDTO loginDTO,
                                                  HttpServletRequest request) throws AuthenticationException {
        LoginResponseDTO loginResponseDTO = loginService.processLogin(loginDTO, request);

        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDTO);
    }


}
