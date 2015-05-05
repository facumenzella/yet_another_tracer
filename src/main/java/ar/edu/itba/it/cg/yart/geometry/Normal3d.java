package ar.edu.itba.it.cg.yart.geometry;

public class Normal3d extends Vector3d{

	public Normal3d() {
		super(0,0,0);
		this.length = 1;
	}
	
	public Normal3d(final double x, final double y, final double z) {
		super(x,y,z);
		this.x = this.x / this.length;
		this.y = this.y / this.length;
		this.z = this.z / this.length;
		this.length = 1;
	}
	
}
