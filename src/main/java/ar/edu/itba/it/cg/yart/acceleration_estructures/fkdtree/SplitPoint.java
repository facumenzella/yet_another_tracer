package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

import java.util.Objects;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.nlog2n.YAFKDTree;

public class SplitPoint {
	
	public int axis; // x = 0, y = 1, z = 2
	public double point;
	
	public SplitPoint(){};
	
	public SplitPoint(final int axis, final double point) {
		this.axis = axis;
		this.point = point;
	}
	
	@Override
	public String toString() {
		return "Axis " + axis + ": "+ point;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SplitPoint)) {
			return false;
		}
		final SplitPoint o = (SplitPoint) obj;

		return axis == o.axis && point > o.point - YAFKDTree.kEPSILON
				&& point < o.point + YAFKDTree.kEPSILON;
	}

	@Override
	public int hashCode() {
		return Objects.hash(axis, point);
	}
}
