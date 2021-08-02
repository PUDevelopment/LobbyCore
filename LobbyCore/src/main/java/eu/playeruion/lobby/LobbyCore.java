package eu.playeruion.lobby;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.plugin.java.JavaPlugin;

import eu.playeruion.lobby.commands.BaseCommand;
import eu.playeruion.lobby.handlers.ServerHandler;
import eu.playeruion.lobby.listeners.PlayerListener;
import eu.playeruion.lobby.scoreboard.BoardManager;

public class LobbyCore extends JavaPlugin {
	
	private static LobbyCore instance;
	private BoardManager boardManager;
	private ServerHandler serverHandler;
	
	private File config = new File(this.getDataFolder(), "config.yml");
	
	public static LobbyCore getInstance() {
		return instance;
	}
	
	public LobbyCore() {
		instance = this;
	}
	
	@Override
	public void onEnable() {
		instance = this;
		
		this.serverHandler = new ServerHandler();
		this.boardManager = new BoardManager();
		
		this.setupConfig();
		
		this.serverHandler.initTreasureThread();
		this.boardManager.initBoard();
		
		this.registerCommands();
		this.registerListeners();
		
		this.getLogger().info("A plugin sikeresen elindult!");
	}
	
	@Override
	public void onDisable() {
		
	}
	
	private void registerCommands() {
		this.getCommand("treasure").setExecutor(new BaseCommand());
		this.getCommand("armortool").setExecutor(new BaseCommand());
	}
	
	private void registerListeners() {
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
	}
	
	public void setupConfig(){
		if(!config.exists()){
			getDataFolder().mkdirs();
			copy(getResource("config.yml"), config);
			reloadConfig();
			try {
				getConfig().load(config);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}else{
			try {
				getConfig().load(config);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void copy(InputStream from, File to) {
		try {
			OutputStream out = new FileOutputStream(to);
			byte[] buffer = new byte[1024];
			int size = 0;
			
			while((size = from.read(buffer)) != -1) {
				out.write(buffer, 0, size);
			}
			
			out.close();
			from.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void saveConfig(){
		try {
			getConfig().save(config);
			reloadConfig();
		} catch (IOException e) {
			e.printStackTrace(); 
		}
	}
	
	public ServerHandler getServerHandler() {
		return this.serverHandler;
	}

}
