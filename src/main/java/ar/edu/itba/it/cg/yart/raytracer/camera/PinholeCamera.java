package ar.edu.itba.it.cg.yart.raytracer.camera;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point2d;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class PinholeCamera extends CameraAbstract {

	private final double distance;
	private final double zoom;
	
	// Default value according to LuxRender specs.
	private double fov = 90;

	public PinholeCamera(final Point3 eye, final Point3 lookat, final Vector3d up, final double distance,
			final double zoom) {
		super(eye, lookat, up);
		this.distance = distance;
		this.zoom = zoom;
	}

	@Override
	public void renderScene(final Bucket bucket, RayTracer rayTracer, final ArrayIntegerMatrix result) {
		// TODO : Its almost working, but its not finished
		Color color;
		ViewPlane viewPlane = rayTracer.getViewPlane();
		double adjustedPixelSize = viewPlane.pixelSize / zoom;
		Point2d sp = new Point2d(0, 0);
		Point2d pp;
		Ray ray = new Ray(this.eye);
		final int n = (int) Math.sqrt((double) rayTracer.getNumSamples());
		final double invNumSamples = 1 / (double) rayTracer.getNumSamples();

		int xStart = bucket.getX();
		int xFinish = xStart + bucket.getWidth();
		int yStart = bucket.getY();
		int yFinish = yStart + bucket.getHeight();
		
		World world = rayTracer.getWorld();
		Tracer tracer = rayTracer.getTracer();

		for (int row = yStart; row < yFinish; row++) { // up
			for (int col = xStart; col < xFinish; col++) { // across
				color = Color.blackColor();
				for (int i = 0; i < n; i++) {
					for (int j = 0; j < n; j++) {
						final double distributionX;
						final double distributionY;
						if (n == 1) {
							distributionX = distributionY = 0;
						} else {
							distributionX = (j + Math.random())/n;
							distributionY = (i + Math.random())/n;
						}
						final double x = adjustedPixelSize
								* (col - 0.5 * viewPlane.hRes + sp.x + distributionX);
						final double y = adjustedPixelSize
								* (0.5 * viewPlane.vRes - row + sp.y + distributionY);

						pp = new Point2d(x, y);
						ray.direction = this.rayDirection(pp);
						Color c = world.getTree().traceRay(ray, tracer, new ShadeRec(world));
						color.addEquals(c);
//						color.addEquals(tracer.traceRay(ray,
//								world.getObjects(), new ShadeRec(world), Double.POSITIVE_INFINITY));

					}
				}
				color.multiplyEquals(invNumSamples);
				displayPixel(col, row, color, result);
			}
		}

	}
	
	public double getFov() {
		return fov;
	}
	
	public void setFov(double fov) {
		if (fov < 0) {
			fov = 0;
		}
		else if (fov >= 180) {
			fov = 179;
		}
		
		this.fov = fov;
		
		invalidateViewPlane();
	}

	private Vector3d rayDirection(final Point2d p) {
		final Vector3d dir = (u.scale(p.x)).add(v.scale(p.y)).sub(
				w.scale(distance));
		return dir.normalized;
	}
	
	private double getPixelSize(final int hRes, final int vRes) {
		double pixelSize = 1;
		
		final double min = Math.min(hRes, vRes);
		// We need radians, not degrees!!!
		final double radians = Math.PI * fov / 180;
		final double length = 2 * distance * Math.tan(radians/2);

		if (min == hRes) {
			pixelSize = length / hRes;
		} else {
			pixelSize = length / vRes;
		}
		
		return Math.abs(pixelSize);
	}

	private void displayPixel(final int col, final int row, final Color color,
			final ArrayIntegerMatrix result) {
		// if (color.r > 1.0) {
		// color.r = 1.0;
		// }
		//
		// if (color.g > 1.0) {
		// color.g = 1.0;
		// }
		//
		// if (color.b > 1.0) {
		// color.b = 1.0;
		// }
		final Color mappedColor = maxToOne(color);
		result.put(col, row, mappedColor.toInt());
	}

	private Color maxToOne(final Color c) {
		final double maxValue = Math.max(c.r, Math.max(c.g, c.b));
		if (maxValue > 1.0) {
			return c.multiply(1 / maxValue);
		}
		return c;
	}

	@Override
	public ViewPlane calculateViewPlane(int hRes, int vRes) {
		if (isViewPlaneInvalid()) {
			viewPlane = new ViewPlane(hRes, hRes, getPixelSize(hRes, vRes));
			viewPlaneInvalidated = false;
		}
		return viewPlane;
	}

}
