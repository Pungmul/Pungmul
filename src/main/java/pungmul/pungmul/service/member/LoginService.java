package pungmul.pungmul.service.member;

import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.member.Account;
import pungmul.pungmul.domain.member.SessionUser;
import pungmul.pungmul.repository.member.login.LoginRepository;
import pungmul.pungmul.web.member.dto.LoginDTO;

import javax.naming.AuthenticationException;

@Service
public class LoginService {
//    private final LoginRepository loginRepository;
//
//    public SessionUser login(LoginDTO loginDto) throws AuthenticationException {
//        Account loginAccount = loginRepository.findByLoginId(loginDto.getLoginId())
//                .filter(account -> account.getPassword().equals(loginDto.getPassword()))
//                .orElseThrow(() -> new AuthenticationException("로그인 실패"));
//
//        return getSessionUser(loginAccount);
//
//    }

//    private SessionUser getSessionUser(Account loginAccount) {
//
//        SessionUser.builder()
//                .accountId(loginAccount.getId())
//                .username()
//    }
}
