package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Directional extends Light{
	
	private final double ls = 2.0;
	private final Color color = Color.whiteColor();
	private final Vector3d dir = Vector3d.normalize(new Vector3d(30,0,0));
	


	@Override
	public Vector3d getDirection(ShadeRec sr) {
		return dir;
	}

	@Override
	public Color L(ShadeRec sr) {
		return color.multiply(ls);
	}

}
