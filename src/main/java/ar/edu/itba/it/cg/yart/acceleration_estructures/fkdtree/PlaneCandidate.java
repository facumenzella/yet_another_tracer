package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n.YAFKDTree2;
import ar.edu.itba.it.cg.yart.geometry.primitives.AABB;

public class PlaneCandidate {
	public AABB[] boxes;
	public SplitPoint splitPoint;
	public double cost;
	public boolean left; // regarding np
	
	public PlaneCandidate() {
	};

	public PlaneCandidate(final AABB[] boxes, final SplitPoint splitPoint,
			final double cost, final boolean left) {
		this.boxes = boxes;
		this.splitPoint = splitPoint;
		this.cost = cost;
		this.left = left;
	}
	
	

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof PlaneCandidate) {
			PlaneCandidate other = (PlaneCandidate) obj;
			return other.splitPoint.axis == this.splitPoint.axis
					&&  this.splitPoint.point - other.splitPoint.point < YAFKDTree2.kEPSILON;
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash = (int) (hash * 3 + splitPoint.point);
		hash = (int) (hash * 5 + cost);
		return hash;
	}

	@Override
	public String toString() {
		return "Axis: " + splitPoint.axis + " " + splitPoint.point + " cost: " + cost;
	}
}