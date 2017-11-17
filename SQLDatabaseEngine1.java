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
	{
		if (text.equals("1")) state=1;
		else if (text.equals("2")) state=2;
		else if (text.equals("3")) state=3;
		else if (text.equals("4")) state=4;
	break;}
	
	case 1: //meal menu
		{if (text.toLowerCase().equals("exit")) state=0;
		else if (text.equals("1")) {
			
		PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_meal_time=? where (username='test');");
		change_choice.setString(1, "breakfast");
		change_choice.close();		
		state=11;}
		
		else if (text.equals("2"))  {
			
		PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_meal_time=? where (username='test');");
		change_choice.setString(1, "lunch");
		change_choice.close();		
		state=11;}
		else if (text.equals("3"))  {
			
		PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_meal_time=? where (username='test');");
		change_choice.setString(1, "dinner");
		change_choice.close();		
		state=11;}
		else if (text.equals("4"))  {
			
		PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_meal_time=? where (username='test');");
		change_choice.setString(1, "dessert");
		change_choice.close();		
		state=12;}
	break;}
	
	case 11:
		{if (text.toLowerCase().equals("exit")) state=0;
		else if (text.equals("1"))  {
		PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type=? where (username='test');");
		change_choice.setString(1, "vegetarian");
		change_choice.close();		
		state=111;}
		
		else if (text.equals("2")) {
			
		PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type=? where (username='test');");
		change_choice.setString(1, "chicken");
		change_choice.close();		
		state=111;}
		else if (text.equals("3"))  {
			
		PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type=? where (username='test');");
		change_choice.setString(1, "pork");
		change_choice.close();		
		state=111;}
		
		else if (text.equals("4")) {
			
		PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type=? where (username='test');");
		change_choice.setString(1, "beef");
		change_choice.close();		
		state=111;}
		
		else if (text.equals("5")) {
			
		PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type=? where (username='test');");
		change_choice.setString(1, "nothing");
		change_choice.close();		
		state=111;}
		
		break;	}
		
	case 12:
		{if (text.toLowerCase().equals("exit")) state=0;
		else if (text.equals("1"))  {
			PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type=? where (username='test');");
			change_choice.setString(1, "vegetarian");
			change_choice.close();		
			state=112;}
			
		else if (text.equals("2")) {
			PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type=? where (username='test');");
			change_choice.setString(1, "nothing");
			change_choice.close();		
			state=112;}
		break;
		}
		
	case 111: //choose dishes function	
		{if (text.toLowerCase().equals("exit")) state=0;
		break;}
	case 112: //choose dessert function
		{if (text.toLowerCase().equals("exit")) state=0;
		break;}
		
	
	case 2://show nutition details of each food
		{if (text.toLowerCase().equals("exit")) state=0;
		
	break;
		}
	case 3:
	{
		if (text.toLowerCase().equals("exit")) state=0;
		break;
	}
	case 4:{
		if (text.toLowerCase().equals("exit")) state=0;
		break;
	}
	default:{
		if (text.toLowerCase().equals("exit")) state=0;
		break;
	}
	}
	

	//update state to user_info
	PreparedStatement change_state = connection.prepareStatement("UPDATE users_info SET state=? where username='test';");
	change_state.setInt(1,state);
	change_state.close();
	connection.close();
	
	
	
	// feature 2 need push message function, overconsume nutrition warning
	switch (state) {
	case 0: //features menu
	
	{
	String feature_menu="Welcome! What do you want to do now?\n"
			+ "1. Meal Menu\n2.Search nutition details of food\n"
			+ "3.Track taken energy\n4.Calculate exercise needed\n";
	return feature_menu;}
	

	case 1: //meal menu, feature 1,4,8,9,10
	{return "Meal menu\ntype exit can go back feature menu"
			+ "\n1.Breakfast \n2.Lunch \n3.Dinner \n4. Dessert\ntype exit to go back feature table";}
	
	case 11:{return "What type of food do you like to choose?\n1.Vegetarian\n2.Chicken\n3.Pork\n4.Beef\n5.Don't care\ntype exit to go back feature table";	}	
	case 12: {return "Are you vegetarian?\n1.yes\n2.no\ntype exit to go back feature table";}
		
	case 111:{ //print dishes
		PreparedStatement get_users_final_choice = connection.prepareStatement("SELECT * FROM users_choice where (username='test');");
		ResultSet get_users_final_choice1=get_users_final_choice.executeQuery();
		get_users_final_choice1.next();
		String users_final_type=get_users_final_choice1.getString(4);
		String users_final_time=get_users_final_choice1.getString(5);
		get_users_final_choice1.close();
		get_users_final_choice.close();
		
		String statement="";
		
		if (users_final_type.equals("nothing")){
			statement="SELECT * FROM meal_menu where ((choose_meal_time='"+users_final_time+"') and (username='test'));";
		}
		else {
		statement="SELECT * FROM meal_menu where ((choose_meal_time='"+users_final_time+"') and (choose_type='"+users_final_type+"') and (username='test'));";
		}
		PreparedStatement get_dishes = connection.prepareStatement(statement);
		ResultSet get_dishes1=get_dishes.executeQuery();
		int count_dishes=1;
		String print_menu_message="";
		while (get_dishes1.next()){
		print_menu_message=print_menu_message+count_dishes+"."+get_dishes1.getString(1)+" "+get_dishes1.getInt(3)+"\n";	
			count_dishes++;
		}	return print_menu_message;
	}
	
	
	case 112: {
		
		PreparedStatement get_users_final_choice = connection.prepareStatement("SELECT * FROM users_choice where (username='test');");
		ResultSet get_users_final_choice1=get_users_final_choice.executeQuery();
		get_users_final_choice1.next();
		String users_final_type=get_users_final_choice1.getString(4);
		String users_final_time=get_users_final_choice1.getString(5);
		get_users_final_choice1.close();
		get_users_final_choice.close();
		
		String statement="";
		
		
		if (users_final_type.equals("nothing")){
		statement="SELECT * FROM meal_menu where ((choose_meal_time='dessert') and (username='test'));";
		}
		else {
		statement="SELECT * FROM meal_menu where ((choose_meal_time='dessert') and (choose_type='vegetarian') and (username='test'));";
		}
		PreparedStatement get_dishes=connection.prepareStatement(statement);
		ResultSet get_dishes1=get_dishes.executeQuery();
		int count_dishes=1;
		String print_menu_message="";
		while (get_dishes1.next()){
		print_menu_message=print_menu_message+count_dishes+"."+get_dishes1.getString(1)+" "+get_dishes1.getInt(3)+"\n";	
			count_dishes++;
		}	return print_menu_message;
		
	}
	
	
	case 2: //show nutition details of each food, feature 3
		{return "This is second function\ntype exit can go back feature menu";}
		
		
		

	case 3://track taken energy, feature 6
		{PreparedStatement track_energy = connection.prepareStatement("SELECT * FROM users_info where username='test';");
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
		track_energy.close();
		track_energy1.close();
		
		
		
		
		String message="For past 7 days, you got:\nEnergy:"
				+total_kcal_get +"kcal\nsodium:"+total_sodium_get+" Na\nFatty Acids:"+total_fatty_acids_get;
		//maybe add more comment, something like you get too much sodium, etc....
		if (total_kcal_get>2400*7) message+="\n you get too much kcal!";
		if (total_kcal_get<1000*7) message+="\n you get too little kcal!";
		if (total_sodium_get>2300*7) message+="\n you get too much sodium!";
		
		return message;	
		
		
		}

	case 4://calculate exercises needed, feature 7
		{return "Calculate exercises needed\ntype exit can go back feature menu";
		
		
		}
	default:
		break;

	
	
	}
	

	
	
	
	
	
	
	
	
	
	
	

				

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
