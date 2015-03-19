package ar.edu.itba.it.cg.yart.raytracer;

import java.util.LinkedList;
import java.util.List;

public class ViewPlane {

	public int hRes;
	public int vRes;
	public float pixelSize = 1;

	public ViewPlane(final int hRes, final int vRes) {
		this.hRes = hRes;
		this.vRes = vRes;
	}

	public List<Bucket> dividePlane(final int bucketSize) {
		List<Bucket> buckets = new LinkedList<Bucket>();
		int xBuckets = (int) Math.ceil(hRes / ((float) bucketSize));
		int yBuckets = (int) Math.ceil(vRes / ((float) bucketSize));

		for (int i = 0; i < yBuckets; i++) {
			for (int j = 0; j < xBuckets; j++) {
				int width = bucketSize;
				int height = bucketSize;

				if (j == xBuckets)
					width = hRes - j * bucketSize;

				if (i == yBuckets)
					height = vRes - i * bucketSize;

				buckets.add(new Bucket(j, i, width, height));
			}
		}

		return buckets;
	}

}
