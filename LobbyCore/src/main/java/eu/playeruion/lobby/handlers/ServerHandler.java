package eu.playeruion.lobby.handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.json.JSONArray;
import org.json.JSONObject;

import eu.playeruion.lobby.LobbyCore;

public class ServerHandler {
	
	private LobbyCore main = LobbyCore.getInstance();
	
	private File treasureSaveFile = new File(this.main.getDataFolder(), "treasures.json");
	
	private JSONObject treasureSave = new JSONObject();
	public ArrayList<String> treasureList = new ArrayList<String>();
	
	public void initTreasureThread() {
		this.loadTreasures();
		
		Runnable particleTask = () -> {
			if(this.treasureList.size() > 0)
				this.treasureList.forEach(location -> this.convertStringtoLocation(location).getWorld().playEffect(this.convertStringtoLocation(location), Effect.VILLAGER_PLANT_GROW, 1));
		};
		
		Runnable saveTask = () -> {
			this.saveTreasures();
		};
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(this.main, particleTask, 0, 30);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this.main, saveTask, 0, 6000L);
	}
	
	public void saveTreasures() {
		this.treasureSave.put("treasures", this.treasureList);
		
		try {
			if(!this.treasureSaveFile.exists())
				this.treasureSaveFile.createNewFile();
			
			FileWriter writer = new FileWriter(this.treasureSaveFile);
			
			writer.write(this.treasureSave.toString(4));
			writer.close();
			
			this.main.getLogger().info("Kincsek sikeresen elmentve!");
		} catch (IOException e) {
			this.main.getLogger().severe("Nem sikerült elmenteni a kincseket: " + e.getMessage());
		}
	}
	
	public void loadTreasures() {
		if(!this.treasureSaveFile.exists())
			this.saveTreasures();
		
		try {
			StringBuilder lines = new StringBuilder("");
			
			Files.lines(this.treasureSaveFile.toPath()).forEach(line -> lines.append(line));
			
			this.treasureSave = new JSONObject(lines.toString());
			
			JSONArray arrayOfLocations = this.treasureSave.getJSONArray("treasures");
			
			arrayOfLocations.forEach(location -> this.treasureList.add(String.valueOf(location)));
			
			this.main.getLogger().info("Kincsek sikeresen betölve!");
		} catch (IOException e) {
			this.main.getLogger().severe("Nem sikerült a kincsek betöltése: " + e.getMessage());
		}
	}
	
	public Location convertStringtoLocation(String locationIn) {
		String[] arguments = locationIn.split("x");
		
		World world = Bukkit.getWorld(arguments[0]);
		int blockX = Integer.parseInt(arguments[1]);
		int blockY = Integer.parseInt(arguments[2]);
		int blockZ = Integer.parseInt(arguments[3]);
		
		return new Location(world, blockX, blockY, blockZ);
	}
	
	public String convertLocationToString(Location locationIn) {
		return "" + locationIn.getWorld().getName() + "x" + locationIn.getBlockX() + "x" + locationIn.getBlockY() + "x" + locationIn.getBlockZ();
	}
	
}
