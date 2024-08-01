package com.bliutvikler.bliutvikler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;



@SpringBootApplication
public class BliutviklerV3Application {

	@Autowired
	private Environment environment;

	private String datasourceUrl;
	private String datasourceUsername;
	private String datasourcePassword;

	/*@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${spring.datasource.url}")
	private String datasourceUrl;

	@Value("${spring.datasource.username}")
	private String datasourceUsername;

	@Value("${MYSQLPORT}")
	private String mysqlPort;*/

	/*@PostConstruct
	public void logDatabaseProperties() {
		System.out.println("JWT secret URL: " + jwtSecret);
		System.out.println("Datasource URL: " + datasourceUrl);
		System.out.println("Datasource Username: " + datasourceUsername);
		// System.out.println("MYSQL Port: " + mysqlPort);
	}*/

	@PostConstruct
	public void logDatabaseProperties() {
		datasourceUrl = System.getenv("DB_URL");
		datasourceUsername = System.getenv("DB_USERNAME");
		datasourcePassword = System.getenv("DB_PASSWORD");

		System.out.println("Datasource URL: " + datasourceUrl);
		System.out.println("Datasource Username: " + datasourceUsername);
		System.out.println("Datasource Password: " + datasourcePassword);
	}

	@PostConstruct
	public void logEnvironmentVariables() {
		System.out.println("MYSQLHOST: " + System.getenv("MYSQLHOST"));
		System.out.println("MYSQLPORT: " + System.getenv("MYSQLPORT"));
		System.out.println("MYSQLDATABASE: " + System.getenv("MYSQLDATABASE"));
		System.out.println("MYSQLUSER: " + System.getenv("MYSQLUSER"));
	}

	public static void main(String[] args) {
		SpringApplication.run(BliutviklerV3Application.class, args);
	}
}
