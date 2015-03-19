package ar.edu.itba.it.cg.yart.light.materials;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public abstract class MaterialAbstract implements Material{
	
	public abstract Color shade(final ShadeRec sr);

}
