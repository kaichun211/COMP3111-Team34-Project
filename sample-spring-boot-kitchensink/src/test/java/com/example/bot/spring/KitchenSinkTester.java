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
	
/*
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
	@Test
	public void testWaterUserNotExist() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.waterInterval("water\n50", "not_exist_user");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(null);
	}
	
	
	
	@Test
	public void testWaterValidInputBoundary() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			this.sqldatabaseEngine.InitiallizeTestData("update user_info set water_int = 0 where user_id = 'testID'");
			result = this.sqldatabaseEngine.waterInterval("water" + "\n" + "1", "testID");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Data updated! ");
		TimeUnit.SECONDS.sleep(60);
		try {
			result = this.sqldatabaseEngine.waterNotif("testID");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("Remember"));
		
		try {
			result = this.sqldatabaseEngine.waterNotif("testID");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("");
		
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
	
	@Test
	public void testWaterUserNotexist() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.waterNotif("not_exist_user");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("");
	}


//-------------Friend
	@Test
	public void testFriendUserExistsShorterThan6() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.friend("small_id");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("000005");
	}
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
	public void testGetCouponCountFunctionWrongSQL() throws Exception {
		boolean thrown = false;
		int result = 0;
		try {
		result = this.sqldatabaseEngine.GetCouponCount("select coupon_count from coupontable where user_id = 'notexist'");
		}catch (Exception e)
		{
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result==0).isEqualTo(true);
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
	public void testRedeemNoUser() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.redeem("not_exist_user");
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
		public void testSportsWeightUserNotExist() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.sports_amount("user_not_exist");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo("Your Weight is invalid! Please set your weight first");
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
		
		@Test
		public void testSexTypeInvalidUser() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.sex("M", "user_not_exist");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo(null);
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
		public void testAgeValidInputNotExistingUser() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.age("50", "not_exist_user");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo(null);
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
		public void testHeightValidInputNotExistingUser() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.height("50", "not_exist_user");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo(null);
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
		public void testWeightValidInputNotExistingUser() throws Exception {
			boolean thrown = false;
			String result = null;
			try {
				result = this.sqldatabaseEngine.weight("50", "not_exist_user");
			} catch (Exception e) {
				thrown = true;
			}
			
			assertThat(!thrown).isEqualTo(true);
			assertThat(result).isEqualTo(null);
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

//-------------------Order
	
	@Test
	public void testOrderNewUser() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			this.sqldatabaseEngine.InitiallizeTestData("update user_info set state = 0 where user_id = 'newuser'");
			result = this.sqldatabaseEngine.order("newuser","1");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("Meal menu")).isEqualTo(true);
	}
	
	@Test
	public void testOrderNewUserWrongState() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			this.sqldatabaseEngine.InitiallizeTestData("update user_info set state = -1 where user_id = 'randomID'");
			result = this.sqldatabaseEngine.order("randomID","1");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("");
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
		assertThat(result.contains("Meal menu")).isEqualTo(true);
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
		assertThat(result.contains("What type of food do you like to choose")).isEqualTo(true);
		try {
		result = this.sqldatabaseEngine.order("testID","1");
	} catch (Exception e) {
		thrown = true;
	}
	
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("1.*")).isEqualTo(true);
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
		assertThat(result.contains("What type of food do you like to choose")).isEqualTo(true);
		
		try {
			result = this.sqldatabaseEngine.order("testID","2");
		} catch (Exception e) {
			thrown = true;
		}
		
			assertThat(!thrown).isEqualTo(true);
			assertThat(result.contains("1.*")).isEqualTo(true);
		
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
		assertThat(result.contains("What type of food do you like to choose")).isEqualTo(true);
		
		try {
			result = this.sqldatabaseEngine.order("testID","3");
		} catch (Exception e) {
			thrown = true;
		}
		
			assertThat(!thrown).isEqualTo(true);
			assertThat(result.contains("1.*")).isEqualTo(true);
		
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
		assertThat(result.contains("Are you vegetarian")).isEqualTo(true);
		
		try {
			result = this.sqldatabaseEngine.order("testID","1");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("1.*")).isEqualTo(true);
	}
	
	@Test
	public void testOrderCorrectOutput6() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
			result = this.sqldatabaseEngine.order("testID","4");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("Are you vegetarian")).isEqualTo(true);
		
		
		try {
			result = this.sqldatabaseEngine.order("testID","2");
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("1.*")).isEqualTo(true);
	}
	
	@Test
	public void testOrderCorrectOutput7() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
			result = this.sqldatabaseEngine.order("testID","1");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("What type of food do you like to choose")).isEqualTo(true);
		try {
		result = this.sqldatabaseEngine.order("testID","4");
	} catch (Exception e) {
		thrown = true;
	}
	
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("1.*")).isEqualTo(true);
	}

		
	
	@Test
	public void testOrderCorrectOutput8() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
			result = this.sqldatabaseEngine.order("testID","2");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("What type of food do you like to choose")).isEqualTo(true);
		
		try {
			result = this.sqldatabaseEngine.order("testID","5");
		} catch (Exception e) {
			thrown = true;
		}
		
			assertThat(!thrown).isEqualTo(true);
			assertThat(result.contains("1.*")).isEqualTo(true);
		
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
		assertThat(result.contains("Meal menu")).isEqualTo(true);
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
		assertThat(result.contains("What type of food do you like to choose")).isEqualTo(true);
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
		assertThat(result.contains("Are you vegetarian")).isEqualTo(true);
	}
	
	@Test
	public void testGetAndSetInfoStateFunction() throws Exception {
		boolean thrown = false;
		try {
			this.sqldatabaseEngine.getInfoState("randomID");
			this.sqldatabaseEngine.setInfoState("default","randomID");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void testCreateAndDeleteUser() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.InitializeNewUser("user_that_will_be_deleted");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Data re-created! Unfortunately you are not a new user so you are not qualified for our new user event");
		try {
	
		result = this.sqldatabaseEngine.RemoveUser("user_that_will_be_deleted");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Data Deleted sucessfully!");
	}
	
	@Test
	public void testCreateNewUser() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			this.sqldatabaseEngine.InitiallizeTestData("delete from coupontable where user_id = 'new_user'");
			result = this.sqldatabaseEngine.InitializeNewUser("new_user");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("Data initiallized! Welcome~");
	}

	
	@Test
	public void testWarningUserNotExist() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.warning("not_exist_user");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("Error")).isEqualTo(true);
	}
	*/
	@Test
	public void testWarningUserWrongGender() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.warning("warning_user");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("User info is invalid")).isEqualTo(true);
	}
	
	@Test
	public void testWarningEnergyExceeds() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.warning("energy_too_high");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("EXCEED")).isEqualTo(true);
	}
	
	@Test
	public void testWarningSodiumExceeds() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.warning("sodium_too_high");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("EXCEED")).isEqualTo(true);
	}
	
	@Test
	public void testWarningFatExceeds() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.warning("fat_too_high");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("EXCEED")).isEqualTo(true);
	}
	
	@Test
	public void testEnergy1() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nSun","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
		
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nsun","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
	}
	
	@Test
	public void testEnergy2() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nMon","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
		
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nmon","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
	}
	
	@Test
	public void testEnergy3() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nTue","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
		
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\ntue","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
	}
	
	@Test
	public void testEnergy4() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nWed","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
		
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nwed","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
	}
	
	@Test
	public void testEnergy5() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nThu","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
		
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nthu","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
	}
	
	@Test
	public void testEnergy6() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nFri","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
		
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nfri","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
	}
	
	@Test
	public void testEnergy7() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nSat","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
		
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nsat","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
	}
	
	@Test
	public void testEnergy8() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nxxx","testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("updated")).isEqualTo(true);
	}
	
	@Test
	public void testEnergyUserNotExist() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.energy("energy\n100\nSun","not_exist_user");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo(null);
	}
	
	@Test
	public void testMenuNoResult() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.menu_search("menu\non");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("0 (g)")).isEqualTo(true);
	}
	
	@Test
	public void testMenuValidResult() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.menu_search("menu\nRice");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("155")).isEqualTo(true);
	}
	
	@Test
	public void testMenuValidResult2Lines() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.menu_search("menu\nRice\nBeef");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("155 (g)")).isEqualTo(true);
	}
	
	@Test
	public void testMenuInvalidResult() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.menu_search("menu");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result).isEqualTo("");
	}
	
	@Test
	public void testEatNoResult() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.eat("eat\non", "testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("Weight = 0")).isEqualTo(true);
	}
	
	@Test
	public void testEatValidResult() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.eat("eat\nRice", "testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("155 (g)")).isEqualTo(true);
	}
	
	@Test
	public void testEatValidResult2Lines() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.eat("eat\nRice\nBeef", "testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("155 (g)")).isEqualTo(true);
	}
	
	@Test
	public void testEatInvalidResult() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.eat("eat", "testID");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("Data is recorded")).isEqualTo(true);
	}
	
	@Test
	public void testEatInvalidUser() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.eat("eat\nRice", "not_exist_user");
			
		} catch (Exception e) {
			thrown = true;
		}
		assertThat(!thrown).isEqualTo(true);
	}
	
	@Test
	public void testOrderCorrectOutputWithInvalidDecision5() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
			result = this.sqldatabaseEngine.order("testID","1");
			result = this.sqldatabaseEngine.order("testID","6");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("What type of food do you like to choose")).isEqualTo(true);
	}

	@Test
	public void testOrderCorrectOutputWithInvalidDecision6() throws Exception {
		boolean thrown = false;
		String result = null;
		try {
			result = this.sqldatabaseEngine.order("testID","exit");
			result = this.sqldatabaseEngine.order("testID","5");
		} catch (Exception e) {
			thrown = true;
		}
		
		assertThat(!thrown).isEqualTo(true);
		assertThat(result.contains("Meal menu")).isEqualTo(true);
	}
	
	
}
