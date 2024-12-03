package pungmul.pungmul.service.member.authentication;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.MessagingException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pungmul.pungmul.domain.member.auth.VerificationToken;
import pungmul.pungmul.domain.member.user.User;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.repository.member.repository.VerificationTokenRepository;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final JavaMailSender mailSender;

    @Value("${server.address}")
    private String serviceAddress;

    @Value("${server.port}")
    private String port;

    public void verificationEmail(Long userByUserId) {
        User user = userRepository.getUserByUserId(userByUserId)
                .orElseThrow(NoSuchElementException::new);
        String token = createVerificationToken(user);
        log.info("token : {}", token);
        sendVerificationEmail(user.getEmail(), token);
    }

    private String createVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);  // 24시간 후 만료
        VerificationToken verificationToken = VerificationToken.builder()
                .token(token)
                .userId(user.getId())
                .expiredAt(expiryDate)
                .build();
        verificationTokenRepository.saveVerificationToken(verificationToken);

        return token;
    }

    @Scheduled(cron = "0 0 0 * * ?")  // 매일 자정에 실행
    public void removeExpiredTokens() {
        verificationTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }


    public void sendVerificationEmail(String toEmail, String token) {
        String subject = "이메일 인증";
        String verificationLink = "http://" + serviceAddress + ":" + port +  "/verify?token=" + token;
        String content = "<p>아래 링크를 클릭하여 이메일을 인증하세요:</p>" +
                "<a href=\"" + verificationLink + "\">이메일 인증 링크</a>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(content, true);  // HTML 형식으로 메일 전송

            mailSender.send(message);
            System.out.println("이메일이 성공적으로 전송되었습니다!");
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        } catch (jakarta.mail.MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
