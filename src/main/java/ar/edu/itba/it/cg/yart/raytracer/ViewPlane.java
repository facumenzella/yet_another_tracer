package ar.edu.itba.it.cg.yart.raytracer;

public class ViewPlane {
	
	public int hRes;
	public int vRes;
	final public float pixelSize = 1;
	
	public ViewPlane(final int hRes, final int vRes) {
		this.hRes = hRes;
		this.vRes = vRes;
	}

}
