case "order":{
        	
        	
        		
        		//find state

        		try {
        			String decision=command[1];
            		String result = database.order(userId,decision);
            		this.replyText(replyToken, result);
            	} catch (Exception e) {
            		this.replyText(replyToken, "Sorry, please enter a valid input. order <int> ");
            	};
                break;
}
