package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public interface Light {

	public Vector3d getDirection(final ShadeRec sr);
	public Color L(final ShadeRec sr);
	public abstract boolean inShadow(final Ray ray, final ShadeRec sr);
	public void shadowsOn();
	public void shadowsOff();
	public boolean castShadows();
	
}
