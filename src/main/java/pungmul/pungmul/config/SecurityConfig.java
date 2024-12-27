package pungmul.pungmul.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pungmul.pungmul.config.security.JwtAuthenticationFilter;
import pungmul.pungmul.config.security.JwtAuthenticationProvider;
import pungmul.pungmul.service.member.authorization.UserDetailsServiceImpl;
import pungmul.pungmul.service.member.authentication.user.LoginUserArgumentResolver;

import javax.naming.AuthenticationException;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig implements WebMvcConfigurer {

    @Value("${app.url}")
    private String appUrl;

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())   // 새로운 람다 문법을 사용하여 CSRF 보호 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)     //  Http Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable)     //  폼 기반 로그인 방식 비활성화
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/member/clubs","/member/signup", "/member/login","/member/kakao/**","/ws/**").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //  세션 관리 정책. 세션 사용 안함
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // 필터 순서 조정

//        http.setSharedObject(AuthenticationEntryPoint.class, customAuthenticationEntryPoint());

        return http.build();
    }
//    private AuthenticationEntryPoint customAuthenticationEntryPoint() {
//        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
//            try {
//                throw authException; // Spring Security의 AuthenticationException을 직접 던짐
//            } catch (AuthenticationException e) {
//                throw new RuntimeException(e);
//            }
//        };
//    }

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
                .allowedOrigins(appUrl)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
                .allowCredentials(true);
    }


}
