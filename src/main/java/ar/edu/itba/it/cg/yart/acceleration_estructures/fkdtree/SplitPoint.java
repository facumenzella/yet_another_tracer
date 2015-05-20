package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

public class SplitPoint {
	
	public SplitAxis axis;
	public double point;
	public double cost;
	
	public SplitPoint(){};
	
	public SplitPoint(final SplitAxis axis, final double point) {
		this.axis = axis;
		this.point = point;
	}
	
	@Override
	public String toString() {
		return "Axis " + axis + ": "+ point;
	}
}
