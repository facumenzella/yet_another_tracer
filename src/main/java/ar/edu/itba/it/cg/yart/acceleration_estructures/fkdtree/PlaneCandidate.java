package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n.YAFKDTree;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(cost);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((int) splitPoint.point);
		return result;
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
					&& this.splitPoint.point - other.splitPoint.point < YAFKDTree.kEPSILON;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Axis: " + splitPoint.axis + " " + splitPoint.point + " cost: "
				+ cost;
	}
}