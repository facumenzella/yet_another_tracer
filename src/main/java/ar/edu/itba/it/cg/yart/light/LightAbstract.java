package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public abstract class LightAbstract implements Light{
	
	protected boolean shadows;
	
	public abstract Vector3d getDirection(final ShadeRec sr);
	public abstract Color L(final ShadeRec sr);
}
