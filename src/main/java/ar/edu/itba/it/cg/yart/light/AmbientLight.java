package ar.edu.itba.it.cg.yart.light;


import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;

public class AmbientLight extends LightAbstract {
	
	private double ls;
	private Color color;
	private final Color l;
	private Vector3d direction;
	
	public AmbientLight() {
		this.ls = 0.2;
		this.color = Color.whiteColor();
		this.direction = new Vector3d(0,0,0);
		this.l = color.multiply(ls); 
	}
	
	@Override
	public Vector3d getDirection(final ShadeRec sr) {
		return this.direction;
	}
	
	@Override
	public Color L(final ShadeRec sr) {
		return l;
	}

	@Override
	public boolean inShadow(Ray ray, ShadeRec sr) {
		// TODO Auto-generated method stub
		return false;
	}

}
