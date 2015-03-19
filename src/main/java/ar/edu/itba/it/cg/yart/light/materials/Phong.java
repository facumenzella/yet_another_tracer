package ar.edu.itba.it.cg.yart.light.materials;

import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.brdf.GlossySpecular;
import ar.edu.itba.it.cg.yart.light.brdf.Lambertian;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Phong extends MaterialAbstract{
	
	private final Lambertian ambientBRDF = new Lambertian();
	private final Lambertian diffuseBRDF = new Lambertian();
	private final GlossySpecular specularBRDF = new GlossySpecular();
	@Override
	public Color shade(ShadeRec sr) {
		final Vector3d wo = sr.ray.direction.inverse();
		final Color colorL = ambientBRDF.rho(sr, wo).multiply(sr.world.getAmbientLight().L(sr));
		final List<Light> lights = sr.world.getLights();
		
		for(final Light light : lights) {
			final Vector3d wi = light.getDirection(sr);
			double ndotwi = sr.normal.dot(wi);
			
			if (ndotwi > 0.0) {
				final Color aux = (diffuseBRDF.f(sr, wo, wi).add(specularBRDF.f(sr, wo, wi))).multiply(light.L(sr)).multiply(ndotwi);
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
	
	public void setKs(final double ks) {
		specularBRDF.setKs(ks);
	}
	
	public void setCd(final Color cd) {
		ambientBRDF.setCd(cd);
		diffuseBRDF.setCd(cd);
		specularBRDF.setCs(cd);
	}
	
	public void setExp(final double exp) {
		specularBRDF.setExp(exp);
	}

}
