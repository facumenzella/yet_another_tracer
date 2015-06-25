package ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree;

public class KDInternalNode extends KDNodeAbstract {

	public final KDNodeAbstract left, right;
	public final SplitPoint splitPoint;
	
	public KDInternalNode(final SplitPoint splitPoint, final KDNodeAbstract left, final KDNodeAbstract right) {
		this.splitPoint = splitPoint;
		this.left = left;
		this.right = right;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}
	
}
