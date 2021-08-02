package eu.playeruion.lobby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import eu.playeruion.lobby.LobbyCore;
import eu.playeruion.lobby.Utils;

public class BaseCommand implements CommandExecutor {
	
	private LobbyCore main = LobbyCore.getInstance();
	private Utils utils = new Utils();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(label.equalsIgnoreCase("treasure")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("§cEzt a parancsot csak játékos használhatja!");
				
				return true;
			}
			
			if(!sender.hasPermission("playerunion.treasure")) {
				sender.sendMessage("§cNincs jogosultságod a parancs használatára!");
				
				return true;
			}
			
			Player p = (Player) sender;
			
			p.getInventory().addItem(utils.getTreasure());
			
			sender.sendMessage("§aYey!");
			
			return true;
		}
		
		if(label.equalsIgnoreCase("armortool")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("§cEzt a parancsot csak játékos használhatja!");
				
				return true;
			}
			
			if(!sender.hasPermission("playerunion.treasure")) {
				sender.sendMessage("§cNincs jogosultságod a parancs használatára!");
				
				return true;
			}
			
			Player p = (Player) sender;
			
			p.getInventory().addItem(utils.getArmorStandRemover());
			
			sender.sendMessage("§aYey!");
			
			return true;
		}
		
		return false;
	}

}
