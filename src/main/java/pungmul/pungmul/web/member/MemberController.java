package pungmul.pungmul.web.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.domain.member.InstrumentStatus;
import pungmul.pungmul.domain.member.SessionUser;
import pungmul.pungmul.dto.member.*;
import pungmul.pungmul.service.member.CreateMemberService;
import pungmul.pungmul.service.member.LoginService;
import pungmul.pungmul.service.member.loginvalidation.user.User;

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
     * 사용자의 회원가입 요청을 처리하는 메서드
     *
     * 이 메서드는 `/signup` 엔드포인트와 매핑되어 있으며, `multipart/form-data` 형식의 요청을 처리
     * 요청은 회원가입 정보를 담은 JSON 데이터(`accountData`)와 선택적인 프로필 이미지(`profile`)를 포함
     *
     * @param createMemberRequestDto 회원가입에 필요한 데이터를 담고 있는 DTO 객체
     *                               이 객체는 JSON 형식으로 전달되며, `@RequestPart`로 매핑
     * @param profile 선택적인 프로필 이미지 파일
     *                이 파일은 `@RequestPart`로 받아지며, 필요에 따라 업로드 가능
     * @return 회원가입이 성공적으로 완료되었을 경우, `201 Created` 상태 코드와 함께 `CreateAccountResponseDTO` 객체를 반환
     * @throws IOException 프로필 이미지 업로드 중 발생할 수 있는 IO 예외를 처리
     */


    @PostMapping(value = "/signup", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<CreateAccountResponseDTO> createMember(
            @Validated @RequestPart("accountData") CreateMemberRequestDTO createMemberRequestDto,
            @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {
        CreateAccountResponseDTO accountResponseDto = createMemberService.createMember(createMemberRequestDto, profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDto);
    }

    @PostMapping("/inst")
    public ResponseEntity<List<Long>> regInstrument(@User SessionUser sessionUser, @Validated @RequestBody List<InstrumentStatus> instrumentStatusList){
        return ResponseEntity.ok(createMemberService.createInstrument(sessionUser.getUserId(), instrumentStatusList));
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

    @PostMapping("/login-jwt")
    public ResponseEntity<AuthenticationResponseDTO> loginJwt(@Validated @RequestBody LoginDTO loginDTO,
                                                     HttpServletRequest request) throws AuthenticationException {
        loginService.isValidCredentials(loginDTO);
        AuthenticationResponseDTO response = loginService.authenticate(loginDTO);

        log.info("[authenticate] login response: {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        // 기존 세션이 있는지 확인
        HttpSession session = request.getSession(false);  // false -> 새로운 세션을 생성하지 않고, 기존 세션을 반환
        if (session == null) {
            // 세션이 없으면 사용자가 로그인되어 있지 않은 상태이므로 401 UNAUTHORIZED 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("로그인 상태 아님");
        }
        // 세션 무효화하여 로그아웃 처리
        session.invalidate();
        return ResponseEntity.ok()
                .body("로그아웃 되었습니다.");
    }

}
