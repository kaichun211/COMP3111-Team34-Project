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
	String order(String userID, String decision) throws Exception {
		
		
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
			state=1;
		break;}
		
		case 1: //meal menu
			{if (decision.toLowerCase().equals("exit")) state=1;
			else if (decision.equals("1")) {
				
			PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_meal_time= ? where (username='test');");
			change_choice.setString(1, "breakfast");
			change_choice.executeUpdate();
			change_choice.close();		
			state=11;}
			
			else if (decision.equals("2"))  {
				
			PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_meal_time= ? where (username='test');");
			change_choice.setString(1, "lunch");
			change_choice.executeUpdate();
			change_choice.close();		
			state=11;}
			else if (decision.equals("3"))  {
				
			PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_meal_time= ? where (username='test');");
			change_choice.setString(1, "dinner");
			change_choice.executeUpdate();
			change_choice.close();		
			state=11;}
			else if (decision.equals("4"))  {
				
			PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_meal_time= ? where (username='test');");
			change_choice.setString(1, "dessert");
			change_choice.executeUpdate();
			change_choice.close();		
			state=12;}
		break;}
		
		case 11:
			{if (decision.toLowerCase().equals("exit")) state=1;
			else if (decision.equals("1"))  {
			PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type= ? where (username='test');");
			change_choice.setString(1, "vegetarian");
			change_choice.executeUpdate();
			change_choice.close();		
			state=111;}
			
			else if (decision.equals("2")) {
				
			PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type= ? where (username='test');");
			change_choice.setString(1, "chicken");
			change_choice.executeUpdate();
			change_choice.close();		
			state=111;}
			else if (decision.equals("3"))  {
				
			PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type= ? where (username='test');");
			change_choice.setString(1, "pork");
			change_choice.executeUpdate();
			change_choice.close();		
			state=111;}
			
			else if (decision.equals("4")) {
				
			PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type= ? where (username='test');");
			change_choice.setString(1, "beef");
			change_choice.executeUpdate();
			change_choice.close();		
			state=111;}
			
			else if (decision.equals("5")) {
				
			PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type= ? where (username='test');");
			change_choice.setString(1, "nothing");
			change_choice.executeUpdate();
			change_choice.close();		
			state=111;}
			
			break;	}
			
		case 12:
			{if (decision.toLowerCase().equals("exit")) state=1;
			else if (decision.equals("1"))  {
				PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type= ? where (username='test');");
				change_choice.setString(1, "vegetarian");
				change_choice.executeUpdate();
				change_choice.close();		
				state=112;}
				
			else if (decision.equals("2")) {
				PreparedStatement change_choice = connection.prepareStatement("UPDATE users_choice SET choose_type= ? where (username='test');");
				change_choice.setString(1, "nothing");
				change_choice.executeUpdate();
				change_choice.close();		
				state=112;}
			break;
			}
			
		case 111: //choose dishes function	
			{if (decision.toLowerCase().equals("exit")) state=1;
			break;}
		case 112: //choose dessert function
			{if (decision.toLowerCase().equals("exit")) state=1;
			break;}
			


		default:{
			if (decision.toLowerCase().equals("exit")) state=1;
			break;
		}
		}
		
		

		
		
		//update state to user_info
		String change_state_statement="UPDATE users_info SET state="+Integer.toString(state)+" where username='test';";
		PreparedStatement change_state = connection.prepareStatement(change_state_statement);
		change_state.executeUpdate();
		change_state.close();


		
		String print_message="";
		
		switch (state) { //ouput message
        //features menu
		case 1: //meal menu, feature 1,4,8,9,10
		{print_message= "Meal menu"
				+ "\n1.Breakfast \n2.Lunch \n3.Dinner \n4. Dessert\n"; break;}
		
		case 11:{print_message= "What type of food do you like to choose?\n1.Vegetarian\n2.Chicken\n3.Pork\n4.Beef\n5.Don't care";	break;}	
		case 12: {print_message= "Are you vegetarian?\n1.yes\n2.no"; break;}
			
		case 111:{ //print dishes
			PreparedStatement get_users_final_choice = connection.prepareStatement("SELECT * FROM users_choice where (username='test');");
			ResultSet get_users_final_choice1=get_users_final_choice.executeQuery();
			get_users_final_choice1.next();
			String users_final_type=get_users_final_choice1.getString(4);
			String users_final_time=get_users_final_choice1.getString(5);
			get_users_final_choice1.close();
			get_users_final_choice.close();
			
			String statement="";
			
			PreparedStatement  get_dishes=null;
			if (users_final_type.equals("nothing")){
				
				get_dishes = connection.prepareStatement("SELECT * FROM meal_menu where (meal_time= ? ) ;");
				get_dishes.setString(1,users_final_time);
				//statement="SELECT * FROM meal_menu where ((choose_meal_time='"+users_final_time+"') and (username='test'));";
			}
			else {
			
				
				
				get_dishes = connection.prepareStatement("SELECT * FROM meal_menu where ((meal_time= ? ) and (type= ? ));");
				get_dishes.setString(1,users_final_time);
				get_dishes.setString(2,users_final_type);
				//statement="SELECT * FROM meal_menu where ((choose_meal_time='"+users_final_time+"') and (choose_type='"+users_final_type+"') and (username='test'));";
			}

			
	//		PreparedStatement get_dishes = connection.prepareStatement(statement);
			ResultSet get_dishes1=get_dishes.executeQuery();
			int count_dishes=1;
			
			while (get_dishes1.next()){
				print_message=print_message+count_dishes+"."+get_dishes1.getString(1)+" "+get_dishes1.getInt(3)+"\n";	
				count_dishes++;
			}	break;
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
			statement="SELECT * FROM meal_menu where ((meal_time='dessert'));";
			}
			else {
			statement="SELECT * FROM meal_menu where ((meal_time='dessert') and (type='vegetarian'));";
			}
			PreparedStatement get_dishes=connection.prepareStatement(statement);
			ResultSet get_dishes1=get_dishes.executeQuery();
			int count_dishes=1;
			while (get_dishes1.next()){
				print_message=print_message+count_dishes+"."+get_dishes1.getString(1)+" "+get_dishes1.getInt(3)+"\n";	
				count_dishes++;
			}	break;
			
		}
		

			
			
		
		default:
			break;

		
		
		}	
		
		
		
		connection.close();
		return print_message;
		
		
		
		}catch(Exception e){
			System.out.println(e);
		}	
		return null;
	}
	
	
	
	
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
						resultbuilder.append(dishes[i] + ":\nWeight= " + weight_total + " (g)\nEnergy = " + energy_total + " (kcal)\nSodium =" + sodium_total + " (g)\nFatty Acids = " + fat_total + " (g)\n\n");
							
			}			
		}catch(Exception e){
			System.out.println(e);
		}
		result_set = resultbuilder.toString();
		return result_set;
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
