package ar.edu.itba.it.cg.yart.raytracer;


public class ViewPlane {

	public int hRes;
	public int vRes;
	public double pixelSize;

	public ViewPlane(final int hRes, final int vRes, final double pixelSize) {
		this.hRes = hRes;
		this.vRes = vRes;
		this.pixelSize = pixelSize;
	}

}
