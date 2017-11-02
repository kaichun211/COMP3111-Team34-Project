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
	@Override
	String search(String text) throws Exception {
		//Write your code here
		Connection connection = getConnection();
		
		
		//find state
		PreparedStatement find_state = connection.prepareStatement("SELECT state FROM user_info where user=%s;","test");
		ResultSet find_state1=find_state.executeQuery();
		find_state1.next();
		int state=find_state1.getInterger(1);
		find_state1.close();
		find_state.close();
		
		
		switch (state) { //change state
		case 0:
			if text.equals("1") state=1;
			else if text.equals("2") state=2;
			else if text.equals("3") state=3;
			else if text.equals("4") state=4;
		break;
		
		case 1: //meal menu
			if text.toLowerCase().equals("exit") state=0;
		break;
		
		case 2://show nutition details of each food
			if text.toLowerCase().equals("exit") state=0;
			
		break;
		
		case 3:
			
			if text.toLowerCase().equals("exit") state=0;
			break;
			
		case 4:
			if text.toLowerCase().equals("exit") state=0;
			break;
			
		default:
			if text.toLowerCase().equals("exit") state=0;
			break;
			
		
		}
		

		
		
		// feature 2 need push message function, overconsume nutrition warning
		switch (state) {
		case 0: //features menu
		
		
		String feature_menu="Welcome! What do you want to do now?\n"
				+ "1. Meal Menu\n2.Search nutition details of food\n"
				+ "3.Track taken energy\n4.Calculate exercise needed\n";
		return feature_menu;
		
		break;
		case 1: //meal menu, feature 1,4,8,9,10
		String message="This is first function";
		return message;
			
		break;			
		case 2: //show nutition details of each food, feature 3
			String message="This is second function";
			return message;
			
			
		break;
		case 3://track taken energy, feature 6
			PreparedStatement track_energy = connection.prepareStatement("SELECT * FROM user_info where user=%s;","test");
			ResultSet track_energy1=track_energy.executeQuery();
			track_energy1.next();
			float total_sodium_get=0.0;
			float total_kcal_get=0.0;
			float total_fatty_acids=0.0;
			int temp=5;
			for(int i=0;i<7;i++) {
			total_kcal+=track_energy1.getFloat(temp);
			total sodium+=track_energy1.getFloat(temp+1);
			total_fatty_acids+=track_energy1.getFloat(temp+2);
			temp+=3;
			}
			track_energy1.close();
			
			
			
			
			String message="For past 7 days, you got:\nEnergy:"
					+total_kcal +"kcal\nsodium:"+total_sodium+" Na\nFatty Acids:"+total_fatty_acids;
			//maybe add more comment, something like you get too much sodium, etc....
			return message;	
			
			
			
		break;
		case 4://calculate exercises needed, feature 7
			String message="Calculate exercises needed";
			return message;
			
			
			
		break;
		
		default:	
		break;
		
		}
		
		//update state to user_info
		PreparedStatement change_state = connection.prepareStatement("UPDATE user_info SET state=%d where user=%s;",state,"test");
		change_state.close();
		
		connection.close();
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
/*	
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
			
			
		for(int i=0; i < items.length;i++) {
				Connection connection = getConnection();
				weight_avg = 0;
				energy_avg = 0;
				sodium_avg = 0;
				fat_avg = 0;
				result_count = 0;
				PreparedStatement stmt = connection.prepareStatement("SELECT * FROM nutrient_table WHERE description LIKE concat(?,'%')");
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
				rs.close();
				stmt.close();
				
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
				connection.close();  */
			}
			
		}catch(Exception e){
			System.out.println(e);
		}
		//resultbuilder.append("\n Total Weight = " + weight_total + " (g) \n Total Energy = " + energy_total + " (kcal) \n Total Sodium = " + sodium_total + " (g) \n Total Fat = " + fat_total + " (g)");
		
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
