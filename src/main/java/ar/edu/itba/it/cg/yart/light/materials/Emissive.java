package ar.edu.itba.it.cg.yart.light.materials;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class Emissive extends MaterialAbstract {
	
	private double ls = 1;
	private Color ce = Color.WHITE;
	private Color realCe = ce;
	private final Material backfaceMaterial;
	
	public Emissive(final Material backfaceMaterial) {
		this.backfaceMaterial = backfaceMaterial;
	}
	
	public Material getBackfaceMaterial() {
		return backfaceMaterial;
	}
	
	public void setCe(final Color ce) {
		this.realCe = ce.multiply(ls);
	}
	
	public void setLs(final double ls) {
		this.ls = ls;
		setCe(ce);
	}
	
	public Color getCe() {
		return realCe;
	}
	
	public double getLs() {
		return ls;
	}

	@Override
	public Color shade(ShadeRec sr, Stack stack) {
		if (sr.normal.inverse().dot(new Vector3d(sr.ray.direction[0], sr.ray.direction[1], sr.ray.direction[2])) > 0) {
			return realCe;
		}
		
		return backfaceMaterial.shade(sr, stack);
	}

	@Override
	public Color globalShade(ShadeRec sr, Stack stack) {
		if (sr.ray.depth == 1) {
			return Color.BLACK;
		}
		Vector3d invSr = sr.normal.inverse();
		
		final double dot = invSr.x * sr.ray.direction[0] + invSr.y * sr.ray.direction[1] + invSr.z * sr.ray.direction[2];
		
		if (dot > 0.0) {
			return ce;
		}
		return Color.BLACK;
	}

}
