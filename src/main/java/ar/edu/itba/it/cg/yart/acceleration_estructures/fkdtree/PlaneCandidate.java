package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;


public class PlaneCandidate {
	public AABB[] boxes;
	public SplitPoint splitPoint;
	public double cost;
	public boolean left; // regarding np

	public PlaneCandidate() {};
	
	public PlaneCandidate(final AABB[] boxes, final SplitPoint splitPoint, final double cost, final boolean left) {
		this.boxes = boxes;
		this.splitPoint = splitPoint;
		this.cost = cost;
		this.left = left;
	}
	
	@Override
	public String toString() {
		return "Axis: " + splitPoint.axis +" "+ splitPoint.point; 
	}
}