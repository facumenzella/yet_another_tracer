package ar.edu.itba.it.cg.yart.textures.wrappers;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import ar.edu.itba.it.cg.yart.color.Color;

public abstract class Wrapper {
	
	protected BufferedImage img;
	protected int hres;
	protected int vres;
	private byte[] pixels;
 	private boolean hasAlphaChannel;
	
	
	public void setImage(final BufferedImage img) {
		this.img = img;
		vres = img.getHeight();
		hres = img.getWidth();
		pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
 		hasAlphaChannel = img.getAlphaRaster() != null;
	}
	
	public abstract Color wrap(final double u, final double v);
	
	protected Color getColor(int row, int column) {		
		int color = img.getRGB(row,column);
		
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		double red = r / 255.0;
		double green = g / 255.0;
		double blue = b / 255.0;
		return new Color(red, green, blue);
	}
}
