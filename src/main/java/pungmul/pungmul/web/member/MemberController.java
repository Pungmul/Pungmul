package pungmul.pungmul.web.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pungmul.pungmul.service.member.CreateMemberService;
import pungmul.pungmul.web.member.dto.CreateAccountRequestDto;
import pungmul.pungmul.web.member.dto.CreateAccountResponseDto;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final CreateMemberService createMemberService;

    @PostMapping("/create")
    public ResponseEntity<CreateAccountResponseDto> createMember(@Validated @RequestBody CreateAccountRequestDto createAccountRequestDto) {
        CreateAccountResponseDto accountResponseDto = createMemberService.createMember(createAccountRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(accountResponseDto);
    }
}
