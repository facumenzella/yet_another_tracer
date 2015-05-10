package ar.edu.itba.it.cg.yart.light.brdf;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.textures.Texture;

public class SV_Lambertian extends BRDF{
	
	private double kd;
	private Texture cd;
	private final double invPi = 1/(Math.PI);

	@Override
	public Color f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		return cd.getColor(sr).multiply(kd);
	}

	@Override
	public Color rho(ShadeRec sr, Vector3d wo) {
		return cd.getColor(sr).multiply(kd*invPi);
	}

	@Override
	public Color sample_f(ShadeRec sr, Vector3d wo, Vector3d wi) {
		// TODO Auto-generated method stub
		return null;
	}

}
