package eu.playeruion.lobby.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import eu.playeruion.lobby.LobbyCore;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;

public class CitizensListener implements Listener {
	
	private LobbyCore main = LobbyCore.getInstance();
	
	@EventHandler
	public void onPlayerInteractNPC(NPCRightClickEvent e) {
		NPC npc = e.getNPC();
		Player p = e.getClicker();
		
		String name = npc.getName();
		
		if(!name.startsWith("§")) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			
			out.writeUTF("Connect");
			out.writeUTF(name);
			
			p.sendPluginMessage(this.main, "BungeeCord", out.toByteArray());
		}
	}

}
