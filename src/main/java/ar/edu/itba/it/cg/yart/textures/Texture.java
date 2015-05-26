package ar.edu.itba.it.cg.yart.textures;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public abstract class Texture {
	
	public abstract Color getColor (final ShadeRec sr);
	
}
