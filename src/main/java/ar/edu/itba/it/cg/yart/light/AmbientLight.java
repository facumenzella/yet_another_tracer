package ar.edu.itba.it.cg.yart.light;


import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class AmbientLight extends LightAbstract {
	
	private double ls;
	private Color color;
	
	public AmbientLight(final Color color) {
		this.ls = 1;
		this.color = color;
		
	}
	
	@Override
	public Vector3d getDirection(final ShadeRec sr) {
		return new Vector3d(0,0,0);
	}
	
	@Override
	public Color L(final ShadeRec sr) {
		return color.multiply(ls); 
	}

	@Override
	public boolean inShadow(Ray ray, ShadeRec sr) {
		// TODO Auto-generated method stub
		return false;
	}

}
