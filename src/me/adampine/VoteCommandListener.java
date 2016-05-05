package me.adampine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class VoteCommandListener implements CommandExecutor {
	private ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	private Statement stmt = null;
	private ResultSet rs = null;
	private String query = null;
	// private HashMap<String, Integer> topVoters = new HashMap<String,
	// Integer>();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg1, String[] args) {
		/*
		 * SELECT uuid, COUNT( uuid ) AS votecount FROM `VOTES` WHERE
		 * MONTH(voteDate)=4 GROUP BY uuid ORDER BY votecount DESC
		 */
		int month = 0;
		if (args.length < 1) {
			SimpleDateFormat format = new SimpleDateFormat("MM");
			Calendar cal = Calendar.getInstance();
			String date = format.format(cal.getTime());
			month = Integer.parseInt(date);
		} else {
			month = Integer.parseInt(args[0]);
		}

		if (month >= 1 && month <= 12) {
			try {
				stmt = VoteCounterMain.getConnection().createStatement();
				query = "SELECT uuid, COUNT( uuid ) AS votecount FROM `VOTES` WHERE MONTH(voteDate)=" + month
						+ " GROUP BY uuid ORDER BY votecount DESC";
				console.sendMessage(query);
				rs = stmt.executeQuery(query);
				sender.sendMessage(ChatColor.YELLOW + "----------Top Voters-----------");
				int i = 0;
				while (rs.next()) {
					String stringUUID = rs.getString("uuid");
					UUID uuid = UUID.fromString(stringUUID);
					String playerName = Bukkit.getOfflinePlayer(uuid).getName();
					int voteCount = rs.getInt("voteCount");
					if (i <= 5) {
						sender.sendMessage(ChatColor.YELLOW + playerName + ": " + ChatColor.AQUA + voteCount);
					}
					i++;
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) {
					} // ignore
					rs = null;
				}
				if (stmt != null) {
					try {
						stmt.close();
					} catch (SQLException sqlEx) {
					} // ignore
					stmt = null;
				}
			}
		}

		return true;
	}
}
