package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;
import ar.edu.itba.it.cg.yart.transforms.Transformable;

public abstract class AbstractLight implements Light, Transformable {

	protected boolean shadows;

	public abstract Vector3d getDirection(final ShadeRec sr);

	public abstract Color L(final ShadeRec sr);

	public abstract boolean inShadow(final Ray ray, final ShadeRec sr, final Stack stack);

	public AbstractLight() {
		shadows = true;
	}

	public boolean castShadows() {
		return shadows;
	}

	public void shadowsOn() {
		shadows = true;
	}

	public void shadowsOff() {
		shadows = false;
	}

}
