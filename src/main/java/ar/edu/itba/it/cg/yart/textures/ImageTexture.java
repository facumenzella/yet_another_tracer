package ar.edu.itba.it.cg.yart.textures;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.textures.mapping.Mapping;
import ar.edu.itba.it.cg.yart.textures.wrappers.Wrapper;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

public class ImageTexture extends Texture {

//	private BufferedImage image;
//	private byte[] pixels;
	private int hres;
	private int vres;
	private Mapping mapping;
//	private boolean hasAlphaChannel;
	private Wrapper wrapper;

	public ImageTexture(final BufferedImage img, final Mapping mapping,
			final Wrapper wrapper) {
		this.mapping = mapping;
		this.wrapper = wrapper;
		setImage(img);

	}

	public void setImage(final BufferedImage image) {
		this.wrapper.setImage(image);
		vres = image.getHeight();
		hres = image.getWidth();
//		pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
//		hasAlphaChannel = image.getAlphaRaster() != null;
	}

	@Override
	public Color getColor(ShadeRec sr) {
		final Point coordinates = new Point(0, 0);
		if (mapping != null) {
			mapping.getTexetlCoordinates(sr.localHitPoint, hres, vres,
					coordinates);
		} else {
			int x = (int) (sr.v * (vres - 1));
			int y = (int) (sr.u * (hres - 1));
			coordinates.setLocation(x, y);
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
//	}
		return wrapper.wrap(coordinates.getX(), coordinates.getY());
	}

}
