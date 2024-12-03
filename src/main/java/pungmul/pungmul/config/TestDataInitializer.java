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
import pungmul.pungmul.dto.member.CreateMemberRequestDTO;
import pungmul.pungmul.repository.member.repository.UserRepository;
import pungmul.pungmul.service.file.ImageService;
import pungmul.pungmul.service.member.membermanagement.MemberManagementService;

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
    private final MemberManagementService memberManagementService;
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

            if (isPostDataUnderLimit()) {
                executeSqlFile("db/migration/test_postData_testV1.sql");
            } else {
                System.out.println("Post and Content data already sufficient, no need to insert post test data.");
            }

            if (isFriendDataUnderLimit()) {
                executeSqlFile("db/migration/balanced_friends_data_testV1.sql");
            } else {
                System.out.println("Friend data already sufficient, no need to insert friend test data.");
            }

            // 참여자 데이터 확인 및 SQL 실행
            if (isParticipantDataUnderLimit()) {
                executeSqlFile("db/migration/unique_lightning_meeting_participant.sql");
            } else {
                System.out.println("Participant data already sufficient, no need to insert participant test data.");
            }
        };
    }

    private void createAccountsFromJson() throws IOException {
        String jsonFilePath = "src/main/resources/db/migration/test_memberData.json";
        List<CreateMemberRequestDTO> memberRequests = objectMapper.readValue(
                Files.readAllBytes(Paths.get(jsonFilePath)),
                new TypeReference<List<CreateMemberRequestDTO>>() {}
        );

        for (CreateMemberRequestDTO request : memberRequests) {
            memberManagementService.createMember(request, null); // 프로필 이미지가 없으므로 null
        }

        System.out.println("All accounts from JSON file have been created.");
    }

    private boolean isPostDataUnderLimit() {
        Integer postCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM post", Integer.class);
        Integer contentCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM content", Integer.class);

        return postCount <= 10 && contentCount <= 10;
    }

    private boolean isUserDataUnderLimit() {
        Integer accountCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM account", Integer.class);
        return accountCount < 10;
    }

    private boolean isFriendDataUnderLimit() {
        Integer friendCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM friends", Integer.class);
        return friendCount < 20;
    }

    // 참여자 데이터가 40건 이하인지 확인하는 메서드
    private boolean isParticipantDataUnderLimit() {
        Integer participantCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM lightning_meeting_participant", Integer.class);
        return participantCount < 40;
    }

    private void executeSqlFile(String filePath) {
        try (Connection conn = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conn, new ClassPathResource(filePath));
            System.out.println("Executed SQL file: " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
