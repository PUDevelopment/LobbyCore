package eu.playeruion.lobby.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;

import eu.playeruion.lobby.LobbyCore;
import eu.playeruion.lobby.Utils;

public class PlayerListener implements Listener {
	
	//Játékosoknak történő tiltások a lobby-ban.
	//Ezen tiltások csak és kizárólag a lobby szerverekre vonatkoznak.
	
	private LobbyCore main = LobbyCore.getInstance();
	private Utils utils = new Utils();
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItemInMainHand();
		Block block = e.getBlock();
		Location blockLocation = block.getLocation();
		
		if(!p.hasPermission("playerunion.build"))
			e.setCancelled(true);
		
		if(this.main.getServerHandler().treasureList.contains(this.main.getServerHandler().convertLocationToString(blockLocation))) {
			this.main.getServerHandler().treasureList.remove(this.main.getServerHandler().convertLocationToString(blockLocation));
			
			p.sendMessage("§aAzta, kiszedted he!");
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItemInMainHand();
		Block block = e.getBlock();
		Location blockLocation = block.getLocation();
		
		if(!p.hasPermission("playerunion.build"))
			e.setCancelled(true);
		
		this.main.getLogger().info("place");
		
		if(item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			
			if(meta.hasDisplayName()) {
				if(meta.getDisplayName().equals(utils.getTreasure().getItemMeta().getDisplayName())) {
					this.main.getServerHandler().treasureList.add(this.main.getServerHandler().convertLocationToString(blockLocation));
					
					p.sendMessage("§aNagyon komoly szar faszka");
				}
			}
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if(e.getEntity() instanceof Player)
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onExplosion(EntityExplodeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent e) {
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		if(!p.hasPermission("playerunion.gamemode"))
			p.setGameMode(GameMode.ADVENTURE);
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();
			Location blockLocation = block.getLocation();
			
			if(block.getType() == Material.PLAYER_HEAD) {
				if(main.getServerHandler().treasureList.contains(this.main.getServerHandler().convertLocationToString(blockLocation))) {
					ArmorStand armorStand = (ArmorStand) blockLocation.getWorld().spawn(blockLocation.clone().add(0.5, 0.0, 0.5).subtract(0.0, 1.3, 0.0), ArmorStand.class);
					
					armorStand.setCustomName("§eMegtaláltál, jej!");
					armorStand.setCustomNameVisible(true);
					armorStand.setGravity(false);
					armorStand.setVisible(false);
					armorStand.setInvulnerable(true);
					
					Firework firework = (Firework) blockLocation.getWorld().spawn(blockLocation, Firework.class);
					FireworkMeta meta = firework.getFireworkMeta();
					
					meta.addEffect(FireworkEffect.builder().flicker(true).withColor(Color.fromBGR(224, 243, 132)).trail(true).build());
					meta.setPower(1);
					
					firework.setFireworkMeta(meta);
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(this.main, () -> {
						firework.detonate();
					}, 5L);
					
					//TODO: ajándékozás
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(this.main, () -> {
						armorStand.remove();
					}, 100L);
				}
			}
		}
	}
	
	@EventHandler
	public void onInteractAt(PlayerInteractAtEntityEvent e) {
		Player p = e.getPlayer();
		Entity entity = e.getRightClicked();
		
		this.main.getLogger().info("interactAt");
		
		if(entity instanceof ArmorStand) {
			ItemStack item = p.getInventory().getItemInMainHand();
			
			if(item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				
				if(meta.hasDisplayName()) {
					if(meta.getDisplayName().equals(utils.getArmorStandRemover().getItemMeta().getDisplayName())) {
						entity.remove();
						
						p.sendMessage("§aAhh, végre, anyád!");
					}
				}
				
			}
		}
	}

}
