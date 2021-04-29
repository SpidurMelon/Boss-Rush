package nl.br.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import en.lib.drawing.DrawUtils;
import en.lib.setup.Panel;
import nl.br.main.BossRush;

public class LoadingScreen extends Panel {
	private static final Rectangle loadingBox = new Rectangle(0, 0, BossRush.WIDTH, BossRush.HEIGHT);
	public static boolean loading = true;
	public static String loadingLabel = "Loading...";
	public int loadBarWidth = 300;
	public static double loadPercent = 0.0;
	public static Color loadingBGColor = Color.WHITE;
	
	public LoadingScreen() {
		setBounds(0, 0, BossRush.WIDTH, BossRush.HEIGHT);
	}
	
	public void tick(double delta) {
		
	}
	
	public void draw(Graphics2D g2) {
		g2.setColor(loadingBGColor);
		g2.fillRect(0, 0, BossRush.WIDTH, BossRush.HEIGHT);
		
		g2.setColor(Color.BLACK);
		g2.setFont(new Font("Arial", 0, 30));
		DrawUtils.drawStringInBox(loadingLabel, loadingBox, g2);
		
		g2.drawRect(BossRush.WIDTH/2-loadBarWidth/2, BossRush.HEIGHT/2+100, loadBarWidth, 30);
		g2.setColor(Color.GREEN);
		g2.fillRect(BossRush.WIDTH/2-loadBarWidth/2, BossRush.HEIGHT/2+100, (int)(loadBarWidth*loadPercent), 30);
	}
}
