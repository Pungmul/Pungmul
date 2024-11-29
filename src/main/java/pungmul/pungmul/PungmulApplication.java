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

}
