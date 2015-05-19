package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

public class KDInternalNode extends KDNode {

	public final KDNode left, right;
	public final SplitPoint splitPoint;
	
	public KDInternalNode(final SplitPoint splitPoint, final KDNode left, final KDNode right) {
		this.splitPoint = splitPoint;
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}
	
}
