package eu.playeruion.lobby.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import eu.playeruion.lobby.LobbyCore;
import eu.playeruion.lobby.Utils;

public class BaseCommand implements CommandExecutor {
	
	private LobbyCore main = LobbyCore.getInstance();
	private Utils utils = new Utils();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("treasure")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("§cEzt a parancsot csak játékos használhatja!");
				
				return true;
			}
			
			if(!sender.hasPermission("playerunion.treasure")) {
				sender.sendMessage("§cNincs jogosultságod a parancs használatára!");
				
				return true;
			}
			
			Player p = (Player) sender;
			
			p.getInventory().addItem(this.utils.getTreasure());
			
			sender.sendMessage("§aYey!");
			
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("armortool")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("§cEzt a parancsot csak játékos használhatja!");
				
				return true;
			}
			
			if(!sender.hasPermission("playerunion.build")) {
				sender.sendMessage("§cNincs jogosultságod a parancs használatára!");
				
				return true;
			}
			
			Player p = (Player) sender;
			
			p.getInventory().addItem(this.utils.getArmorStandRemover());
			
			sender.sendMessage("§aYey!");
			
			return true;
		}
		
		if(cmd.getName().equalsIgnoreCase("speed")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage("§cEzt a parancsot csak játékos használhatja!");
				
				return true;
			}
			
			if(!sender.hasPermission("playerunion.speed")) {
				sender.sendMessage("§cNincs jogosultságod a parancs használatára!");
				
				return true;
			}
			
			Player p = (Player) sender;
			
			if(this.main.getServerHandler().speedUp.contains(p)) {
				p.removePotionEffect(PotionEffectType.SPEED);
				this.main.getServerHandler().speedUp.remove(p);
				
				p.sendMessage("§b§lSebesség §8§l| §bSebességed visszaállítva az eredetire!");
				
				return true;
			}
			
			p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1000000, 1, true, true, true));
			this.main.getServerHandler().speedUp.add(p);
			
			p.sendMessage("§b§lSebesség §8§l| §bSebességed sikeresen megduplázva!");
			
			return true;
		}
		
		return false;
	}

}
