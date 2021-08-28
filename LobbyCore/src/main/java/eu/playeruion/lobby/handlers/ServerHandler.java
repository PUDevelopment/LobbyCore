package eu.playeruion.lobby.handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import eu.playeruion.lobby.LobbyCore;

public class ServerHandler {
	
	private LobbyCore main = LobbyCore.getInstance();
	
	private File treasureSaveFile = new File(this.main.getDataFolder(), "treasures.json");
	private File collectedSaveFile = new File(this.main.getDataFolder(), "collectedTreasures.json");
	
	private JSONObject treasureSave = new JSONObject();
	
	public HashMap<String, ArrayList<String>> collectedTreasures = new HashMap<String, ArrayList<String>>();
	public HashMap<Player, ItemStack> profileItems = new HashMap<Player, ItemStack>();
	
	public ArrayList<String> treasureList = new ArrayList<String>();
	public ArrayList<Player> speedUp = new ArrayList<Player>();
	
	public void initTreasureThread() {
		try {
			this.loadTreasures();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			this.loadCollectedList();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Runnable particleTask = () -> {
			if(this.treasureList.size() > 0) {
				for(Player player : Bukkit.getOnlinePlayers()) {
					if(this.collectedTreasures.containsKey(player.getUniqueId().toString())) {
						for(String location : this.treasureList) {
							if(this.collectedTreasures.get(player.getUniqueId().toString()).contains(location)) {
								player.playEffect(this.convertStringtoLocation(location), Effect.SMOKE, 1);
							} else {
								player.playEffect(this.convertStringtoLocation(location), Effect.VILLAGER_PLANT_GROW, 1);
							}
						}
					} else {
						this.treasureList.forEach(location -> player.playEffect(this.convertStringtoLocation(location), Effect.VILLAGER_PLANT_GROW, 1)); 
					}
					
				}
			}
		};
		
		Runnable saveTask = () -> {
			try {
				this.saveTreasures();
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		
		Runnable collectedSaveTask = () -> {
			try {
				this.saveCollected();
			} catch(Exception e) {
				e.printStackTrace();
			}
		};
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(this.main, collectedSaveTask, 0, 6000L);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this.main, particleTask, 0, 30);
		Bukkit.getScheduler().runTaskTimerAsynchronously(this.main, saveTask, 0, 6000L);
	}
	
	public void saveTreasures() throws Exception {
		this.treasureSave.put("treasures", this.treasureList);
		
		try {
			if(!this.treasureSaveFile.exists())
				this.treasureSaveFile.createNewFile();
			
			FileWriter writer = new FileWriter(this.treasureSaveFile);
			
			writer.write(this.treasureSave.toString(4));
			writer.close();
		} catch (IOException e) {
			this.main.getLogger().severe("Nem sikerült elmenteni a kincseket: " + e.getMessage());
		}
	}
	
	public void collectTreasure(Player p, String location) {
		String id = p.getUniqueId().toString();
		ArrayList<String> collected = new ArrayList<String>();
		
		if(this.collectedTreasures.containsKey(id))
			collected = this.collectedTreasures.get(id);
		
		collected.add(location);
		
		this.collectedTreasures.put(id, collected);
		p.sendMessage("§6§lKincs §8§l| §6Wow! Találtál egy kincset!");
		p.sendMessage("§6§lKincs §8§l| §6Jutalmad: §e1 §6Unit!");
		
		this.main.getConfig().getStringList("beallitasok.ajandek.parancs").forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("%jatekos%", p.getName())));
	}
	
	public void loadCollectedList() throws JSONException {
		if(!this.collectedSaveFile.exists())
			this.saveCollected();
		
		try {
			StringBuilder lines = new StringBuilder("");
			
			Files.lines(this.collectedSaveFile.toPath()).forEach(line -> lines.append(line));
			
			JSONObject collectedList = new JSONObject(lines.toString());
			Iterator<String> idIterator = collectedList.keys();
			
			while(idIterator.hasNext()) {
				String id = idIterator.next();
				JSONArray arrayOfLocations = collectedList.getJSONArray(id);
				ArrayList<String> locations = new ArrayList<String>();
				
				for(int o = 0; o < arrayOfLocations.length(); o++) {
					locations.add(String.valueOf(arrayOfLocations.getString(o)));
				}
				
				this.collectedTreasures.put(id, locations);
			}
			
			this.main.getLogger().info("Kincsek sikeresen betölve!");
		} catch (IOException e) {
			this.main.getLogger().severe("Nem sikerült a kincsek betöltése: " + e.getMessage());
		}
	}
	
	public boolean isCollected(Player p, String location) {
		String id = p.getUniqueId().toString();
		
		if(!this.collectedTreasures.containsKey(id))
			return false;
		
		if(this.collectedTreasures.get(id).contains(location))
			return true;
		
		return false;
	}
	
	public void saveCollected() {
		try {
			if(!this.collectedSaveFile.exists())
				this.collectedSaveFile.createNewFile();
			
			FileWriter writer = new FileWriter(this.collectedSaveFile);
			
			writer.write(new JSONObject(this.collectedTreasures).toString(4));
			writer.close();
		} catch (IOException e) {
			this.main.getLogger().severe("Nem sikerült elmenteni a kincseket: " + e.getMessage());
		} catch (JSONException e) {
			this.main.getLogger().severe("Nem sikerült elmenteni a kincseket: " + e.getMessage());
		}
	}
	
	public void loadTreasures() throws Exception {
		if(!this.treasureSaveFile.exists())
			this.saveTreasures();
		
		try {
			StringBuilder lines = new StringBuilder("");
			
			Files.lines(this.treasureSaveFile.toPath()).forEach(line -> lines.append(line));
			
			this.treasureSave = new JSONObject(lines.toString());
			
			JSONArray arrayOfLocations = this.treasureSave.getJSONArray("treasures");
			
			for(int o = 0; o < arrayOfLocations.length(); o++) {
				this.treasureList.add(String.valueOf(arrayOfLocations.getString(o)));
			}
			
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
