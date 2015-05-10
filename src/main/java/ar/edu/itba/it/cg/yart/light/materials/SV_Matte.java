package ar.edu.itba.it.cg.yart.light.materials;

import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.light.Light;
import ar.edu.itba.it.cg.yart.light.brdf.SV_Lambertian;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class SV_Matte implements Material {
	
	private SV_Lambertian ambientBRDF = new SV_Lambertian();
	private SV_Lambertian diffuseBRDF = new SV_Lambertian();

	@Override
	public Color shade(ShadeRec sr) {
		final Vector3d wo = null; // not necessary
		// final Vector3d wo = sr.ray.direction.inverse();
		final Color colorL = ambientBRDF.rho(sr, wo);
		colorL.multiplyEquals(sr.world.getAmbientLight().L(sr));

		final List<Light> castShadowLights = sr.world.getCastShadowLights();
		final List<Light> doNotCastShadowLights = sr.world
				.getDoNotCastShadowLights();
		
		for (final Light light : doNotCastShadowLights) {
			final Vector3d wi = light.getDirection(sr);
			double ndotwi = sr.normal.dot(wi);
			
			if (ndotwi > 0.0) {
				final Color aux = diffuseBRDF.f(sr, wo, wi);
				aux.multiplyEquals(light.L(sr));
				aux.multiplyEquals(ndotwi);

				colorL.addEquals(aux);
			}
		}

		for (final Light light : castShadowLights) {
			final Vector3d wi = light.getDirection(sr);
			double ndotwi = sr.normal.dot(wi);

			if (ndotwi > 0.0) {
				boolean inShadow = false;
				Ray shadowRay = new Ray(sr.hitPoint, wi);
				inShadow = light.inShadow(shadowRay, sr);
				if (!inShadow) {
					final Color aux = diffuseBRDF.f(sr, wo, wi);
					aux.multiplyEquals(light.L(sr));
					aux.multiplyEquals(ndotwi);

					colorL.addEquals(aux);
				}
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

	public void setCd(final Texture cd) {
		ambientBRDF.setCd(cd);
		diffuseBRDF.setCd(cd);
	}

}
