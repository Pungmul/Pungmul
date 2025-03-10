package pungmul.pungmul.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info()
                        .title("JWT API")
                        .description("JWT 인증이 적용된 API 테스트 문서입니다.")
                        .version("1.0"));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    @Bean
    public GroupedOpenApi chatGroupedOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("CHAT") // group 설정 (API들을 그룹화시켜 그룹에 속한 API들만 확인할 수 있도록 도와줌)
                .pathsToMatch("/api/chat/**") // group에 포함될 API endpoint 경로
                .addOpenApiCustomizer(
                        openApi ->
                                openApi
                                        .setInfo(
                                                new Info()
                                                        .title("CHAT API") // API 제목
                                                        .description("채팅 관련 API입니다. stomp나 websocket은 질문하세요") // API 설명
                                                        .version("1.0.0") // API 버전
                                        )
                )
                .build();
    }
    @Bean
    public GroupedOpenApi friendsGroupedOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("FRIENDS") // group 설정 (API들을 그룹화시켜 그룹에 속한 API들만 확인할 수 있도록 도와줌)
                .pathsToMatch("/api/friends/**") // group에 포함될 API endpoint 경로
                .addOpenApiCustomizer(
                        openApi ->
                                openApi
                                        .setInfo(
                                                new Info()
                                                        .title("FRIENDS API") // API 제목
                                                        .description("친구 관련 API입니다. stomp나 websocket은 질문하세요") // API 설명
                                                        .version("1.0.0") // API 버전
                                        )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi lightningGroupedOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("LIGHTNING") // group 설정 (API들을 그룹화시켜 그룹에 속한 API들만 확인할 수 있도록 도와줌)
                .pathsToMatch("/api/lightning/**") // group에 포함될 API endpoint 경로
                .addOpenApiCustomizer(
                        openApi ->
                                openApi
                                        .setInfo(
                                                new Info()
                                                        .title("LIGHTNING API") // API 제목
                                                        .description("번개 관련 API입니다. stomp나 websocket은 질문하세요") // API 설명
                                                        .version("1.0.0") // API 버전
                                        )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi meetingGroupedOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("MEETING") // group 설정 (API들을 그룹화시켜 그룹에 속한 API들만 확인할 수 있도록 도와줌)
                .pathsToMatch("/api/meeting/**") // group에 포함될 API endpoint 경로
                .addOpenApiCustomizer(
                        openApi ->
                                openApi
                                        .setInfo(
                                                new Info()
                                                        .title("MEETING API") // API 제목
                                                        .description("미팅 관련 API입니다. stomp나 websocket은 질문하세요") // API 설명
                                                        .version("1.0.0") // API 버전
                                        )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi memberGroupedOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("MEMBER") // group 설정 (API들을 그룹화시켜 그룹에 속한 API들만 확인할 수 있도록 도와줌)
                .pathsToMatch("/api/member/**") // group에 포함될 API endpoint 경로
                .addOpenApiCustomizer(
                        openApi ->
                                openApi
                                        .setInfo(
                                                new Info()
                                                        .title("MEMBER API") // API 제목
                                                        .description("멤버 관련 API입니다. stomp나 websocket은 질문하세요") // API 설명
                                                        .version("1.0.0") // API 버전
                                        )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi messageGroupedOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("MESSAGE") // group 설정 (API들을 그룹화시켜 그룹에 속한 API들만 확인할 수 있도록 도와줌)
                .pathsToMatch("/api/message/**") // group에 포함될 API endpoint 경로
                .addOpenApiCustomizer(
                        openApi ->
                                openApi
                                        .setInfo(
                                                new Info()
                                                        .title("MESSAGE API") // API 제목
                                                        .description("메시지 관련 API입니다. stomp나 websocket은 질문하세요") // API 설명
                                                        .version("1.0.0") // API 버전
                                        )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi boardsGroupedOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("BOARDS") // group 설정 (API들을 그룹화시켜 그룹에 속한 API들만 확인할 수 있도록 도와줌)
                .pathsToMatch("/api/boards/**") // group에 포함될 API endpoint 경로
                .addOpenApiCustomizer(
                        openApi ->
                                openApi
                                        .setInfo(
                                                new Info()
                                                        .title("BOARD API") // API 제목
                                                        .description("게시판 관련 API입니다. stomp나 websocket은 질문하세요") // API 설명
                                                        .version("1.0.0") // API 버전
                                        )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi commentGroupedOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("COMMENT") // group 설정 (API들을 그룹화시켜 그룹에 속한 API들만 확인할 수 있도록 도와줌)
                .pathsToMatch("/api/comments/**") // group에 포함될 API endpoint 경로
                .addOpenApiCustomizer(
                        openApi ->
                                openApi
                                        .setInfo(
                                                new Info()
                                                        .title("COMMENT API") // API 제목
                                                        .description("댓글 관련 API입니다. stomp나 websocket은 질문하세요") // API 설명
                                                        .version("1.0.0") // API 버전
                                        )
                )
                .build();
    }

    @Bean
    public GroupedOpenApi postGroupedOpenApi() {
        return GroupedOpenApi
                .builder()
                .group("POSTS") // group 설정 (API들을 그룹화시켜 그룹에 속한 API들만 확인할 수 있도록 도와줌)
                .pathsToMatch("/api/posts/**") // group에 포함될 API endpoint 경로
                .addOpenApiCustomizer(
                        openApi ->
                                openApi
                                        .setInfo(
                                                new Info()
                                                        .title("POST API") // API 제목
                                                        .description("게시글 관련 API입니다. stomp나 websocket은 질문하세요") // API 설명
                                                        .version("1.0.0") // API 버전
                                        )
                )
                .build();
    }


}