package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

public class SplitPoint {
	
	public int axis; // x = 0, y = 1, z = 2
	public double point;
	public double cost;
	
	public SplitPoint(){};
	
	public SplitPoint(final int axis, final double point) {
		this.axis = axis;
		this.point = point;
	}
	
	@Override
	public String toString() {
		return "Axis " + axis + ": "+ point;
	}
}
