package pungmul.pungmul.web.member;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pungmul.pungmul.config.security.UserDetailsImpl;
import pungmul.pungmul.core.response.BaseResponseCode;
import pungmul.pungmul.domain.member.instrument.InstrumentStatus;
import pungmul.pungmul.dto.member.*;
import pungmul.pungmul.core.response.BaseResponse;
import pungmul.pungmul.service.member.authentication.LoginService;
import pungmul.pungmul.service.member.membermanagement.*;

import java.io.IOException;
import java.util.List;

/**
 * MemberController 클래스는 회원 관련 요청을 처리하는 REST 컨트롤러입니다.
 * 회원 생성(create) 및 로그인(login) 요청을 처리합니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
public class MemberController {

    // 회원 생성과 로그인 처리 서비스
    private final MemberManagementService memberManagementService;
    private final MemberService memberService;
    private final LoginService loginService;
    private final InvitationCodeService invitationCodeService;

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

        log.info("signup controller method");
        CreateAccountResponseDTO accountResponseDto = memberManagementService.createMember(createMemberRequestDto, profile);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ofSuccess(BaseResponseCode.CREATED, accountResponseDto));
    }

    @GetMapping("/clubs")
    public ResponseEntity<BaseResponse<ClubListResponseDTO>> getClubList(){
        ClubListResponseDTO clubList = memberManagementService.getClubList();
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, clubList));
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("")
    public ResponseEntity<BaseResponse<UpdateMemberResponseDTO>> updateMember(
            @AuthenticationPrincipal UserDetails userDetails,
            @Validated @RequestPart("accountData") UpdateMemberRequestDTO updateMemberRequestDTO,
            @RequestPart(value = "profile", required = false) MultipartFile profile) throws IOException {
        UpdateMemberResponseDTO updateMemberResponseDTO = memberManagementService.updateMember(userDetails,updateMemberRequestDTO, profile);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, updateMemberResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/inst")
    public ResponseEntity<BaseResponse<List<Long>>> regInstrument(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Validated @RequestBody List<InstrumentStatus> instrumentStatusList, HttpServletRequest request){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.ofSuccess(BaseResponseCode.CREATED, memberManagementService.createInstrument(userDetails.getAccountId(), instrumentStatusList)));
    }

    @PreAuthorize("hasRole('USER')")
    @PatchMapping("/inst")
    public ResponseEntity<BaseResponse<UpdateInstrumentResponseDTO>> updateInstrument(
            @AuthenticationPrincipal UserDetails userDetail,
            @RequestBody UpdateInstrumentRequestDTO updateInstrumentRequestDTO
            ){
        UpdateInstrumentResponseDTO updateInstrumentResponseDTO = memberManagementService.updateInstrumentStatus(userDetail,updateInstrumentRequestDTO);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, updateInstrumentResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("")
    public ResponseEntity<BaseResponse<GetMemberResponseDTO>> getMemberInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        GetMemberResponseDTO memberInfo = memberService.getMemberInfo(userDetails.getUsername());
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, memberInfo));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/users")
    public ResponseEntity<BaseResponse<SearchUserResponseDTO>> searchUsers(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String keyword
            ){
        SearchUserResponseDTO searchUserResponseDTO = memberService.searchUsers(userDetails, keyword);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, searchUserResponseDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("")
    public ResponseEntity<BaseResponse<Void>> deleteMember(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        memberManagementService.deleteMember(userDetails);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/ban")
    public ResponseEntity<BaseResponse<BanMemberResponseDTO>> banMember(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody BanMemberRequestDTO banMemberRequestDTO
            ) {
        BanMemberResponseDTO banMemberResponseDTO = memberManagementService.banMember(banMemberRequestDTO);
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, banMemberResponseDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthenticationResponseDTO>> loginJwt(@Validated @RequestBody LoginDTO loginDTO) {
        log.info("loginDTO {}", loginDTO);
        loginService.isValidCredentials(loginDTO);
        AuthenticationResponseDTO response = loginService.authenticate(loginDTO.getLoginId());

        return ResponseEntity.status(HttpStatus.OK)
                .body(BaseResponse.ofSuccess(BaseResponseCode.OK, response));
    }

    @GetMapping("/signup/check")
    public ResponseEntity<BaseResponse<CheckDuplicateUsernameResponseDTO>> checkDuplicateUsername(
            @RequestBody CheckDuplicateUsernameRequestDTO checkDuplicateUsernameRequestDTO){
        CheckDuplicateUsernameResponseDTO checkDuplicateUsernameResponseDTO = memberManagementService.checkDuplicateUsername(checkDuplicateUsernameRequestDTO);

        return ResponseEntity.ok(BaseResponse.ofSuccess(checkDuplicateUsernameResponseDTO));
    }

    @PostMapping("/refresh")
    public ResponseEntity<BaseResponse<AuthenticationResponseDTO>> refreshAccessToken(@RequestHeader("refreshToken") String refreshToken) {
        AuthenticationResponseDTO response = loginService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(BaseResponse.ofSuccess(response));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/code")
    public ResponseEntity<BaseResponse<String>> getAdminInvitationCode(
        @RequestBody AdminInvitationCodeRequestDTO adminInvitationCodeRequestDTO
    ){
        String adminInvitationCode = invitationCodeService.issueInvitationCode(adminInvitationCodeRequestDTO.getMaxUses());
        return ResponseEntity.ok(BaseResponse.ofSuccess(BaseResponseCode.OK, adminInvitationCode));
    }
}
