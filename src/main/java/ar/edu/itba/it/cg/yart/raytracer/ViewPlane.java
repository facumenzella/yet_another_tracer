package ar.edu.itba.it.cg.yart.raytracer;

public class ViewPlane {
	
	public int hRes;
	public int vRes;
	public float pixelSize;
	
	public ViewPlane(final int hRes, final int vRes) {
		this.hRes = hRes;
		this.vRes = vRes;
		pixelSize = 1;
	}

}
