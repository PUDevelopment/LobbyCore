package eu.playeruion.lobby;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class Utils {
	
	private LobbyCore main = LobbyCore.getInstance();
	private Reflection reflection = new Reflection();
	
	public String getCurrentDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
		
		return format.format(new Date());
	}
	
	public String generateUniqueId() {
		return RandomStringUtils.randomAlphabetic(5).toLowerCase();
	}
	
	public ItemStack getTreasure() {
		String texture = main.getConfig().getString("beallitasok.ajandekTextura");
		GameProfile profile = new GameProfile(UUID.randomUUID(), "Ajandek");
		
		profile.getProperties().put("textures", new Property("textures", texture));
		
		ItemStack item = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (byte) 3);
		ItemMeta meta = item.getItemMeta();
		Class<?> metaClass = meta.getClass();
		
		reflection.getField(metaClass, "profile", GameProfile.class).set(meta, profile);
		
		meta.setDisplayName("§6Kincs");
		meta.setLore(Arrays.asList("§7Kérlek, helyezd le ezt a kincset", "§7valahová, hogy a játékosok", "§7később felvehessék!"));
		
		item.setItemMeta(meta);
		
		return item;
	}
	
	public ItemStack getArmorStandRemover() {
		ItemStack item = new ItemStack(Material.STICK);
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName("§aFaszom ArmorStand");
		meta.setLore(Arrays.asList("§7Kattints rá, azt eltűnik."));
		
		item.setItemMeta(meta);
		
		return item;
	}

}
