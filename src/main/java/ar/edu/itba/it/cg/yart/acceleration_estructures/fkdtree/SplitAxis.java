package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

import ar.edu.itba.it.cg.yart.geometry.Vector3d;

public enum SplitAxis {
	
	X(Vector3d.X, 0),
	Y(Vector3d.Y, 1),
	Z(Vector3d.Z, 2);
	
	SplitAxis(Vector3d axis, final int value) {
		this.axis = axis;
		this.value = value;
	}
	
	public Vector3d axis;
	public int value;
	
	public boolean isParalel(final Vector3d vector) {
		return axis.equals(vector);
	}
	
	public static SplitAxis nextAxis(final SplitAxis axis) {
		SplitAxis next = X;;
		switch (axis) {
		case X:
			next = Y;
			break;
		case Y:
			next = Z;
			break;
		case Z:
			next = X;
			break;
		default:
			System.out.println("Holy shit the impossible happened");
			break;
		}
		return next;
	}
	
}
