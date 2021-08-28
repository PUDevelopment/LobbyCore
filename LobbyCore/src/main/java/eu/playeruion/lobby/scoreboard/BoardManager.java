package eu.playeruion.lobby.scoreboard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import eu.playeruion.lobby.LobbyCore;
import eu.playeruion.lobby.Utils;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;

public class BoardManager {
	
	private LobbyCore main = LobbyCore.getInstance();
	private Utils utils = new Utils();
	
	private Scoreboard board;
	
	private HashMap<Integer, String> lineIndentifiers = new HashMap<Integer, String>();
	private List<String> lines = this.main.getConfig().getStringList("scoreboard.sorok");
	
	public void initBoard() {
		this.board = Bukkit.getScoreboardManager().getMainScoreboard();
		
		Iterator<Objective> objectiveIter = this.board.getObjectivesByCriteria("dummy").iterator();
		
		while(objectiveIter.hasNext()) {
			Objective obj = objectiveIter.next();
			
			obj.unregister();
		}
		
		Objective mainObjective = this.board.registerNewObjective("board-" + utils.generateUniqueId(), "dummy");
		
		mainObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		mainObjective.setDisplayName(ChatColor.translateAlternateColorCodes('&', this.main.getConfig().getString("scoreboard.fejlec")));
		
		// Lista megfordítása, mivel fejjel lefelé olvassa be.
		Collections.reverse(lines);
		
		Iterator<String> lineIter = lines.iterator();
		int lineNumber = 0;
		
		while(lineIter.hasNext()) {
			String line = ChatColor.translateAlternateColorCodes('&', lineIter.next());
			String lineId = this.calculatePlaceholders(lineNumber);
			
			if(this.board.getTeam("line-" + lineNumber) != null)
				this.board.getTeam("line-" + lineNumber).unregister();
			
			Team team = this.board.registerNewTeam("line-" + lineNumber);
			
			team.addEntry(lineId);
			team.setPrefix(line);
			
			mainObjective.getScore(lineId).setScore(lineNumber);
			
			this.lineIndentifiers.put(lineNumber, lineId);
			
			lineNumber++;
		}
		
		this.initUpdateThread();
	}
	
	public void updateScoreBoard(Player p) {
		if(p.getScoreboard() == null)
			p.setScoreboard(this.board);
		
		for(int i = 0; i < this.board.getEntries().size(); i++) {
			Team team = this.board.getTeam("line-" + i);
			
			team.setPrefix(this.replacePlaceholders(lines.get(i), p));
		}
	}
	
	private String calculatePlaceholders(int number) {
		String placeholder = "§0";
		
		for(int i = 0; i < number; i++) {
			placeholder += "§0";
		}
		
		return placeholder;
	}
	
	public void initUpdateThread() {
		Runnable updateTask = () -> {
			Bukkit.getOnlinePlayers().forEach(player -> this.updateScoreBoard(player));
		};
		
		Bukkit.getScheduler().runTaskTimerAsynchronously(main, updateTask, 0, 600);
	}
	
	private String replacePlaceholders(String text, Player p) {
		text = text.replaceAll("%jelenlegi_datum%", utils.getCurrentDate());
		text = PlaceholderAPI.setPlaceholders(p, text);
		
		return text;
	}

}
