/*
 * Copyright 2016 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package com.example.bot.spring;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import com.linecorp.bot.model.profile.UserProfileResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.google.common.io.ByteStreams;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.action.MessageAction;
import com.linecorp.bot.model.action.PostbackAction;
import com.linecorp.bot.model.action.URIAction;
import com.linecorp.bot.model.event.BeaconEvent;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.FollowEvent;
import com.linecorp.bot.model.event.JoinEvent;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.PostbackEvent;
import com.linecorp.bot.model.event.UnfollowEvent;
import com.linecorp.bot.model.event.message.AudioMessageContent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.LocationMessageContent;
import com.linecorp.bot.model.event.message.StickerMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.event.source.GroupSource;
import com.linecorp.bot.model.event.source.RoomSource;
import com.linecorp.bot.model.event.source.Source;
import com.linecorp.bot.model.message.AudioMessage;
import com.linecorp.bot.model.message.ImageMessage;
import com.linecorp.bot.model.message.ImagemapMessage;
import com.linecorp.bot.model.message.LocationMessage;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.StickerMessage;
import com.linecorp.bot.model.message.TemplateMessage;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.message.imagemap.ImagemapArea;
import com.linecorp.bot.model.message.imagemap.ImagemapBaseSize;
import com.linecorp.bot.model.message.imagemap.MessageImagemapAction;
import com.linecorp.bot.model.message.imagemap.URIImagemapAction;
import com.linecorp.bot.model.message.template.ButtonsTemplate;
import com.linecorp.bot.model.message.template.CarouselColumn;
import com.linecorp.bot.model.message.template.CarouselTemplate;
import com.linecorp.bot.model.message.template.ConfirmTemplate;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

@Slf4j
@LineMessageHandler
public class KitchenSinkController {
	


	@Autowired
	private LineMessagingClient lineMessagingClient;

	@EventMapping
	public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		log.info("This is your entry point:");
		log.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		TextMessageContent message = event.getMessage();
		handleTextContent(event.getReplyToken(), event, message);
	}

	@EventMapping
	public void handleStickerMessageEvent(MessageEvent<StickerMessageContent> event) {
		handleSticker(event.getReplyToken(), event.getMessage());
	}

	@EventMapping
	public void handleLocationMessageEvent(MessageEvent<LocationMessageContent> event) {
		LocationMessageContent locationMessage = event.getMessage();
		reply(event.getReplyToken(), new LocationMessage(locationMessage.getTitle(), locationMessage.getAddress(),
				locationMessage.getLatitude(), locationMessage.getLongitude()));
	}

	@EventMapping
	public void handleImageMessageEvent(MessageEvent<ImageMessageContent> event) throws IOException {
		final MessageContentResponse response;
		String replyToken = event.getReplyToken();
		String messageId = event.getMessage().getId();
		try {
			response = lineMessagingClient.getMessageContent(messageId).get();
		} catch (InterruptedException | ExecutionException e) {
			reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
			throw new RuntimeException(e);
		}
		DownloadedContent jpg = saveContent("jpg", response);
		reply(((MessageEvent) event).getReplyToken(), new ImageMessage(jpg.getUri(), jpg.getUri()));

	}

	@EventMapping
	public void handleAudioMessageEvent(MessageEvent<AudioMessageContent> event) throws IOException {
		final MessageContentResponse response;
		String replyToken = event.getReplyToken();
		String messageId = event.getMessage().getId();
		try {
			response = lineMessagingClient.getMessageContent(messageId).get();
		} catch (InterruptedException | ExecutionException e) {
			reply(replyToken, new TextMessage("Cannot get image: " + e.getMessage()));
			throw new RuntimeException(e);
		}
		DownloadedContent mp4 = saveContent("mp4", response);
		reply(event.getReplyToken(), new AudioMessage(mp4.getUri(), 100));
	}

	@EventMapping
	public void handleUnfollowEvent(UnfollowEvent event) {
		log.info("unfollowed this bot: {}", event);
		String userId = event.getSource().getUserId();
		try{
			database.RemoveUser(userId);
		}catch (Exception e){
    	};
	}

	@EventMapping
	public void handleFollowEvent(FollowEvent event) {
		String reply = null;
		String replyToken = event.getReplyToken();
		String userId = event.getSource().getUserId();
		log.info(userId);
		try {
		reply = database.InitializeNewUser(userId);
		}catch (Exception e){
    		this.replyText(replyToken, "Sorry, Unknown error occured, Please try to reinstall the bot and try again. ");
    	};
		this.replyText(replyToken, reply);
	}

	@EventMapping
	public void handleJoinEvent(JoinEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Joined " + event.getSource());
	}

	@EventMapping
	public void handlePostbackEvent(PostbackEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Got postback " + event.getPostbackContent().getData());
	}

	@EventMapping
	public void handleBeaconEvent(BeaconEvent event) {
		String replyToken = event.getReplyToken();
		this.replyText(replyToken, "Got beacon message " + event.getBeacon().getHwid());
	}

	@EventMapping
	public void handleOtherEvent(Event event) {
		log.info("Received message(Ignored): {}", event);
	}

	private void reply(@NonNull String replyToken, @NonNull Message message) {
		reply(replyToken, Collections.singletonList(message));
	}

	private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
		try {
			BotApiResponse apiResponse = lineMessagingClient.replyMessage(new ReplyMessage(replyToken, messages)).get();
			log.info("Sent messages: {}", apiResponse);
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private void replyText(@NonNull String replyToken, @NonNull String message) {
		if (replyToken.isEmpty()) {
			throw new IllegalArgumentException("replyToken must not be empty");
		}
		if (message.length() > 1000) {
			message = message.substring(0, 1000 - 2) + "..";
		}
		this.reply(replyToken, new TextMessage(message));
	}


	private void handleSticker(String replyToken, StickerMessageContent content) {
		reply(replyToken, new StickerMessage(content.getPackageId(), content.getStickerId()));
	}

	private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
        String text = content.getText();
		String[] command;
		command = text.split("\\r?\\n");
		String userId = event.getSource().getUserId();
		log.info("Got text message from {}: {}", replyToken, text);
        switch (command[0].toLowerCase()) {
        	case "help":{
    		try {
        		String result1 = "Welcome to this bot! Here are our supported commands, all of them are case-insensitive.\n\n" + 
        				"1. Weight Function\nYou can save your weight in kg, which is required to calculate Sports time to burn those calories!\nTo use the function, type 'weight<go to next line>50' if your weight is 50." +
        				"\n\n2. Sports Function\nYou can calculate how much do you need to workout to burn those calories!\nTo use the function, simply type in 'sports'." +
        				"\n\n3. Water Function\nYou can enable this function and our bot will remind you to drink water once in a while!\nTo use this function, type 'water<go to next line>60' if you want us to remind you every 60 minutes.";
        		
        		String result3 = "We are also having a promotional event now for new users!\n\nYou can check your unique 6-digit id using 'friend', and tells your friend about this code when you recommend them to use this bot." +
        				"\n\nOnce they joined, they can use 'code<go to next line>XXXXXX' where XXXXXX is your ID.Both of you will get a coupon when this were done!" +
        				"\n\nYou can redeem a coupon and check how many coupons do you still have using 'redeem'. Enjoy~" +
        				"\n\n*We only have 6000 coupons to giveaway in total, so please act quick!";
        		this.reply(replyToken, Arrays.asList(new TextMessage(result1), new TextMessage(result3)));
        	} catch (Exception e) {
        		this.replyText(replyToken, "Sorry, please try again.");
        	};
            break;
}  
        	case "order":{
    		try {
    			String decision=command[1];
        		String result = database.order(userId,decision);
        		this.replyText(replyToken, result);
        	} catch (Exception e) {
        		this.replyText(replyToken, "Sorry, please enter a valid input. order <int> ");
        	};
            break;
}
            case "profile": {
                //String userId = event.getSource().getUserId();
                if (userId != null) {
                    /*lineMessagingClient
                            .getProfile(userId)
                            .whenComplete(new ProfileGetter (this, replyToken));*/
                	this.replyText(replyToken, userId);
                } else {
                    this.replyText(replyToken, "Bot can't use profile API without user ID");
                }
                break;
            }
        /*    case "total": {
            	
            	//String userId = event.getSource().getUserId();
            	try {
            		String result = database.search(text, "user_info", userId);
            		this.replyText(replyToken, result);
            	} catch (Exception e) {
            		this.replyText(replyToken, "Sorry, please enter a valid input. Input should be in format 'weight <your weight in kg rounded to the nearest integer>'. ");
            	};
                break;
            }*/
            case "water":{
            	try {
            		String result = database.waterInterval(text, userId);
            		this.replyText(replyToken, result + database.waterNotif(userId));
            	} catch (Exception e) {
            		this.replyText(replyToken, "Sorry, please enter a valid input. Input should be in format 'water <minutes in integer, 0 as OFF>'. ");
            	};
                break;
            }
            case "sports": {
            	
            	//String userId = event.getSource().getUserId();
            	try {
            		String result = database.sports_amount(userId);
            		this.replyText(replyToken, result);
            	} catch (Exception e) {
            		this.replyText(replyToken, "Sorry, please enter a valid input.");
            	};
                break;
            }
            case "weight": {
            	
            	//String userId = event.getSource().getUserId();
            	try {
            		String result = database.weight(text, userId);
            		this.replyText(replyToken, result + database.waterNotif(userId));
            	} catch (Exception e) {
            		this.replyText(replyToken, "Sorry, please enter a valid input. Input should be in format 'weight <your weight in kg rounded to the nearest integer>'. ");
            	};
                break;
            }
            case "energy": {
            	
            	//String userId = event.getSource().getUserId();
            	try {
            		String result = database.energy(text, userId);
            		this.replyText(replyToken, result);
            	} catch (Exception e) {
            		this.replyText(replyToken, "Sorry, please enter a valid input. Input should be in format 'energy <energy in integer> <day of week(e.g. Mon/Tue)>'. ");
            	};
                break;
            }  
            case "menu": {
            	
            	//String userId = event.getSource().getUserId();
            	try {
            		String result_set = database.menu_search(text);
            		this.replyText(replyToken, result_set + database.waterNotif(userId));
            		}
            		catch (Exception e) {
            		this.replyText(replyToken, "Sorry, please enter a valid input.");
            	};
                break;
            }         
 /*           case "calculate": {
            	
            	//String userId = event.getSource().getUserId();
            	try {
            		String result = database.search(text, "user_info", userId);
            		this.replyText(replyToken, result + database.waterNotif(userId));
            	} catch (Exception e) {
            		this.replyText(replyToken, "Sorry, please enter a valid input. Input should be in format 'weight <your weight in kg rounded to the nearest integer>'. ");
            	};
                break;
            }
            case "confirm": {
                ConfirmTemplate confirmTemplate = new ConfirmTemplate(
                        "Do it?",
                        new MessageAction("Yes", "Yes!"),
                        new MessageAction("No", "No!")
                );
                TemplateMessage templateMessage = new TemplateMessage("Confirm alt text", confirmTemplate);
                this.reply(replyToken, templateMessage);
                break;
            }*/
            case "friend":{
	            	try {
	            		String result = database.friend(userId);
	            		this.replyText(replyToken, "Your code is: " + result + database.waterNotif(userId));
	            	} catch (Exception e) {
	            		this.replyText(replyToken, "Sorry, please enter a valid input.");
	            	};
	                break;
            }
            case "code":{
	            	try {
	            		String result = database.code(text, userId);
	            		this.replyText(replyToken, result + database.waterNotif(userId));
	            	} catch (Exception e) {
	            		this.replyText(replyToken, "Invalid input! You should enter a valid 6-digit number.");
	            	};
	                break;
            }
            case "redeem":{
	            	try {
	            		String result = database.redeem(userId);
	            		if(result!="You currently have no coupon")
	            		{
	            			this.reply(replyToken,Arrays.asList(new ImageMessage("https://help.idevaffiliate.com/wp-content/uploads/2015/04/coupon-graphic.gif", "https://help.idevaffiliate.com/wp-content/uploads/2015/04/coupon-graphic.gif")
	            				, new TextMessage(result + database.waterNotif(userId))));
	            		}
	            		else
	            		{
	            			this.replyText(replyToken, result + database.waterNotif(userId));
	            		}
	            	} catch (Exception e) {
	            		this.replyText(replyToken, "Sorry, Error occured, please try again later.");
	            	};
	                break;
            }

            default:
            	String reply = "Sorry! Your command is not recognized. You may type 'help' to check the list of commands available for this bot.";
                this.replyText(
                        replyToken,
                        reply + database.waterNotif(userId)
                );
                break;
        }
    }

	static String createUri(String path) {
		return ServletUriComponentsBuilder.fromCurrentContextPath().path(path).build().toUriString();
	}

	private void system(String... args) {
		ProcessBuilder processBuilder = new ProcessBuilder(args);
		try {
			Process start = processBuilder.start();
			int i = start.waitFor();
			log.info("result: {} =>  {}", Arrays.toString(args), i);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		} catch (InterruptedException e) {
			log.info("Interrupted", e);
			Thread.currentThread().interrupt();
		}
	}

	private static DownloadedContent saveContent(String ext, MessageContentResponse responseBody) {
		log.info("Got content-type: {}", responseBody);

		DownloadedContent tempFile = createTempFile(ext);
		try (OutputStream outputStream = Files.newOutputStream(tempFile.path)) {
			ByteStreams.copy(responseBody.getStream(), outputStream);
			log.info("Saved {}: {}", ext, tempFile);
			return tempFile;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static DownloadedContent createTempFile(String ext) {
		String fileName = LocalDateTime.now().toString() + '-' + UUID.randomUUID().toString() + '.' + ext;
		Path tempFile = KitchenSinkApplication.downloadedContentDir.resolve(fileName);
		tempFile.toFile().deleteOnExit();
		return new DownloadedContent(tempFile, createUri("/downloaded/" + tempFile.getFileName()));
	}


	


	public KitchenSinkController() {
		database = new SQLDatabaseEngine();
	}

	private SQLDatabaseEngine database;
	

	//The annontation @Value is from the package lombok.Value
	//Basically what it does is to generate constructor and getter for the class below
	//See https://projectlombok.org/features/Value
	@Value
	public static class DownloadedContent {
		Path path;
		String uri;
	}


	//an inner class that gets the user profile and status message
	class ProfileGetter implements BiConsumer<UserProfileResponse, Throwable> {
		private KitchenSinkController ksc;
		private String replyToken;
		
		public ProfileGetter(KitchenSinkController ksc, String replyToken) {
			this.ksc = ksc;
			this.replyToken = replyToken;
		}
		@Override
    	public void accept(UserProfileResponse profile, Throwable throwable) {
    		if (throwable != null) {
            	ksc.replyText(replyToken, throwable.getMessage());
            	return;
        	}
        	ksc.reply(
                	replyToken,
                	Arrays.asList(new TextMessage(
                		"Display name: " + profile.getDisplayName()),
                              	new TextMessage("Status message: "
                            		  + profile.getStatusMessage()))
        	);
    	}
    }
	
	

}
