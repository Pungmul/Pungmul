package pungmul.pungmul.core.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ParameterStoreCheck implements CommandLineRunner {

    @Value("${AWS_ACCESS_KEY_ID}")
    private String awsAccessKeyId;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Override
    public void run(String... args) throws Exception {
        log.info("AWS Access Key ID: {}", awsAccessKeyId);
        log.info("JWT Secret: {}", jwtSecret);

    }
}
