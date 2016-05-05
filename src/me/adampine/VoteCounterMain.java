package me.adampine;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteCounterMain extends JavaPlugin {
	ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
	// Connection vars
	protected static Connection connection = null;
	private static VoteCounterMain plugin;

	// This is the variable we will use to connect to database

	@Override
	public void onEnable() {
		plugin=this;
		Statement stmt = null;
		FileConfiguration config = this.getConfig();
		config.addDefault("username", "username");
		config.addDefault("password", "password");
		config.addDefault("url", "jdbc:mysql://127.0.0.1:3306/username");
		config.options().copyDefaults(true);
		saveConfig();

		String url = config.getString("url");
		String username = config.getString("username");
		String password = config.getString("password");
		// console.sendMessage(config.getString("username") +", "+
		// config.getString("password") + ", " + config.getString("url"));
		try {
			console.sendMessage("[VoteCounter] Connecting to database...");
			connection = DriverManager.getConnection(url, username, password);
			stmt = connection.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS VOTES " + "(uuid VARCHAR(255) not NULL, " + " voteDate DATE);";
			stmt.executeUpdate(sql);
			console.sendMessage("[VoteCounter] Connected to database.");
			getCommand("votetop").setExecutor(new VoteCommandListener());
			getServer().getPluginManager().registerEvents(new VoteListener(), this);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		try {
			if (connection != null && !connection.isClosed()) {
				connection.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() throws SQLException{
		if (connection.isClosed() || connection == null){
			FileConfiguration config = plugin.getConfig();
			String url = config.getString("url");
			String username = config.getString("username");
			String password = config.getString("password");
			try {
				connection = DriverManager.getConnection(url, username, password);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return connection;		
	}

}
