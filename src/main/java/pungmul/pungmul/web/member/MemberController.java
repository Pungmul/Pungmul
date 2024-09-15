package pungmul.pungmul.web.member;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;
import pungmul.pungmul.dto.member.*;
import pungmul.pungmul.service.member.CreateMemberService;
import pungmul.pungmul.service.member.LoginService;
import pungmul.pungmul.core.response.BaseResponse;

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
    public ResponseEntity<BaseResponse<CreateAccountResponseDTO>> createMember(
            @Validated @RequestPart("accountData") CreateMemberRequestDTO createMemberRequestDto,
            @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {

        CreateAccountResponseDTO accountResponseDto = createMemberService.createMember(createMemberRequestDto, profile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ofSuccess(BaseResponseCode.CREATED, accountResponseDto));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/inst")
    public ResponseEntity<BaseResponse<List<Long>>> regInstrument(@AuthenticationPrincipal UserDetailsImpl userDetails, @Validated @RequestBody List<InstrumentStatus> instrumentStatusList, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ofSuccess(BaseResponseCode.CREATED, createMemberService.createInstrument(userDetails.getAccountId(), instrumentStatusList)));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthenticationResponseDTO>> loginJwt(@Validated @RequestBody LoginDTO loginDTO) throws AuthenticationException {
        log.info("loginDTO {}", loginDTO);
        loginService.isValidCredentials(loginDTO);
        AuthenticationResponseDTO response = loginService.authenticate(loginDTO.getLoginId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, response));
    }

//    @GetMapping("/logout")
//    public ResponseEntity<BaseResponse<Void>> logout(@AuthenticationPrincipal UserDetailsImpl userDetails) {
//        log.info("logout - userName  : {} ", userDetails.getUsername());
//        loginService.logout(userDetails);
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(BaseResponse.ofSuccess(ResponseCode.NO_CONTENT));
//    }

//    @PostMapping("/logout")
//    public ResponseEntity<BaseResponse<Void>> logout(HttpServletRequest request) {
//        // 기존 세션이 있는지 확인
//        HttpSession session = request.getSession(false);  // false -> 새로운 세션을 생성하지 않고, 기존 세션을 반환
//        if (session == null) {
//            // 세션이 없으면 사용자가 로그인되어 있지 않은 상태이므로 401 UNAUTHORIZED 반환
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(BaseResponse.ofFail(ResponseCode.UNAUTHORIZED));
//        }
//        // 세션 무효화하여 로그아웃 처리
//        session.invalidate();
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(BaseResponse.ofSuccess(ResponseCode.NO_CONTENT));
//    }

//    @PostMapping("/login")
//    public ResponseEntity<BaseResponse<LoginResponseDTO>> login(@Validated @RequestBody LoginDTO loginDTO,
//                                                                HttpServletRequest request) throws AuthenticationException {
//        LoginResponseDTO loginResponseDTO = loginService.processLogin(loginDTO, request);
//        return ResponseEntity.status(HttpStatus.OK)
//                .body(BaseResponse.ofSuccess(ResponseCode.OK,loginResponseDTO));
//    }
}
