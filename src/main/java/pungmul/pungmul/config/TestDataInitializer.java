package pungmul.pungmul.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pungmul.pungmul.domain.file.DomainType;
import pungmul.pungmul.dto.file.RequestImageDTO;
import pungmul.pungmul.dto.member.CreateMemberRequestDTO;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.service.file.ImageService;
import pungmul.pungmul.service.member.CreateMemberService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;

@Component
@Profile("test")
@RequiredArgsConstructor
public class TestDataInitializer {
    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final CreateMemberService createMemberService;
    private final ObjectMapper objectMapper;  // JSON 데이터를 읽어오기 위한 ObjectMapper
    private final ImageService imageService;
    private final UserRepository userRepository;

    @Bean
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public ApplicationRunner initializer() {
        return args -> {
            if (isUserDataUnderLimit()) {
                try {
                    createAccountsFromJson();
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to load member data from JSON", e);
                }
            }

            // Post 및 Content 데이터 확인 후 SQL 실행
            if (isPostDataUnderLimit()) {
                executeSqlFile("db/migration/test_postData_testV1.sql");
            } else {
                System.out.println("Post and Content data already sufficient, no need to insert post test data.");
            }
        };
    }

    private void createAccountsFromJson() throws IOException {
        // JSON 파일 경로 설정
        String jsonFilePath = "src/main/resources/db/migration/test_memberData.json";

        // JSON 파일을 읽어서 CreateMemberRequestDTO 리스트로 변환
        List<CreateMemberRequestDTO> memberRequests = objectMapper.readValue(
                Files.readAllBytes(Paths.get(jsonFilePath)),
                new TypeReference<List<CreateMemberRequestDTO>>() {}
        );

        // 회원가입 API를 호출하여 회원 생성
        for (CreateMemberRequestDTO request : memberRequests) {
            createMemberService.createMember(request, null);  // 프로필 이미지가 없으므로 null
        }

        System.out.println("All accounts from JSON file have been created.");
    }

    // Post와 Content 데이터가 10개 이하인지를 확인하는 메서드
    private boolean isPostDataUnderLimit() {
        Integer postCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post", Integer.class);
        Integer contentCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM content", Integer.class);

        return postCount <= 10 && contentCount <= 10;  // 두 테이블 모두 10개 이하일 때만 true 반환
    }

    private boolean isUserDataUnderLimit() {
        Integer accountCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM account", Integer.class);

        return accountCount < 10;  // 두 테이블 모두 10개 이하일 때만 true 반환
    }
    // 주어진 경로의 SQL 파일을 실행하는 메서드
    private void executeSqlFile(String filePath) {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(filePath));
            System.out.println("Executed SQL file: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
