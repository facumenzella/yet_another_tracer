package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;


public class PlaneCandidate {
	public AABB[] boxes;
	public SplitPoint splitPoint;

	public PlaneCandidate(final AABB[] boxes, final SplitPoint splitPoint) {
		this.boxes = boxes;
		this.splitPoint = splitPoint;
	}
	
	@Override
	public String toString() {
		return "Axis: " + splitPoint.axis +" "+ splitPoint.point; 
	}
}