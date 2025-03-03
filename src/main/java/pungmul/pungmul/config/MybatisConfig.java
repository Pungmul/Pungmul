package pungmul.pungmul.config;

import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.ConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class MybatisConfig {

    @Bean
    public PageInterceptor pageInterceptor() {
        PageInterceptor pageInterceptor = new PageInterceptor();
        Properties properties = new Properties();
        properties.setProperty("helperDialect", "mysql");  // 데이터베이스에 맞게 설정 (ex. MySQL)
        properties.setProperty("reasonable", "true");       // 페이지 번호가 0보다 작거나 크면 자동 조정
        properties.setProperty("supportMethodsArguments", "true"); // 메서드 매개변수 지원
        properties.setProperty("params", "count=countSql"); // 카운트 쿼리 자동 수행
        pageInterceptor.setProperties(properties);
        return pageInterceptor;
    }

}
