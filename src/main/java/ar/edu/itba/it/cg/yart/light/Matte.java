package ar.edu.itba.it.cg.yart.light;


import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.brdf.Lambertian;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Matte extends Material {
	
	private Lambertian ambientBRDF = new Lambertian();
	private Lambertian diffuseBRDF = new Lambertian();
	

	@Override
	public Color shade(ShadeRec sr) {
		final Vector3d wo = sr.ray.direction.inverse();
		final Color colorL = ambientBRDF.rho(sr, wo).multiply(sr.world.ambientLight.L(sr));
		final List<Light> lights = sr.world.lights;
		
		for(final Light light : lights) {
			final Vector3d wi = light.getDirection(sr);
			double ndotwi = sr.normal.dot(wi);
			
			if (ndotwi > 0.0) {
				final Color aux = diffuseBRDF.f(sr, wo, wi).multiply(light.L(sr)).multiply(ndotwi);
				colorL.addEquals(aux);
			}
		}
		
		return colorL;
	}
	
	public void setKa(final double ka) {
		ambientBRDF.setKd(ka);
	}
	
	public void setKd(final double kd) {
		diffuseBRDF.setKd(kd);
	}
	
	public void setCd(final Color cd) {
		ambientBRDF.setCd(cd);
		diffuseBRDF.setCd(cd);
	}
	
}
