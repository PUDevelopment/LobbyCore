package eu.playeruion.lobby.handlers;

import org.bukkit.entity.Player;

import eu.playeruion.lobby.LobbyCore;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderHandler extends PlaceholderExpansion {

	private LobbyCore main = LobbyCore.getInstance();
	
	@Override
	public String getIdentifier() {
		return this.main.getDescription().getName().toLowerCase();
	}

	@Override
	public String getAuthor() {
		return this.main.getDescription().getAuthors().toString();
	}

	@Override
	public String getVersion() {
		return this.main.getDescription().getVersion();
	}
	
	public String onPlaceholderRequest(Player p, String id) {
		String response = "";
		
		if(id.equalsIgnoreCase("kincsek_megtalalt"))
			return "" + this.main.getServerHandler().collectedTreasures.get(p.getUniqueId().toString()).size();
		
		if(id.equalsIgnoreCase("kincsek_osszes"))
			return "" + this.main.getServerHandler().treasureList.size();
		
		return response;
	}

}
