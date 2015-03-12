package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public abstract class Light {
	
	protected boolean shadows;
	
	public abstract Vector3d getDirection();
	public abstract Color L(final ShadeRec sr);
}
