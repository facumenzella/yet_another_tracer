package ar.edu.itba.it.cg.yart.light;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.light.brdf.Lambertian;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Matte extends Material {
	
	private Lambertian ambientBRDF = new Lambertian();
	private Lambertian diffuseBRDF = new Lambertian();
	

	@Override
	public Color shade(ShadeRec sr) {
		//TODO: PAGE 271
		return null;
	}
	
}
