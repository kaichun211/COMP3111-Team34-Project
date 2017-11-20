package com.example.bot.spring;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.MessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.LineBotMessages;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import com.example.bot.spring.DatabaseEngine;
import com.example.bot.spring.SQLDatabaseEngine;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = { KitchenSinkTester.class, SQLDatabaseEngine.class})
public class KitchenSinkTester {
	@Autowired
	//private DatabaseEngine databaseEngine;
	private SQLDatabaseEngine sqldatabaseEngine;
	
	
	//Water------------------------------
	@Test
	public void testWaterInvalidInput() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.waterInterval("water", "testID");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(false);
	}
	
/*	
	@Test
	public void testWaterValidInputBoundary() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.waterInterval("water" + "\n" + "1", "testID");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Data updated! ");
		TimeUnit.SECONDS.sleep(60);
		try {
			result = this.sqldatabaseEngine.friend("testID");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("Remember"));
	} 
	
	@Test
	public void testWaterInvalidInputNegative() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.waterInterval("water" + "\n" + "-10", "testID");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Interval can't be negative!");
	}
	@Test
	public void testWaterValidInput50() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.waterInterval("water" + "\n" + "50", "test2");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Data updated! ");
	
	}

//-------------Friend
	@Test
	public void testFriendUserExists() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.friend("testID");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("123456");
	}
	
	@Test
	public void testFriendUserNotExist() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.friend("notexistuser");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("You are not qualified for this event! This event is only for new users.");
	}
	
//-------------Code	
	
		@Test
	public void testCodeInvalidLengthNumber() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.code("code"+"\n"+"12345","U250bca48655aa67f697c1b99b5d2828b");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Don't miss the zero(s)! Please try again.");
	}
	
	@Test
	public void testCodeInvalidLengthString() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.code("code"+"\n"+"ABCDE","U250bca48655aa67f697c1b99b5d2828b");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(false);
		
	}
	
	@Test
	public void testCodeInvalidCode() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.code("code"+"\n"+"789789","U250bca48655aa67f697c1b99b5d2828b");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("There is no user associated with the code! Please ask your friend and get the correct one.");
	}
	
	
	@Test
	public void testCodeInputOwnCode() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.code("code"+"\n"+"123456","testID");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Hey! You can not refer yourself!");
	}
	
	@Test
	public void testCodeReferredAlready() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.code("code"+"\n"+"000001","U250bca48655aa67f697c1b99b5d2828b");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("You are not qualified for this event! Either you are not a new user or you have already referred your friend.");
	}
	
	@Test
	public void testCodeReferSuccess() throws Exception {
		boolean thrown = false;
		String result = null;
		this.sqldatabaseEngine.InitiallizeTestData("update coupontable set code = false where user_id = 'testID'");
		try {
			result = this.sqldatabaseEngine.code("code"+"\n"+"000001","testID");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Both you and your friend has got a coupon! You may redeem it using 'redeem'.");
	}
	
	@Test
	public void testCodePromotionEnds() throws Exception {
		boolean thrown = false;
		String result = null;
		int current_coupon_count = this.sqldatabaseEngine.GetCouponCount("select coupon_count from coupontable where user_id = 'master'");
		
		//set user can use code function and total coupon count = 5000
		this.sqldatabaseEngine.InitiallizeTestData("update coupontable set code = false where user_id = 'testID'");
		this.sqldatabaseEngine.InitiallizeTestData("update coupontable set coupon_count = 5000 where user_id = 'master'");
		try {
			result = this.sqldatabaseEngine.code("code"+"\n"+"000001","testID");
		} catch (Exception e) {
			thrown = true;
		}
		this.sqldatabaseEngine.InitiallizeTestData("update coupontable set coupon_count = " + current_coupon_count + " where user_id = 'master'");
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Sorry, the event has ended and all the coupons has been given out.");
	}
	
//----------------------Redeem	
	@Test
	public void testRedeemNoRedeem() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.redeem("no_coupon_user");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("You currently have no coupon");
	}
	
	@Test
	public void testRedeemCanRedeem() throws Exception {
		boolean thrown = false;
		String result = null;
		this.sqldatabaseEngine.InitiallizeTestData("update coupontable set coupon_count = 10 where user_id = 'testID'");
		
		try {
			result = this.sqldatabaseEngine.redeem("testID");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("You redeemed one coupon")).isEqualTo(true);
	}
	//------------------Weight		
		@Test
		public void testSportsWeightNonZero() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.sports_amount("testID");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result.contains("Total energy intake(for the last 7 days) is")).isEqualTo(true);
		}  
		
		@Test
		public void testSportsWeightInvalidWeight() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.sports_amount("NegativeWeightUser");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Your Weight is invalid! Please set your weight first");
		}  
	//-------------------Info
		//-----------Sex
		@Test
		public void testSexClickButton() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.sex("M", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Data updated! Your sex has been set to M");
		}  
		
		@Test
		public void testSexTypeGender() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.sex("F", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Data updated! Your sex has been set to F");
		} 
		
		@Test
		public void testSexTypeInvalidInput() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.sex("NotM/F", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Your Sex is invalid! Please try again using the info function.");
		} 
		//-----------Age
		@Test
		public void testAgeInputZero() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.age("0", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Age can not be zero or negative! Please use the info function again and enter a valid input");
		}
		
		@Test
		public void testAgeInputNegative() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.age("-20", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Age can not be zero or negative! Please use the info function again and enter a valid input");
		}
		
		@Test
		public void testAgeValidInputExistingUser() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.age("50", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Data updated! Your age has been set to 50");
		}
		
		@Test
		public void testAgeInputBoundary() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.age("1", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Data updated! Your age has been set to 1");

		}
		
		@Test
		public void testAgeInputNotInteger() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.age("ABC", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(false);

		} 
		
		//-----------Height
		@Test
		public void testHeightInputZero() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.height("0", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Height can not be zero or negative! Please use the info function again and enter a valid input");
		}
		
		@Test
		public void testHeightInputNegative() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.height("-20", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Height can not be zero or negative! Please use the info function again and enter a valid input");
		}
		
		@Test
		public void testHeightValidInputExistingUser() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.height("50", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Data updated! Your height has been set to 50cm");
		}
		
		@Test
		public void testHeightInputBoundary() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.height("1", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Data updated! Your height has been set to 1cm");

		}
		
		@Test
		public void testHeightInputNotInteger() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.height("ABC", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(false);

		} 
		
		//-----------Weight
		@Test
		public void testWeightInputZero() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.weight("0", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Weight can not be zero or negative! Please use the info function again and enter a valid input");
		}
		
		@Test
		public void testWeightInputNegative() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.weight("-30", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Weight can not be zero or negative! Please use the info function again and enter a valid input");
		}
		
		
		@Test
		public void testWeightValidInputExistingUser() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.weight("50", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Data updated! Your weight has been set to 50kg");
		}
		
		@Test
		public void testWeightInputBoundary() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.weight("1", "testID");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Data updated! Your weight has been set to 1kg");

		}
		
		@Test
		public void testWeightInputNotInteger() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.weight("ABC", "testID");
			} catch (Exception e) {
					thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(false);
			}
	
*/		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//--------------------Energy	
	/*
	@Test
	public void testEnergyInvalidInput1() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.energy("energy"+"\n"+"a"+"\n"+"1","testID");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(false);
	} 

	@Test
	public void testEnergyValidInputExistingUser() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.energy("energy"+"\n"+"100"+"\n"+"1","testID");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Data updated!");
	}
	

	@Test
	public void testOrderCorrectOutput1() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Meal menu"
				+ "\n1.Breakfast \n2.Lunch \n3.Dinner \n4. Dessert\n");
	}
	
	@Test
	public void testOrderCorrectOutput2() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
			result = this.sqldatabaseEngine.order("testID","1");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("What type of food do you like to choose?\n1.Vegetarian\n2.Chicken\n3.Pork\n4.Beef\n5.Don't care");
	}
	
	@Test
	public void testOrderCorrectOutput3() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
			result = this.sqldatabaseEngine.order("testID","2");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("What type of food do you like to choose?\n1.Vegetarian\n2.Chicken\n3.Pork\n4.Beef\n5.Don't care");
	}
	
	@Test
	public void testOrderCorrectOutput4() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
			result = this.sqldatabaseEngine.order("testID","3");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("What type of food do you like to choose?\n1.Vegetarian\n2.Chicken\n3.Pork\n4.Beef\n5.Don't care");
	}
	
	@Test
	public void testOrderCorrectOutput5() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
			result = this.sqldatabaseEngine.order("testID","4");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Are you vegetarian?\n1.yes\n2.no");
	}
	
	@Test
	public void testOrderCorrectOutputWithInvalidDecision1() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
			result = this.sqldatabaseEngine.order("testID","s");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Meal menu"
				+ "\n1.Breakfast \n2.Lunch \n3.Dinner \n4. Dessert\n");
	}

	@Test
	public void testOrderCorrectOutputWithInvalidDecision2() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
			result = this.sqldatabaseEngine.order("testID","1");
			result = this.sqldatabaseEngine.order("testID","a");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("What type of food do you like to choose?\n1.Vegetarian\n2.Chicken\n3.Pork\n4.Beef\n5.Don't care");
	}

	@Test
	public void testOrderCorrectOutputWithInvalidDecision3() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
			result = this.sqldatabaseEngine.order("testID","4");
			result = this.sqldatabaseEngine.order("testID","3");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Are you vegetarian?\n1.yes\n2.no");
	}
	
	@Test
	public void testEnergyInvalidInput2() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.energy("energy"+"\n"+"1"+"a","testID");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(false);
	} */	

}
