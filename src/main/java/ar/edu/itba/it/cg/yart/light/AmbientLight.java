package ar.edu.itba.it.cg.yart.light;


import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class AmbientLight extends Light {
	
	private double ls;
	private Color color;
	
	public AmbientLight() {
		this.ls = ls;
		this.color = Color.whiteColor();
		
	}
	
	@Override
	public Vector3d getDirection() {
		return new Vector3d(0,0,0);
	}
	
	@Override
	public Color L(final ShadeRec sr) {
		return null; //white color for now
	}

}
