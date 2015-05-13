package ar.edu.itba.it.cg.yart.textures;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.mapping.Mapping;

public class ImageTexture extends Texture {

	private BufferedImage image;
	private byte[] pixels;
	private int hres;
	private int vres;
	private Mapping mapping;
	private boolean hasAlphaChannel;

	public ImageTexture(final BufferedImage img, final Mapping mapping) {
		this.mapping = mapping;
		setImage(img);

	}

	public void setImage(final BufferedImage image) {
		this.image = image;
		vres = image.getHeight();
		hres = image.getWidth();
		pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		hasAlphaChannel = image.getAlphaRaster() != null;
	}

	@Override
	public Color getColor(ShadeRec sr) {
		final Point coordinates = new Point(0, 0);
		if (mapping != null) {
			mapping.getTexetlCoordinates(sr.localHitPoint, hres, vres,
					coordinates);
		} else {
			int row = (int) (sr.v * (vres - 1));
			int column = (int) (sr.u * (hres - 1));
			coordinates.setLocation(row, column);
		}
//		int row = (int) coordinates.getX();
//		int column = (int) coordinates.getY();
//		final double red;
//		final double green;
//		final double blue;
//		
//		if (hasAlphaChannel) {
//			final int pixelLength = 4;
//			row*= (hres)*pixelLength;
//			column*= pixelLength;	
//			blue = ((int) pixels[row+column + 1])/255.0; // blue
//			green = (((int) pixels[row+column + 2]))/255.0; // green
//			red = (((int) pixels[row+column + 3]))/255.0; // red
//		} else {
//			final int pixelLength = 3;
//			column*= (hres)*pixelLength;
//			row = ((vres - 1) - row)*pixelLength;	
//			blue = ((int) pixels[row+column])/255.0; // blue
//			green = (((int) pixels[row+column + 1]))/255.0; // green
//			red = (((int) pixels[row+column + 2]))/255.0; // red
//		}
		
		int color = image.getRGB((int) coordinates.getX(),
				(vres - 1) - (int) coordinates.getY());
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		double red = r / 255.0;
		double green = g / 255.0;
		double blue = b / 255.0;
		return new Color(red, green, blue);
	}

}
