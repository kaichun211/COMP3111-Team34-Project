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
		items = text.split(" ");
		StringBuilder resultbuilder = new StringBuilder();
		float weight_avg = 0;
		int energy_avg = 0;
		int sodium_avg = 0;
		int fat_avg = 0;
		float weight_total = 0;
		int energy_total = 0;
		int sodium_total = 0;
		int fat_total = 0;
		String result = null;
		try {
			
			Connection connection = getConnection();
			for(int i=0; i < items.length;i++) {
				weight_avg = 0;
				energy_avg = 0;
				sodium_avg = 0;
				fat_avg = 0;
				result_count = 0;
				PreparedStatement stmt = connection.prepareStatement("SELECT * FROM nutrient_table WHERE description LIKE concat(?,'%');");
				stmt.setString(1, items[i]);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()) {
					result_count++;
					weight_avg += rs.getFloat(3);
					energy_avg += rs.getInt(5);
					sodium_avg += rs.getInt(6);
					fat_avg += rs.getInt(7);
					//resultbuilder.append(rs.g(2));
				}

				if (result_count!=0)
				{
				weight_avg = weight_avg / result_count;
				energy_avg = energy_avg / result_count;
				sodium_avg = sodium_avg / result_count;
				fat_avg = fat_avg / result_count;
				
				weight_total += weight_avg;
				energy_total += energy_avg;
				sodium_total += sodium_avg;
				fat_total += fat_avg;
				
				resultbuilder.append(items[i] + ": \n Average Weight = " + weight_avg + " (g) \n Average Energy = " + energy_avg + " (kcal) \n Average Sodium = " + sodium_avg + " (g) \n Saturated Fat = " + fat_avg + " (g) \n");
				}
				
				rs.close();
				stmt.close();
			}
			connection.close();
		}catch(Exception e){
			System.out.println(e);
		}
		resultbuilder.append("\n Total Weight = " + weight_total + " (g) \n Total Energy = " + energy_total + " (kcal) \n Total Sodium = " + sodium_total + " (g) \n Total Fat = " + fat_total + " (g)");
		result = resultbuilder.toString();
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
