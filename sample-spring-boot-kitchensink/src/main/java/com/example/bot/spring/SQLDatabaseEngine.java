package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.util.List;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		//Write your code here
		String[] items;
		int result_count = 0;
		int[] sodium_per_measure;
		int[] fat;
		items = text.split(" ");
		StringBuilder resultbuilder = new StringBuilder();
		float weight_avg = 0;
		int energy_avg = 0;
		String result = null;
		try {
			
			Connection connection = getConnection();
			for(int i=0; i < items.length;i++) {
				PreparedStatement stmt = connection.prepareStatement("SELECT * FROM nutrient_table WHERE description LIKE concat('%',?,'%');");
				stmt.setString(1, items[i]);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					result_count++;
					weight_avg += rs.getFloat(3);
					energy_avg += rs.getInt(5);
					//resultbuilder.append(rs.g(2));
				}
				if (result_count!=0)
				{
				weight_avg = weight_avg / result_count;
				energy_avg = energy_avg / result_count;
				}
				rs.close();
				stmt.close();
			}
			connection.close();
		}catch(Exception e){
			System.out.println(e);
		}
		result = "Average Weight = " + weight_avg + " (g) Average Energy = " + energy_avg + " (kcal)"; 
		return result;
	}

	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
