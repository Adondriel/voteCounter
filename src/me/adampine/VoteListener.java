package me.adampine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

public class VoteListener implements Listener {
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	Statement stmt = null;
	ResultSet rs = null;
	String query = null;

	@EventHandler(priority = EventPriority.NORMAL)
	public void onVotifierEvent(VotifierEvent event) {
		Vote vote = event.getVote();
		// votifier sends username, not uuid.
		String uuid = Bukkit.getOfflinePlayer(vote.getUsername()).getUniqueId().toString();
		// mysql datetime format: DATETIME - format: YYYY-MM-DD
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
		String date = format.format(cal.getTime());

		try {
			stmt = VoteCounterMain.getConnection().createStatement(); 
			query = "INSERT INTO VOTES (uuid, voteDate) VALUES ('" + uuid + "', '" + date + "')";
			console.sendMessage(query);
			stmt.executeUpdate(query);
			console.sendMessage("inserted a row in the database.");
		} catch (SQLException ex) {
			// handle any errors
			console.sendMessage("SQLException: " + ex.getMessage());
			console.sendMessage("SQLState: " + ex.getSQLState());
			console.sendMessage("VendorError: " + ex.getErrorCode());
		} finally {
			// it is a good idea to release
			// resources in a finally{} block
			// in reverse-order of their creation
			// if they are no-longer needed
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
}
