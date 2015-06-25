package ar.edu.itba.it.cg.yart.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;

public class RenderImageResult extends JComponent {

	private static final long serialVersionUID = 1L;
	
	private BufferedImage bufferedImage;
	
	
	public void setBufferedImage(final BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
		setPreferredSize(new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight()));
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.drawImage(bufferedImage, 0, 0, null);
	}

}
