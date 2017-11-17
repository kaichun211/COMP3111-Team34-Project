package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		//Write your code here
		String result = null;
		try {
			Connection connection = getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT response FROM wordlist WHERE keyword LIKE concat('%',?,'%');");
			stmt.setString(1, text);
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				result = rs.getString("response");
			}
			
			rs.close();
			stmt.close();
			connection.close();
		}catch(Exception e){
			System.out.println(e);
		}
		return result;
	}
	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));
		
		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
		
		/**
		String username = "lpusgvdhytsqkq";
		String password = "c3966278efed0596daffc2797cb6036195093d6abf26085bfcf9f683b5a8da66";
		String dbUrl = "postgres://lpusgvdhytsqkq:c3966278efed0596daffc2797cb6036195093d6abf26085bfcf9f683b5a8da66@ec2-54-225-88-199.compute-1.amazonaws.com:5432/d42hhp8arkdooa";
		**/
		
		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
