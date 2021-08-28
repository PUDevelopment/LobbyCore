package eu.playeruion.lobby.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import eu.playeruion.lobby.LobbyCore;
import eu.playeruion.lobby.Utils;
import eu.playeruion.lobby.gui.ProfileGui;

public class PlayerListener implements Listener {
	
	// Játékosoknak történő tiltások a lobby-ban.
	// Ezen tiltások csak és kizárólag a lobby szerverekre vonatkoznak.
	
	private LobbyCore main = LobbyCore.getInstance();
	private Utils utils = new Utils();
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		Block block = e.getBlock();
		Location blockLocation = block.getLocation();
		
		if(!p.hasPermission("playerunion.build"))
			e.setCancelled(true);
		
		String location = this.main.getServerHandler().convertLocationToString(blockLocation);
		
		if(this.main.getServerHandler().treasureList.contains(location)) {
			this.main.getServerHandler().treasureList.remove(location);
			
			p.sendMessage("§6§lKincs §8§l| §6Kincs sikeresen eltávolítva!");
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
		
		if(item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			
			if(meta.hasDisplayName()) {
				if(meta.getDisplayName().equals(utils.getTreasure().getItemMeta().getDisplayName())) {
					String location = this.main.getServerHandler().convertLocationToString(blockLocation);
					
					this.main.getServerHandler().treasureList.add(location);
					
					p.sendMessage("§6§lKincs §8§l| §6Sikeresen lehelyeztél egy kincset!");
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
		
		if(!p.getInventory().contains(Material.PLAYER_HEAD)) {
			ItemStack profileItem = utils.getProfileItem(p);
			
			p.getInventory().addItem(profileItem);
			
			this.main.getServerHandler().profileItems.put(p, profileItem);
		}
		
		if(!p.hasPlayedBefore()) {
			this.main.getServerHandler().collectedTreasures.put(p.getUniqueId().toString(), new ArrayList<String>());
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		this.main.getServerHandler().profileItems.remove(p);
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		
		if(p.getWorld().getBlockAt(e.getTo()).getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
			p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0F, 1.0F);
			
			p.setVelocity(new Vector(0, 1, 2));
		}
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Block block = e.getClickedBlock();
			Location blockLocation = block.getLocation();
			
			if(block.getType() == Material.PLAYER_HEAD) {
				String location = this.main.getServerHandler().convertLocationToString(blockLocation);
				
				if(main.getServerHandler().treasureList.contains(location)) {
					if(this.main.getServerHandler().isCollected(p, location)) {
						p.sendMessage("§6§lKincs §8§l| §cEzt a kincset már megtaláltad!");
						
						return;
					}
					
					ArmorStand armorStand = (ArmorStand) blockLocation.getWorld().spawn(blockLocation.clone().add(0.5, 0.0, 0.5).subtract(0.0, 1.3, 0.0), ArmorStand.class);
					
					armorStand.setCustomName("§6Találtál egy kincset!");
					armorStand.setCustomNameVisible(true);
					armorStand.setGravity(false);
					armorStand.setVisible(false);
					armorStand.setInvulnerable(true);
					
					Firework firework = (Firework) blockLocation.getWorld().spawn(blockLocation.clone().add(0.5, 0.0, 0.5), Firework.class);
					FireworkMeta meta = firework.getFireworkMeta();
					
					meta.addEffect(FireworkEffect.builder().flicker(true).withColor(Color.fromBGR(224, 243, 132)).trail(true).build());
					meta.setPower(1);
					
					firework.setFireworkMeta(meta);
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(this.main, () -> {
						firework.detonate();
					}, 5L);
					
					this.main.getServerHandler().collectTreasure(p, location);
					
					Bukkit.getScheduler().scheduleSyncDelayedTask(this.main, () -> {
						armorStand.remove();
					}, 100L);
				}
			}
		}
		
		if(e.getAction() == Action.PHYSICAL)
			if(e.getClickedBlock().getType() == Material.FARMLAND)
				e.setCancelled(true);
		
		if(e.getAction() == Action.RIGHT_CLICK_AIR) {
			ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
			
			if(item.getType() == Material.PLAYER_HEAD && item.hasItemMeta()) {
				if(item.getItemMeta().hasDisplayName()) {
					if(item.getItemMeta().getDisplayName().equals("§6Profilod")) {
						new ProfileGui().openGui(p);
					}
				}
 			}
		}
				
	}
	
	@EventHandler
	public void onInteractAt(PlayerInteractAtEntityEvent e) {
		Player p = e.getPlayer();
		Entity entity = e.getRightClicked();
		
		if(entity instanceof ArmorStand) {
			ItemStack item = p.getInventory().getItemInMainHand();
			
			if(item.hasItemMeta()) {
				ItemMeta meta = item.getItemMeta();
				
				if(meta.hasDisplayName()) {
					if(meta.getDisplayName().equals(utils.getArmorStandRemover().getItemMeta().getDisplayName())) {
						entity.remove();
						
						p.getWorld().playEffect(entity.getLocation(), Effect.INSTANT_POTION_BREAK, 1);
						
						p.sendMessage("§a§lElvátolító§8§l| §dPuff");
					}
				}
				
			}
		}
	}

}
