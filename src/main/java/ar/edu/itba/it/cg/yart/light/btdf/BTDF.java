package ar.edu.itba.it.cg.yart.light.btdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

public abstract class BTDF {
	
	protected Vector3d normal;
	
	public abstract Color f(final ShadeRec sr, final Vector3d wo, final Vector3d wi);
	public abstract Color rho(final ShadeRec sr, final Vector3d wo);
	public abstract Color sample_f(final ShadeRec sr, final Vector3d wo, final Vector3d wi);
	public abstract boolean tir(final ShadeRec sr);

}
