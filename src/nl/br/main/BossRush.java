package nl.br.main;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import en.lib.setup.RegularFrame;
import en.lib.sounds.AudioMaster;
import nl.br.panels.DrawPanel;
import nl.br.panels.LoadingScreen;
import nl.br.panels.MainMenu;

public class BossRush {
	private static final boolean fullScreen = true;
	public static int WIDTH, HEIGHT;
	public static RegularFrame frame;
	public static DrawPanel drawPanel;
	public static MainMenu menu;
	public static LoadingScreen loadingScreen;
	public static void main(String[] args) {
		AudioMaster.init();
		AudioMaster.setListenerData();
		
		if (fullScreen) {
			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			WIDTH = gd.getDisplayMode().getWidth();
			HEIGHT = gd.getDisplayMode().getHeight();
		} else {
			WIDTH = 800;
			HEIGHT = 800;
		}
		
		frame = new RegularFrame(WIDTH, HEIGHT, fullScreen);
		
		//menu = new MainMenu();
		//frame.addPanel(menu);
		
		loadingScreen = new LoadingScreen();
		frame.addPanel(loadingScreen);
		
		drawPanel = new DrawPanel();
		frame.addPanel(drawPanel);
	}
}
