package ar.edu.itba.it.cg.yart.raytracer.camera;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point2d;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class PinholeCamera extends CameraAbstract {

	private final double distance;
	private final double zoom;

	public PinholeCamera(final Tracer tracer, final Point3 eye,
			final Point3 lookat, final Vector3d up, final double distance,
			final double zoom) {
		super(tracer, eye, lookat, up);
		this.distance = distance;
		this.zoom = zoom;
	}

	@Override
	public void renderScene(final Bucket bucket, final World world,
			final ArrayIntegerMatrix result, final ViewPlane viewPlane) {
		// TODO : Its almost working, but its not finished
		Color color;
		double adjustedPixelSize = viewPlane.pixelSize / zoom;
		Point2d sp = new Point2d(0, 0);
		Point2d pp;
		Ray ray = new Ray(this.eye);

		int xStart = bucket.getX();
		int xFinish = xStart + bucket.getWidth();
		int yStart = bucket.getY();
		int yFinish = yStart + bucket.getHeight();

		for (int row = yStart; row < yFinish; row++) { // up
			for (int col = xStart; col < xFinish; col++) { // across
				final double x = adjustedPixelSize
						* (col - 0.5 * viewPlane.hRes + sp.x);
				final double y = adjustedPixelSize
						* (0.5 * viewPlane.vRes - row + sp.y);

				pp = new Point2d(x, y);
				ray.direction = this.rayDirection(pp);
				ShadeRec sr = new ShadeRec(world);
				// TODO: check for better style
				sr = new ShadeRec(tracer.traceRay(ray, world.getObjects(), sr));
				if (sr.hitObject) {
					sr.ray = ray;
					color = sr.material.shade(sr);
				} else {
					color = world.getBackgroundColor();
				}

				displayPixel(col, row, color, result);
			}
		}

	}

	private Vector3d rayDirection(final Point2d p) {
		final Vector3d dir = (u.scale(p.x)).add(v.scale(p.y)).sub(
				w.scale(distance));
		return dir.normalized;
	}

	private void displayPixel(final int col, final int row, final Color color,
			final ArrayIntegerMatrix result) {
//		if (color.r > 1.0) {
//			color.r = 1.0;
//		}
//
//		if (color.g > 1.0) {
//			color.g = 1.0;
//		}
//
//		if (color.b > 1.0) {
//			color.b = 1.0;
//		}
		final Color mappedColor = maxToOne(color);
		result.put(col, row, mappedColor.toInt());
	}
	
	private Color maxToOne(final Color c) {
		final double maxValue = Math.max(c.r, Math.max(c.g, c.b));
		if(maxValue > 1.0) {
			return c.multiply(1/maxValue);
		}
		return c;
	}

}
