package ar.edu.itba.it.cg.yart.light.materials;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public interface Material {

	public Color shade(final ShadeRec sr);
	
}
