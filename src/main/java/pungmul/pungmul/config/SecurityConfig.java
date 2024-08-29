package pungmul.pungmul.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pungmul.pungmul.config.security.JsonUsernamePasswordAuthenticationFilter;
import pungmul.pungmul.config.security.JwtAuthenticationProvider;
import pungmul.pungmul.config.security.LogoutHandlerImpl;
import pungmul.pungmul.service.member.UserDetailsServiceImpl;
import pungmul.pungmul.service.member.loginvalidation.user.LoginUserArgumentResolver;

import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;
    private final UserDetailsServiceImpl userDetailsService;
    private final LogoutHandlerImpl logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // 새로운 람다 문법을 사용하여 CSRF 보호 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)     //  Http Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable)     //  폼 기반 로그인 방식 비활성화
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
//                        .requestMatchers("/member/signup", "/member/login").permitAll()
//                        .anyRequest().authenticated()
                        .anyRequest().permitAll()) // 모든 요청 허용. 인증 로직 설계 이후 변경
                .logout((logout) -> logout
                        .logoutUrl("/member/logout-jwt")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()))
                        .logoutSuccessUrl("/member/login")      //  로그아웃 후 해당 url로 이동
                        .invalidateHttpSession(true))           //  로그아웃 후 세션 삭제. 굳이 필요?
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //  세션 관리 정책. 세션 사용 안함
                );
        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_REP \n ROLE_REP > ROLE_USER \n ROLE_USER > ROLE_GUEST";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }


    @Bean
    public AuthenticationManager authenticationManager() {
        JwtAuthenticationProvider provider = new JwtAuthenticationProvider(userDetailsService, passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginUserArgumentResolver());
    }

    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")  // 로컬 호스트를 추가
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
                .allowCredentials(true);
    }
}
