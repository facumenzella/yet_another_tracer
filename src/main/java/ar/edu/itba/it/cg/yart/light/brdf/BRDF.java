package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.samplers.NRooks;
import ar.edu.itba.it.cg.yart.samplers.Sampler;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;

public abstract class BRDF {
	
	protected Vector3d normal;
	protected Sampler sampler = new NRooks(1, 10000);
	
	public void setSampler(final Sampler sampler) {
		this.sampler = sampler;
		this.sampler.mapSamples2Hemisphere(1);
	}
	
	public abstract Color f(final ShadeRec sr, final Vector3d wo, final Vector3d wi);
	public abstract Color rho(final ShadeRec sr, final Vector3d wo);
	public abstract Color sample_f(final ShadeRec sr, final Vector3d wo, final Vector3d wi, final PDF pdf);
}
