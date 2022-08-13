package fr.jourboon.launcher;

import java.awt.Color;
import java.io.File;

import javax.swing.JFrame;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import fr.theshark34.openlauncherlib.util.CrashReporter;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.util.WindowMover;

@SuppressWarnings("serial")
public class LauncherFrame extends JFrame{
	
	private static LauncherFrame instance;
	private LauncherPanel launcherPanel;
	private static CrashReporter crashReporter;
	
	public static File ramFile = new File(Launcher.D_DIR, "ram.txt");
	
	public LauncherFrame() {
		this.setTitle("Exodya Launcher");
		this.setSize(962, 547);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setUndecorated(true);
		this.setIconImage(Swinger.getResource("favicon.png"));
		this.setBackground(new Color(0, 0, 0, 0));
		this.setContentPane(launcherPanel = new LauncherPanel());
		this.setBackground(Swinger.TRANSPARENT);
		
		WindowMover mover = new WindowMover(this);
		this.addMouseListener(mover);
		this.addMouseMotionListener(mover);
		
		this.setVisible(true);
		
	}
	
	
	public static void main(String[] args) {
		Swinger.setSystemLookNFeel();
		Swinger.setResourcePath("/fr/jourboon/launcher/resources/");
		Launcher.D_CRASHES_DIR.mkdirs();
		crashReporter = new CrashReporter("Exodya | Launcher", Launcher.D_CRASHES_DIR);
		
		instance = new LauncherFrame();
		
		  DiscordRPC lib = DiscordRPC.INSTANCE;
	        String applicationId = "774308667646148650";
	        String steamId = "";
	        DiscordEventHandlers handlers = new DiscordEventHandlers();
	        lib.Discord_Initialize(applicationId, handlers, true, steamId);
	        DiscordRichPresence presence = new DiscordRichPresence();
	        presence.startTimestamp = System.currentTimeMillis() / 1000; // epoch second
	        presence.details = "Joue à Exodya";
	        presence.largeImageKey = "exodya_rpc2";
	        presence.smallImageKey = "favicon2";
	        presence.state = "In Game";
	        lib.Discord_UpdatePresence(presence);
	        // in a worker thread
	        new Thread(() -> {
	            while (!Thread.currentThread().isInterrupted()) {
	                lib.Discord_RunCallbacks();
	                try {
	                    Thread.sleep(2000);
	                } catch (InterruptedException ignored) {}
	            }
	        }, "RPC-Callback-Handler").start();
	    }

	
	public static LauncherFrame getInstance() {
		return instance;
	}
	
	public static CrashReporter getCrashReporter() {
	    return crashReporter;
	  }
	
	public LauncherPanel getLauncherPanel() {
		return this.launcherPanel;
	}
}
