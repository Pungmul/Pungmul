package pungmul.pungmul;

import org.apache.catalina.webresources.StandardRoot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;


@SpringBootApplication
public class PungmulApplication {

	public static void main(String[] args) {
		SpringApplication.run(PungmulApplication.class, args);
	}

//	@Bean
//	public ServletWebServerFactory servletContainer() {
//		TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
//			@Override
//			protected void postProcessContext(Context context) {
//				// Context 내의 보안 설정 적용
//				context.setResources(new StandardRoot(context));
//			}
//		};
//
//		// HTTP -> HTTPS 리다이렉트 설정
//		tomcat.addAdditionalTomcatConnectors(httpToHttpsRedirectConnector());
//		return tomcat;
//	}
//
//	private Connector httpToHttpsRedirectConnector() {
//		Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
//		connector.setScheme("http");
//		connector.setPort(8080);
//		connector.setSecure(false);
//		connector.setRedirectPort(443);
//		return connector;
//	}
}
