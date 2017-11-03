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
import java.util.Date;
import java.util.Calendar;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	//Search ingredients
	//@Override
	String weight(String text, String userId) throws Exception {
		//Write your code here
		String result = null;
		String[] items;
		items = text.split("\\r?\\n");
		boolean data_exists = false;
		int weight = Integer.parseInt(items[1]);
		Connection connection = getConnection();
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user_info WHERE user_id = ?");
		stmt.setString(1, userId);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			data_exists = true;
		}
		rs.close();
		if(data_exists)
		{
			PreparedStatement stmt2 = connection.prepareStatement("UPDATE user_info set weight = ? where user_id = ?");
			stmt2.setInt(1, weight);
			stmt2.setString(2, userId);
			stmt2.executeUpdate();
			connection.close();
			result = "Data updated!";
			return result;
		}
		else
		{
			PreparedStatement stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , ?, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)");
			stmt3.setString(1, userId);
			stmt3.setInt(2, weight);
			stmt3.executeUpdate();
			connection.close();
			result = "Data added to our database!";
			return result;
		}

	}
	
	String waterInterval(String text, String userId) throws Exception {
		String result = null;
		String[] items;
		items = text.split("\\r?\\n");
		boolean data_exists = false;
		int interval = Integer.parseInt(items[1]) * 60000;
		Connection connection = getConnection();
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user_info WHERE user_id = ?");
		stmt.setString(1, userId);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			data_exists = true;
		}
		rs.close();
		if(data_exists)
		{
			PreparedStatement stmt2 = connection.prepareStatement("UPDATE user_info set water_int = ? where user_id = ?");
			stmt2.setInt(1, interval);
			stmt2.setString(2, userId);
			stmt2.executeUpdate();
			connection.close();
			result = "Data updated!";
			return result;
		}
		else
		{
			result = "Account not existed!\n Please create an account using weight function.";
			return result;
		}

	}
	String waterNotif(String userId) throws Exception {
		String results = "";
		
		try {
			Date curDT = new Date();
			Connection connection = getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT water_time, water_int FROM user_info WHERE user_id = ?");
			stmt.setString(1, userId);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				long old_time = rs.getLong("water_time");
				if (rs.getLong("water_int") <= 0) {
					rs.close();
					stmt.close();
					connection.close();
					return results;
				} else if (old_time == 0) {
					PreparedStatement stmtSave = connection.prepareStatement("UPDATE user_info SET water_time = ? WHERE user_id = ?");
					stmtSave.setLong(1, curDT.getTime());
					stmtSave.setString(2, userId);
					stmtSave.executeUpdate();
					stmtSave.close();
				}
				else if (curDT.getTime() > (old_time + rs.getLong("water_int"))) {
					results = "\nRemember to drink some water!";
					PreparedStatement stmtSave = connection.prepareStatement("UPDATE user_info SET water_time = ? WHERE user_id = ?");
					stmtSave.setLong(1, curDT.getTime());
					stmtSave.setString(2, userId);
					stmtSave.executeUpdate();
					stmtSave.close();
				}
			}
			rs.close();
			stmt.close();
			connection.close();
		} catch(Exception e) {
			System.out.println(e);
		}
		return results;
	}
	
	String menu_search(String text) throws Exception {
		String result_set;
		String[] dishes;
		dishes = text.split("\\r?\\n");
		StringBuilder resultbuilder = new StringBuilder();
		try {	
				for(int i=1; i < dishes.length;i++) {
					String[] ingredients = {};
					ingredients = dishes[i].split(" ");
					int weight_total = 0;
					int energy_total = 0;
					int sodium_total = 0;
					int fat_total = 0;
					for(int j=0; j < ingredients.length; j++)
					{
						int weight_avg = 0;
						int energy_avg = 0;
						int sodium_avg = 0;
						int fat_avg = 0;
						int result_count = 0;
						Connection connection = getConnection();
						PreparedStatement stmt = connection.prepareStatement("SELECT * FROM nutrient_table WHERE description like concat( ?, '%')");
						stmt.setString(1, ingredients[j]);
						ResultSet rs = stmt.executeQuery();
						while (rs.next()) {
							result_count++;
							weight_avg += rs.getInt(3);
							energy_avg += rs.getInt(5);
							sodium_avg += rs.getInt(6);
							fat_avg += rs.getInt(7);
							//resultbuilder.append(rs.g(2));
						}
						
						if (result_count>0)
						{
						weight_avg = weight_avg / result_count;
						energy_avg = energy_avg / result_count;
						sodium_avg = sodium_avg / result_count;
						fat_avg = fat_avg / result_count;
						
						weight_total += weight_avg;
						energy_total += energy_avg;
						sodium_total += sodium_avg;
						fat_total += fat_avg;
						
						//resultbuilder.append(ingredients[j] + ": \n Average Weight = " + weight_avg + " (g) \n Average Energy = " + energy_avg + " (kcal) \n Average Sodium = " + sodium_avg + " (g) \n Saturated Fat = " + fat_avg + " (g) \n \n");
						}
						rs.close();
						stmt.close();
						connection.close();
						}
						resultbuilder.append(dishes[i] + ":\nWeight = " + weight_total + " (g)\nEnergy = " + energy_total + " (kcal)\nSodium = " + sodium_total + " (g)\nFatty Acids = " + fat_total + " (g)\n\n");
							
			}			
		}catch(Exception e){
			System.out.println(e);
		}
		result_set = resultbuilder.toString();
		return result_set;
	}
	String sports_amount(String userId) throws Exception {
		//Write your code here
		String result = null;
		float weight = 0;
		int energy = 0;
		int light_multiplier = 1;
		int medium_multiplier = 4;
		int heavy_multiplier = 8;
		Connection connection = getConnection();
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user_info WHERE user_id = ?");
		stmt.setString(1, userId);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			weight = rs.getInt(2);
			energy = rs.getInt(3) + rs.getInt(4) + rs.getInt(5) + rs.getInt(6) + rs.getInt(7) + rs.getInt(8) + rs.getInt(9); 			
		}
		rs.close();
		if(weight!=0)
		{
			result = "Total energy is : " + energy + " kcal.\nyour weight is : " + weight + "kg.\n\nTime required to consume:\nLight(e.g. walking) : " + energy/(weight*light_multiplier) + " hr\nMedium(e.g. jogging) : " + energy/(weight*medium_multiplier) + " hr\nHeavy(e.g. running, swimming) : " + energy/(weight*heavy_multiplier) + " hr\n"  ;
			return result;
		}
		else
		{
			result = "Weight can not be zero!";
			return result;
		}

	}
	
	
	String energy(String text, String userId) throws Exception {
		//Write your code here
		String result = null;
		String[] items = new String[4];
		int weekday_time = 0;
		String energy_X; 
		items = text.split("\\r?\\n");
		boolean data_exists = false;
		int energy = Integer.parseInt(items[1]);
		System.out.println("Test:Set energy");
		System.out.println(items[2]);
		
		switch(items[2]) {
			case "sun":{
				weekday_time = 1;
				energy_X = "energy_1";
				break;
			}
			case "mon":{
				weekday_time = 2;
				energy_X = "energy_2";
				break;
			}
			case "tue":{
				weekday_time = 3;
				energy_X = "energy_3";
				break;
			}
			case "wed":{
				weekday_time = 4;
				energy_X = "energy_4";
				break;
			}
			case "thu":{
				weekday_time = 5;
				energy_X = "energy_5";
				break;
			}
			case "fri":{
				weekday_time = 6;
				energy_X = "energy_6";
				break;
			}
			case "sat":{
				weekday_time = 7;
				energy_X = "energy_7";
				break;
			}
			default:{
				Calendar c = Calendar.getInstance();
				//Date d = c.getTime();
				//c.setTime(d);
				weekday_time = c.get(Calendar.DAY_OF_WEEK);
				energy_X = "energy_" + weekday_time;
				System.out.println("Test: " + weekday_time);
				System.out.println("Test: " + energy_X);
			}
		}
		System.out.println(weekday_time);
		System.out.println(userId);
		
		Connection connection = getConnection();
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM user_info WHERE user_id = ?");
		stmt.setString(1, userId);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			data_exists = true;
			System.out.println("Test: User ID Exist");
		}else {
			System.out.println("Test: User ID not Exist");
		}
		rs.close();
		if(data_exists)
		{
			PreparedStatement stmt2;
			switch(energy_X) {
				case "energy_1":{
					stmt2 = connection.prepareStatement("UPDATE user_info set energy_1 = ? where user_id = ?");
					stmt2.setInt(1, energy);
					stmt2.setString(2, userId);
					stmt2.executeUpdate();
					break;
				}
				case "energy_2":{
					stmt2 = connection.prepareStatement("UPDATE user_info set energy_2 = ? where user_id = ?");
					stmt2.setInt(1, energy);
					stmt2.setString(2, userId);
					stmt2.executeUpdate();
					break;
				}
				case "energy_3":{
					stmt2 = connection.prepareStatement("UPDATE user_info set energy_3 = ? where user_id = ?");
					stmt2.setInt(1, energy);
					stmt2.setString(2, userId);
					stmt2.executeUpdate();
					break;
				}
				case "energy_4":{
					stmt2 = connection.prepareStatement("UPDATE user_info set energy_4 = ? where user_id = ?");
					stmt2.setInt(1, energy);
					stmt2.setString(2, userId);
					stmt2.executeUpdate();
					break;
				}
				case "energy_5":{
					stmt2 = connection.prepareStatement("UPDATE user_info set energy_5 = ? where user_id = ?");
					stmt2.setInt(1, energy);
					stmt2.setString(2, userId);
					stmt2.executeUpdate();
					break;
				}
				case "energy_6":{
					stmt2 = connection.prepareStatement("UPDATE user_info set energy_6 = ? where user_id = ?");
					stmt2.setInt(1, energy);
					stmt2.setString(2, userId);
					stmt2.executeUpdate();
					break;
				}
				case "energy_7":{
					stmt2 = connection.prepareStatement("UPDATE user_info set energy_7 = ? where user_id = ?");
					stmt2.setInt(1, energy);
					stmt2.setString(2, userId);
					stmt2.executeUpdate();
					break;
				}
				default:{
					break;
				}
			}
			connection.close();
			result = "Data updated!";
			return result;
		}
		else
		{
			PreparedStatement stmt3;
			switch(energy_X) {
				case "energy_1":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_1 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				case "energy_2":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_2 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				case "energy_3":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_3 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				case "energy_4":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_4 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				case "energy_5":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_5 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				case "energy_6":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_6 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				case "energy_7":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_7 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				default:{
					break;
				}
			}
			
			connection.close();
			result = "Data added to our database!";
			return result;
		}

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
