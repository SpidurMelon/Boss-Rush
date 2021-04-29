package nl.br.panels;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import en.lib.input.Button;
import en.lib.input.Menu;
import en.lib.setup.Panel;
import en.lib.sounds.AudioMaster;
import nl.br.main.BossRush;

public class MainMenu extends Panel {
	public Rectangle bounds = new Rectangle(0, BossRush.HEIGHT-400, 400, 400);
	public Menu menu = new Menu(0, 0, bounds.width, bounds.height) {
		public ArrayList<Button> getButtons() {
			ArrayList<Button> buttons = new ArrayList<Button>();
			
			buttons.add(new Button("Start") {
				public void onClick() {
					BossRush.frame.removePanel(BossRush.menu);
				}
			});
			
			buttons.add(new Button("Quit") {
				public void onClick() {
					AudioMaster.cleanUp();
					System.exit(0);
				}
			});
			
			return buttons;
		}
	};
	public MainMenu() {
		setBounds(bounds);
		
		addMouseListener(menu);
	}
	
	public void tick(double delta) {
		
	}

	public void draw(Graphics2D g2) {
		menu.draw(g2);
	}

}
