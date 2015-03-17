package ar.edu.itba.it.cg.yart.raytracer.camera;

import java.util.List;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point2d;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.raytracer.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class PinholeCamera extends CameraAbstract {

	private final double distance;
	private final double zoom;
	
	public PinholeCamera(final Tracer tracer, final Point3 eye, 
			final Point3 lookat, final Vector3d up, final double distance, final double zoom) {
		super(tracer, eye, lookat, up);
		this.distance = distance;
		this.zoom = zoom;
	}

	@Override
	public void renderScene(World world) {
		// TODO : Its almost working, but its not finished
		Color color;
		ViewPlane vp = world.vp;
		vp.pixelSize /= zoom;
		Point2d sp = new Point2d(0, 0);
		Point2d pp;
		Ray ray = new Ray(this.eye);
		
		int bucketSize = 32;
		
		List<Bucket> buckets = vp.dividePlane(bucketSize);
		
		while (!buckets.isEmpty()) {
			Bucket bucket = buckets.get(0);
			buckets.remove(0);
			
			int xStart = bucket.getX() * bucketSize;
			int xFinish = bucket.getX() * bucketSize + bucket.getWidth();
			int yStart = bucket.getY() * bucketSize;
			int yFinish = bucket.getY() * bucketSize + bucket.getHeight();
			
			
			for (int row = yStart; row < yFinish; row++) { // up
				for (int col = xStart; col < xFinish; col++) { // across
					final double x = vp.pixelSize * (col - 0.5 * vp.hRes + sp.x);
					final double y = vp.pixelSize * (0.5 * vp.vRes - row + sp.y);
					pp = new Point2d(x,y);
					ray.direction = this.rayDirection(pp);

					color = tracer.traceRay(ray, world.objects);
					if (color == null) {
						color = world.backgroundColor;
					}

					world.ret.put(col, row, color.toInt());
				}
			}
			
			if (callbacks != null) {
				callbacks.onBucketFinished(world.ret, bucket);
			}
		}
	
		if (callbacks != null) {
			callbacks.onRenderFinished(world.ret);
		}
		
	}
	
	private Vector3d rayDirection(final Point2d p) {
		final Vector3d dir = (u.scale(p.x)).add(v.scale(p.y)).sub(w.scale(distance));
		return Vector3d.normalize(dir);
	}

}
