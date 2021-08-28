package eu.playeruion.lobby.gui;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import eu.playeruion.lobby.LobbyCore;
import eu.playeruion.lobby.Reflection;
import me.clip.placeholderapi.PlaceholderAPI;

public class ProfileGui {
	
	private LobbyCore main = LobbyCore.getInstance();
	private Reflection reflection = new Reflection();
	
	public void openGui(Player p) {
		Inventory inv = Bukkit.createInventory(p, 45, "§aProfilod megtekintése");
		
		inv.setItem(4, this.getProfileItem(p)); // Profil leírás
		inv.setItem(19, getGift()); // Ajándékok menüpont
		inv.setItem(22, this.getGod()); // Istenek menüpont
		
		for(int i = 0; i < 9; i++)
			if(inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR)
				inv.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE)); // Tetejének a feltöltése
		
		for(int i = 36; i < 45; i++)
			if(inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR)
				inv.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE)); // Aljának a feltöltése
		
		p.openInventory(inv);
	}
	
	private ItemStack getProfileItem(Player p) {
		GameProfile owner = ((CraftPlayer) p).getProfile();
		GameProfile profile = new GameProfile(UUID.randomUUID(), p.getName());
		
		owner.getProperties().get("textures").forEach(property -> profile.getProperties().put("textures", property));
		
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		ItemMeta meta = item.getItemMeta();
		Class<?> metaClass = meta.getClass();
		
		this.reflection.getField(metaClass, "profile", GameProfile.class).set(meta, profile);
		
		meta.setDisplayName(PlaceholderAPI.setPlaceholders(p, "%luckperms_prefix%") + p.getName());
		meta.setLore(Arrays.asList("§7Szinted: §a" + PlaceholderAPI.setPlaceholders(p, "%xp_szint%"), "§7Pontjaid: §a" + PlaceholderAPI.setPlaceholders(p, "%xp_mennyiseg%") + "/1000 XP", "§0", "§7Rangod: §a" + PlaceholderAPI.setPlaceholders(p, "%vault_rank%"), "§7Rangod lejárata: §a-", "§0", "§7Regisztráltál: §a2021. 08. 19."));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack getGift() {
		ItemStack item = new ItemStack(Material.CHEST);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§6Ajándékaid");
		meta.setLore(Arrays.asList("§cJelenleg nincs egy ajándékod se :(", "§0", "§7Amennyiben szeretnél ajándékozni", "§7valamit egy játékosnak,", "§7végy a kezedbe egy tárgyat,", "§7majd használd a §a/ajandek §7parancsot!"));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack getGod() {
		ItemStack item = new ItemStack(Material.ELYTRA);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§6Képességeid");
		meta.setLore(Arrays.asList("§7Jelenlegi Istened: §asenki", "§7Fő képességed: §a-", "§7Elért szinted: §a0/0", "§0", "§7Aktiválható Áldásaid: §a0"));
		
		item.setItemMeta(meta);
		
		return item;
	}

}
