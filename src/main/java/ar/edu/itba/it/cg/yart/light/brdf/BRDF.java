package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.samplers.Sampler;

public abstract class BRDF {
	
	protected Vector3d normal;
	protected Sampler sampler;
	
	public void setSampler(final Sampler sampler) {this.sampler = sampler;}
	
	public abstract Color f(final ShadeRec sr, final Vector3d wo, final Vector3d wi);
	public abstract Color rho(final ShadeRec sr, final Vector3d wo);
	public abstract Color sample_f(final ShadeRec sr, final Vector3d wo, final Vector3d wi);
}
