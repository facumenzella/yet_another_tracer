package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.primitives.GeometricObject;

public class KDLeafNode extends KDNodeAbstract {

	public KDLeafNode(final List<GeometricObject> gObjects) {
		this.gObjects = gObjects;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}
	
}
