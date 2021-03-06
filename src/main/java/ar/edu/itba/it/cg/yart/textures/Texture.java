package ar.edu.itba.it.cg.yart.textures;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

public abstract class Texture {
	
	public abstract Color getColor (final ShadeRec sr);
	public abstract Texture complement();
	
}
