package ar.edu.itba.it.cg.yart.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class RenderResult extends JComponent {

	private static final long serialVersionUID = 1L;
	
	private final BufferedImage bi;
	
	public RenderResult(final BufferedImage bi) {
		this.bi = bi;
		
		setPreferredSize(new Dimension(bi.getWidth(), bi.getHeight()));
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(bi, 0, 0, null);
	}

}
