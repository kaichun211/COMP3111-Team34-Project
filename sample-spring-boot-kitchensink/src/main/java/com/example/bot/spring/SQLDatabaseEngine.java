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
	//Search ingredients
	//@Override
	String[] nutrient_search(String text) throws Exception {
		//Write your code here
		String[] result_list = {};
		int result_count = 0;
		String result = null;
		
		//Extract dishes
		String[] dishes_string;
		Dishes[] dishes = {};
		dishes_string = text.split("/n");
		for (int i = 0; i<dishes_string.length; i++) {
			dishes[i] = new Dishes(dishes_string[i]);
		}
		
		float weight_total_meal = 0;
		int energy_total_meal = 0;
		int sodium_total_meal = 0;
		int fat_total_meal = 0;
		
		try {
			for(int j=0; j < dishes.length; j++) {
				
				StringBuilder resultbuilder = new StringBuilder();
				
				float weight_total_temp = 0;
				int energy_total_temp = 0;
				int sodium_total_temp = 0;
				int fat_total_temp = 0;
				
				for(int i=0; i < dishes[j].items.length;i++) {
					Connection connection = getConnection();
					
					float weight_temp = 0;
					int energy_temp = 0;
					int sodium_temp = 0;
					int fat_temp = 0;
					
					result_count = 0;
					PreparedStatement stmt = connection.prepareStatement("SELECT * FROM nutrient_table WHERE description LIKE concat(?,'%')");
					stmt.setString(1, dishes[j].items[i].name);
					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						result_count++;
						weight_temp += rs.getFloat(3);
						energy_temp += rs.getInt(5);
						sodium_temp += rs.getInt(6);
						fat_temp += rs.getInt(7);
					}
					if (result_count!=0)
					{
						weight_temp = weight_temp / result_count;
						energy_temp = energy_temp / result_count;
						sodium_temp = sodium_temp / result_count;
						fat_temp = fat_temp / result_count;
						
						resultbuilder.append(dishes[j].items[i] + ": \n Average Weight = " + weight_temp + " (g) \n Average Energy = " + energy_temp + " (kcal) \n Average Sodium = " + sodium_temp + " (g) \n Saturated Fat = " + fat_temp + " (g) \n");
					}
					dishes[j].items[i].setWeightAvg(weight_temp);
					dishes[j].items[i].setEnergyAvg(energy_temp);
					dishes[j].items[i].setSodiumAvg(sodium_temp);
					dishes[j].items[i].setFatAvg(fat_temp);
					
					weight_total_temp += weight_temp;
					energy_total_temp += energy_temp;
					sodium_total_temp += sodium_temp;
					fat_total_temp += fat_temp;
					
					rs.close();
					stmt.close();
					connection.close();
				}
				dishes[j].setWeightTotal(weight_total_temp);
				dishes[j].setEnergyTotal(energy_total_temp);
				dishes[j].setSodiumTotal(sodium_total_temp);
				dishes[j].setFatTotal(fat_total_temp);
				resultbuilder.append("\n For " + dishes[j].dishes_string + "\n Total Weight = " + weight_total_temp + " (g) \n Total Energy = " + energy_total_temp + " (kcal) \n Total Sodium = " + sodium_total_temp + " (g) \n Total Fat = " + fat_total_temp + " (g)");
				
				weight_total_meal += weight_total_temp;
				energy_total_meal += energy_total_temp;
				sodium_total_meal += sodium_total_temp;
				fat_total_meal += fat_total_temp;
				result_list[j] = resultbuilder.toString();
			}
			
		}catch(Exception e){
			System.out.println(e);
		}
		
		//resultbuilder.append("\n For this meal\n Total Weight = " + weight_total_meal + " (g) \n Total Energy = " + energy_total_meal + " (kcal) \n Total Sodium = " + sodium_total_meal + " (g) \n Total Fat = " + fat_total_meal + " (g)");
		//result = resultbuilder.toString();
		return result_list;
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
