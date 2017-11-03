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
	String search(String text, String database, String userId) throws Exception {
try {	

				
	Connection connection = getConnection();
		
	//find state
	PreparedStatement find_state = connection.prepareStatement("SELECT state FROM users_info where username='test';");
	ResultSet find_state1=find_state.executeQuery();
	find_state1.next();
	int state=find_state1.getInt(1);
	find_state1.close();
	find_state.close();
	
	
	switch (state) { //change state
	case 0:
		if (text.equals("1")) state=1;
		else if (text.equals("2")) state=2;
		else if (text.equals("3")) state=3;
		else if (text.equals("4")) state=4;
	break;
	
	case 1: //meal menu
		if (text.toLowerCase().equals("exit")) state=0;
	break;
	
	case 2://show nutition details of each food
		if (text.toLowerCase().equals("exit")) state=0;
		
	break;
	
	case 3:
		
		if (text.toLowerCase().equals("exit")) state=0;
		break;
		
	case 4:
		if (text.toLowerCase().equals("exit")) state=0;
		break;
		
	default:
		if (text.toLowerCase().equals("exit")) state=0;
		break;
		
	
	}
	

	
	
	// feature 2 need push message function, overconsume nutrition warning
	switch (state) {
	case 0: //features menu
	
	
	String feature_menu="Welcome! What do you want to do now?\n"
			+ "1. Meal Menu\n2.Search nutition details of food\n"
			+ "3.Track taken energy\n4.Calculate exercise needed\n";
	return feature_menu;
	

	case 1: //meal menu, feature 1,4,8,9,10
	return "This is first function";
					
	case 2: //show nutition details of each food, feature 3
		return "This is second function";
		
		
		

	case 3://track taken energy, feature 6
		PreparedStatement track_energy = connection.prepareStatement("SELECT * FROM users_info where username='test';");
		ResultSet track_energy1=track_energy.executeQuery();
		track_energy1.next();
		float total_sodium_get=0;
		float total_kcal_get=0;
		float total_fatty_acids_get=0;
		int temp=5;
		for(int i=0;i<7;i++) {
		total_kcal_get+=track_energy1.getFloat(temp);
		total_sodium_get+=track_energy1.getFloat(temp+1);
		total_fatty_acids_get+=track_energy1.getFloat(temp+2);
		temp+=3;
		}
		track_energy1.close();
		
		
		
		
		String message="For past 7 days, you got:\nEnergy:"
				+total_kcal_get +"kcal\nsodium:"+total_sodium_get+" Na\nFatty Acids:"+total_fatty_acids_get;
		//maybe add more comment, something like you get too much sodium, etc....
		return message;	
		
		
		

	case 4://calculate exercises needed, feature 7
		return "Calculate exercises needed";
		
		
		
		

	
	
	}
	
	//update state to user_info
	PreparedStatement change_state = connection.prepareStatement("UPDATE users_info SET state=? where username='test';");
	change_state.setInt(1,state);
	change_state.close();
	connection.close();
	
	
	
	
	
	
	
	
	
	
	

				

			}catch(Exception e){

				System.out.println(e);

			}

		return null;

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