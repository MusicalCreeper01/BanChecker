package com.musicalcreeper01.banchecker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BanChecker extends JavaPlugin {
	
	public static HashMap<String, Player> playerList;
	
	@Override
	public void onEnable() {
	    // TODO Insert logic to be performed when the plugin is enabled
		getLogger().info("onEnable has been invoked!");
	 
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			 playerList.put(player.getName(), player);
		}
	}
	 
	@Override
	public void onDisable() {
		// TODO Insert logic to be performed when the plugin is disabled
		getLogger().info("onDisable has been invoked!");
	 }
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("checkban")) { // If the player typed /basic then do the following...
			// doSomething
			
			JSONObject bandata = new JSONObject();
			
			try {
				bandata = readJsonFromUrl("http://api.fishbans.com/bans/" + args[0].toString());
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				sender.sendMessage("&4There was an error getting the ban information. This may mean the API server is down.");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				sender.sendMessage("&4There was an issue with the format of the ban data recivied. THIS IS NOT YOUR ISSUE! Tell an admin to check if there is an updated version of the plugin.");
			}
			
			String userExists = "false";
			String playerUUID = "0000";
			String SubjectsName = "null";
			
			int MCBansBans = 0;
			
			List<BanInfo> MCBansInfo = new ArrayList<BanInfo>();;
		
			
			JSONObject services;
			
			try {
				userExists = bandata.getJSONObject("success").toString();
				playerUUID = bandata.getJSONObject("bans").getJSONObject("uuid").toString();
				services = bandata.getJSONObject("bans").getJSONObject("service");
				SubjectsName = bandata.getJSONObject("bans").getJSONObject("username").toString();
			
				MCBansBans = Integer.parseInt(bandata.getJSONObject("bans").getJSONObject("service").getJSONObject("mcbans").getJSONObject("bans").toString());
				
				JSONArray MCBansInfoJSONArray = new JSONArray(bandata.getJSONObject("bans").getJSONObject("service").getJSONObject("mcbans").getJSONObject("ban_info").toString().replace("{", "[").replace("}", "]"));
				
				for (int i = 0; i < MCBansInfoJSONArray.length(); i++){
					MCBansInfo.add(new BanInfo(MCBansInfoJSONArray.get(i).toString().split(":")[0].replace("\"", ""), MCBansInfoJSONArray.get(i).toString().split(":")[1].replace("\"", "")));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				sender.sendMessage("&$Failed to read info about the player.");
			}
			
			
			if(userExists == "false" || userExists == "0"){
				sender.sendMessage("That user does not exist!");
			} else if (userExists == "true" || userExists == "1"){
				sender.sendMessage("&6--------------------%r");
				sender.sendMessage("User's Name: " + SubjectsName);
				sender.sendMessage("User's UUID: " + playerUUID);
				sender.sendMessage(" ");
				sender.sendMessage("---MCBans---");
				sender.sendMessage("Bans: " + MCBansBans);
				if(MCBansBans > 0){
					for (int i = 0; i < MCBansBans; i++){
						sender.sendMessage("Ip: " + MCBansInfo.get(i).serverip);
						sender.sendMessage("Reason: " + MCBansInfo.get(i).reason);
					}
				}
				
			}
			return true;
		} //If this has happened the function will return true. 
	        // If this hasn't happened the value of false will be returned.
		return false; 
	}
	
	
	private static String readAll(Reader rd) throws IOException {
	    StringBuilder sb = new StringBuilder();
	    int cp;
	    while ((cp = rd.read()) != -1) {
	      sb.append((char) cp);
	    }
	    return sb.toString();
	  }
	
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	    InputStream is = new URL(url).openStream();
	    try {
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
	      String jsonText = readAll(rd);
	      JSONObject json = new JSONObject(jsonText);
	      
	      return json;
	    } finally {
	      is.close();
	    }
	  }
}
