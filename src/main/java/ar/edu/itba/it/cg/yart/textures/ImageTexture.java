package ar.edu.itba.it.cg.yart.textures;

import java.awt.image.BufferedImage;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.mapping.Mapping;

public class ImageTexture extends Texture{
	
	private BufferedImage image;
	private int hres;
	private int vres;
	private Mapping mapping;
	
	
	@Override
	public Color getColor(ShadeRec sr) {
		int row = 0;
		int column = 0;
		if(mapping != null) {
			mapping.getTexetlCoordinates(sr.localHitPoint, hres, vres);
		} else {
			row = (int) (sr.v * (vres - 1));
			column = (int) (sr.u * (hres - 1));
		}
		int color = image.getRGB(row, column);
		return null;
	}

}
