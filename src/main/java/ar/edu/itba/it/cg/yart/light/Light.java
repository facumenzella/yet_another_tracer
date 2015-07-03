package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

public interface Light {

	public Vector3d getDirection(final ShadeRec sr);
	public Color L(final ShadeRec sr);
	public abstract boolean inShadow(final Ray ray, final ShadeRec sr, final Stack stack);
	public void shadowsOn();
	public void shadowsOff();
	public boolean castShadows();
	
}
