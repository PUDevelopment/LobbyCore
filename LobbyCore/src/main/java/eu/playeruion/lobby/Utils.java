package eu.playeruion.lobby;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.RandomStringUtils;

public class Utils {
	
	public String getCurrentDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		
		return format.format(new Date());
	}
	
	public String generateUniqueId() {
		return RandomStringUtils.randomAlphabetic(5).toLowerCase();
	}

}
