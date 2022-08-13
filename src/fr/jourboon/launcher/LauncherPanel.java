package fr.jourboon.launcher;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import fr.theshark34.openauth.AuthenticationException;
import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.util.Saver;
import fr.theshark34.openlauncherlib.util.ramselector.RamSelector;
import fr.theshark34.swinger.Swinger;
import fr.theshark34.swinger.colored.SColoredBar;
import fr.theshark34.swinger.event.SwingerEvent;
import fr.theshark34.swinger.event.SwingerEventListener;
import fr.theshark34.swinger.textured.STexturedButton;

@SuppressWarnings("serial")
public class LauncherPanel extends JPanel implements SwingerEventListener{
	
	private Image background = Swinger.getResource("background4.png");
	private Saver saver = new Saver(new File(Launcher.D_DIR, "launcher.properties"));
	private JTextField usernameField = new JTextField(saver.get("username"));
	
	private STexturedButton playButton = new STexturedButton(Swinger.getResource("playButton.png"));
	private STexturedButton closeButton = new STexturedButton(Swinger.getResource("close_hover.png"), Swinger.getResource("close_hover.png"),Swinger.getResource("close_hover.png"));
	private STexturedButton discordButton = new STexturedButton(Swinger.getResource("discord.png"), Swinger.getResource("discord_hover.png"));
	private STexturedButton optionButton = new STexturedButton(Swinger.getResource("option.png"));
	
	private SColoredBar progressBar = new SColoredBar(Swinger.getTransparentWhite(100), Swinger.getTransparentWhite(175));
	private JLabel infoLabel = new JLabel("Cliquez sur jouer pour commencer!", SwingConstants.CENTER);
	
	private RamSelector ramSelector = new RamSelector(new File(Launcher.D_DIR, "ram.txt"));
	
	
	public LauncherPanel() {
		this.setLayout(null);
		
		usernameField.setForeground(Color.DARK_GRAY);
		usernameField.setFont(usernameField.getFont().deriveFont(22F));
		usernameField.setBounds(170, 280, 230, 35);
		usernameField.setBorder(null);
		usernameField.setOpaque(false);
		this.add(usernameField);
		
		playButton.setBounds(130, 360);
		playButton.addEventListener(this);
		playButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(playButton);
		
		closeButton.setBounds(885, 5);
		closeButton.addEventListener(this);
		closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(closeButton);
		
		discordButton.setBounds(5, 5);
		discordButton.addEventListener(this);
		discordButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(discordButton);
		
		optionButton.setBounds(250, 360);
		optionButton.addEventListener(this);
		optionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		this.add(optionButton);
		
		progressBar.setBounds(2, 531, 960, 20);
		this.add(progressBar);
		
		infoLabel.setBounds(350, 500, 330, 20);
		infoLabel.setForeground(Color.DARK_GRAY);
		infoLabel.setFont(infoLabel.getFont().deriveFont(16F));
		this.add(infoLabel);
		
	}


	@Override
	public void onEvent(SwingerEvent e) {
		if(e.getSource() == playButton) {
			setFieldsEnabled(false);
			
			if(usernameField.getText().replaceAll(" ", "").length() == 0 || usernameField.getText().replaceAll(" ", "").length() == 1 || usernameField.getText().replaceAll(" ", "").length() == 2 || usernameField.getText().replaceAll(" ", "").length() == 3) {
				JOptionPane.showMessageDialog(this, "Erreur, veuillez entrer un pseudo valide. Ou un pseudo de plus de 3 caractères", "Erreur", JOptionPane.ERROR_MESSAGE);
				setFieldsEnabled(true);
				return;
			}
			
			Thread t = new Thread() {
				@Override
				public void run() {
					try {
						Launcher.auth(usernameField.getText(), "");
					} catch (AuthenticationException e) {
						JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, impossible de se connecter");
						setFieldsEnabled(true);
						return;
				}
					
					
					try {
						Launcher.update();
					} catch (Exception e) {
						Launcher.interruptThread();
						LauncherFrame.getCrashReporter().catchError(e, "Impossible de mettre a jour Exodya :/");
						JOptionPane.showMessageDialog(LauncherPanel.this, "Erreur, impossible de mettre le jeu a jour ");
						setFieldsEnabled(true);
						return;
				}
					
					try 
					{
						Launcher.launch();
					} catch (LaunchException e) {
						LauncherFrame.getCrashReporter().catchError(e, "Impossible de lancer Exodya :/");
				}
				
				System.out.println("No problem detected.");
				}
			};
		t.start();
		}else if(e.getSource() == closeButton)
            System.exit(0);
		else if (e.getSource() == this.optionButton) {
			ramSelector.display();
		  	ramSelector.display().setTitle("Choix de la ram.");
		  	ramSelector.display().setIconImage(LauncherFrame.getInstance().getIconImage());
		}
		
        else if (e.getSource() == this.discordButton){
            Desktop d = Desktop.getDesktop();
            try { 
                    d.browse(new URI("https://discord.gg/4wpw3D4"));

            } catch (IOException|URISyntaxException localIOException) {}
        }
		
    }
	
	
	@Override
	public void paintComponent(Graphics graph) {
		super.paintComponent(graph);
	    graph.drawImage(this.background, 0, 0, getWidth(), getHeight(), this);
	    
	    this.setBackground(Swinger.TRANSPARENT);
	}
	
	public void setFieldsEnabled(boolean enabled) {
		usernameField.setEnabled(enabled);
		playButton.setEnabled(enabled);
		closeButton.setEnabled(enabled);
	}
	
	public SColoredBar getProgressBar() {
		return progressBar;
	}
	
	public void setInfoText(String text) {
		infoLabel.setText(text);
	}
	
	public RamSelector getRamSelector() {
		return ramSelector;
	}

}
