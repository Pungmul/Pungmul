package pungmul.pungmul.web.member;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.member.InstrumentStatus;
import pungmul.pungmul.dto.member.*;
import pungmul.pungmul.service.member.CreateMemberService;
import pungmul.pungmul.service.member.LoginService;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.List;

/**
 * MemberController 클래스는 회원 관련 요청을 처리하는 REST 컨트롤러입니다.
 * 회원 생성(create) 및 로그인(login) 요청을 처리합니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    // 회원 생성과 로그인 처리 서비스
    private final CreateMemberService createMemberService;
    private final LoginService loginService;

    /**
     * 새로운 회원을 생성하는 메서드.
     * @param createMemberRequestDto 회원 생성 요청 데이터
     * @return 생성된 회원 정보와 함께 201(CREATED) 상태 반환
     */
//    @PostMapping("/signup")
//    public ResponseEntity<CreateAccountResponseDTO> createMember(
//            @Validated @RequestPart("accountData") CreateMemberRequestDTO createMemberRequestDto,
//            @RequestPart("profile") MultipartFile profile) throws IOException {
//        CreateAccountResponseDTO accountResponseDto = createMemberService.createMember(createMemberRequestDto, profile);
//        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDto);
//    }

    @PostMapping(value = "/signup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CreateAccountResponseDTO> createMember(
            @Validated @RequestPart("accountData") CreateMemberRequestDTO createMemberRequestDto,
            @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {
        CreateAccountResponseDTO accountResponseDto = createMemberService.createMember(createMemberRequestDto, profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDto);
    }

    @PostMapping("/inst")
    public ResponseEntity<List<Long>> regInstrument(@RequestParam Long userId, @Validated @RequestBody List<InstrumentStatus> instrumentStatusList){
        return ResponseEntity.ok(createMemberService.createInstrument(userId, instrumentStatusList));
    }

    /**
     * 회원 로그인 요청을 처리하는 메서드.
     * @param loginDTO 로그인 요청 데이터
     * @param request HttpServletRequest 객체로 세션 정보 등을 활용 가능
     * @return 로그인 결과와 함께 200(OK) 상태 반환
     * @throws AuthenticationException 인증 실패 시 예외 발생
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Validated @RequestBody LoginDTO loginDTO,
                                                  HttpServletRequest request) throws AuthenticationException {
        LoginResponseDTO loginResponseDTO = loginService.processLogin(loginDTO, request);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponseDTO);
    }
}
