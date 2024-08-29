package pungmul.pungmul.web.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pungmul.pungmul.dto.admin.SetRoleRequestDTO;
import pungmul.pungmul.dto.admin.SetRoleResponseDTO;
import pungmul.pungmul.service.member.CreateMemberService;
import pungmul.pungmul.service.member.UserRoleService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final UserRoleService userRoleService;
    private final CreateMemberService createMemberService;

    @PostMapping("/role")
    public SetRoleResponseDTO addRole(@RequestBody SetRoleRequestDTO setRoleRequestDTO) {
        return userRoleService.addRoleToAccount(setRoleRequestDTO);
    }
}
