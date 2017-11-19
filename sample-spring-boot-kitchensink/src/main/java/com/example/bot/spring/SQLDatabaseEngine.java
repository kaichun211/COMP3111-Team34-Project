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
	String InitializeNewUser(String userId) throws Exception {
		String result = null;
		int user_count = 0;
		boolean data_exist = false;
		Connection connection = getConnection();
		
		//Create data in user_info
		PreparedStatement stmt1 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)");
		stmt1.setString(1, userId);
		stmt1.executeUpdate();

		
		// Add data to coupontable
		PreparedStatement stmt2 = connection.prepareStatement("SELECT user_number from coupontable where user_id = 'master'");
		ResultSet rs = stmt2.executeQuery();
		if (rs.next()) {
		user_count = rs.getInt(1);
		}


		PreparedStatement stmt3 = connection.prepareStatement("SELECT * from coupontable where user_id = ?");
		stmt3.setString(1, userId);
		ResultSet rs2 = stmt3.executeQuery();
		if (rs2.next()) {
			data_exist = true;
		}
		if(!data_exist)
		{
		user_count++;
		PreparedStatement stmt4 = connection.prepareStatement("INSERT INTO coupontable VALUES (? , ?, false, 0)");
		stmt4.setString(1, userId);
		stmt4.setInt(2, user_count);
		stmt4.executeUpdate();
		
		//Update master user_count
		PreparedStatement stmt5 = connection.prepareStatement("UPDATE coupontable set user_number = ? where user_id = 'master'");
		stmt5.setInt(1, user_count);
		stmt5.executeUpdate();
		result = "Data initiallized! Welcome~";
		return result;
		}
		
		connection.close();
		result = "Data re-created! Unfortunately you are not a new user so you are not qualified for our new user event";
		return result;
	}
	
	String RemoveUser(String userId) throws Exception {
		String result = null;
		Connection connection = getConnection();
		PreparedStatement stmt = connection.prepareStatement("DELETE FROM user_info WHERE user_id= ?");
		stmt.setString(1, userId);
		ResultSet rs = stmt.executeQuery();
		connection.close();	
		result = "Data Deleted sucessfully!";
		return result;
	}
	
	String weight(String text, String userId) throws Exception {
		//Write your code here
		String result = null;
		String[] items;
		items = text.split("\\r?\\n");
		boolean data_exists = false;
		int weight = Integer.parseInt(items[1]);
		if(weight<=0)
		{
			result = "Weight can not be zero or negative! Please try again with a valid input";
			return result;
		}
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
			result = "Data updated! Your weight has been set to " + weight + "kg";
		}
			return result;
		
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
			result = "Data updated! ";
			return result;
		}
		else
		{
			result = "Account not exist!\n Please create an account using weight function.";
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
					results = "\n\nRemember to drink some water!";
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
		
		switch(items[2].toLowerCase()) {
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
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_1 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				case "energy_2":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_2 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				case "energy_3":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_3 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				case "energy_4":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_4 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				case "energy_5":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_5 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				case "energy_6":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_6 = ? where user_id = ?");
					stmt3.setString(1, userId);
					stmt3.setInt(2, energy);
					stmt3.setString(3, userId);
					stmt3.executeUpdate();
					break;
				}
				case "energy_7":{
					stmt3 = connection.prepareStatement("INSERT INTO user_info VALUES (? , 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0); UPDATE user_info set energy_7 = ? where user_id = ?");
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
	
	String friend(String userId) throws Exception {
		//Write your code here
		String result = null;
		int user_id = 0;
		
		boolean data_exists = false;
		
		Connection connection = getConnection();
		PreparedStatement stmt = connection.prepareStatement("SELECT user_number from coupontable where user_id = ?");
		stmt.setString(1, userId);
		ResultSet rs = stmt.executeQuery();
		
		if (rs.next()) {
			user_id = rs.getInt(1);
			result = Integer.toString(user_id);
			for(int i = result.length(); i < 6; i++) {
				result = "0" + result;
			}
			connection.close();
			return result;
		}else {
			result = "You are not qualified for this event! This event is only for new users.";
			connection.close();
			return result;
		}
	}
	
	String code(String text, String userId) throws Exception {
		
		String result = null;
		String[] items;
		items = text.split("\\r?\\n");
		if(items[1].length()!=6)
		{
			result = "Don't miss the zero(s)! Please try again.";
			return result;
		}
		int user_id = Integer.parseInt(items[1]);
		
		
		boolean data_exists = false;
		boolean code = true;
		int coupon_count = 0;
		
		Connection connection = getConnection();
		
		PreparedStatement stmt = connection.prepareStatement("SELECT user_number FROM coupontable where user_id = ?");
		stmt.setString(1, userId);
		ResultSet check_your_id = stmt.executeQuery();
		if(check_your_id.next())
		{
			if (check_your_id.getInt(1) == user_id)
			{
				result = "Hey! You can not refer yourself!";
				return result;
			}
		}
		
		PreparedStatement stmt1 = connection.prepareStatement("SELECT * FROM coupontable where user_number = ? and user_id not like 'master'");
		stmt1.setInt(1, user_id);
		ResultSet rs = stmt1.executeQuery();
		if (rs.next()) {
			data_exists = true;
		}
		PreparedStatement stmt2 = connection.prepareStatement("SELECT code FROM coupontable where user_id = ?");
		stmt2.setString(1, userId);
		ResultSet rs2 = stmt2.executeQuery();
		if (rs2.next()) {
			code = rs2.getBoolean(1);
			System.out.println("code false");
		}
		PreparedStatement stmt7 = connection.prepareStatement("SELECT coupon_count FROM coupontable where user_id = 'master'");
		ResultSet rs3 = stmt7.executeQuery();
		if (rs3.next()) {
			coupon_count = rs3.getInt(1);
		}
		
		if(!code && data_exists && coupon_count < 5000) {
			System.out.println("Updating");
			PreparedStatement stmt3 = connection.prepareStatement("UPDATE coupontable set code = true where user_id = ?");
			stmt3.setString(1, userId);
			stmt3.executeUpdate();
			
			PreparedStatement stmt4 = connection.prepareStatement("UPDATE coupontable set coupon_count = coupon_count + 1 where user_id = ?");
			stmt4.setString(1, userId);
			stmt4.executeUpdate();
			
			PreparedStatement stmt5 = connection.prepareStatement("UPDATE coupontable set coupon_count = coupon_count + 1 where user_number = ? and user_id not like 'master'");
			stmt5.setInt(1, user_id);
			stmt5.executeUpdate();
			
			PreparedStatement stmt6 = connection.prepareStatement("UPDATE coupontable set coupon_count = coupon_count + 2 where user_id = 'master'");
			stmt6.executeUpdate();
		
			result = "Coupon Get!";
			connection.close();
			return result;
		}else if(coupon_count >= 5000){
			result = "Sorry, the event has ended and all the coupons has been given out.";
			connection.close();
			return result;
		}else{
			result = "You are not qualified for this event! Either you are not a new user or you have already referred your friend.";
			connection.close();
			return result;
		}
	}
	
	String redeem(String userId) throws Exception {
		//Write your code here
		String result = null;
		int coupon_count = 0;
		boolean data_exists = false;
		
		Connection connection = getConnection();
		PreparedStatement stmt = connection.prepareStatement("SELECT coupon_count FROM coupontable where user_id = ?");
		stmt.setString(1, userId);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			coupon_count = rs.getInt(1);
		}
		if(coupon_count > 0) {
			PreparedStatement stmt2 = connection.prepareStatement("UPDATE coupontable set coupon_count = coupon_count - 1 where user_id = ?");
			stmt2.setString(1, userId);
			stmt2.executeUpdate();
			result = "You redeemed one coupon\nYou still have " + --coupon_count + " coupon(s) to be redeemed.";
			connection.close();
			return result;
		}else {
			result = "You currently have no coupon";
			connection.close();
			return result;
		}
		
	}
	
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
