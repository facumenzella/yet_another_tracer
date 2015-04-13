package ar.edu.itba.it.cg.yart.raytracer.camera;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point2d;
import ar.edu.itba.it.cg.yart.geometry.Point3;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.tracer.Tracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class PinholeCamera extends CameraAbstract {

	private final double distance;
	private final double zoom;
	public final ViewPlane vp;

	public PinholeCamera(final Point3 eye, final Point3 lookat, final Vector3d up, final double distance,
			final double zoom, final int viewPlaneHRes, final int viewPlaneVRes, final double fov, 
			final int numSamples) {
		super(eye, lookat, up);
		this.distance = distance;
		this.zoom = zoom;
		final double pixelSize = this.getPixeSize(viewPlaneHRes, viewPlaneVRes, fov, distance);
		this.vp = new ViewPlane(viewPlaneHRes, viewPlaneVRes, pixelSize, numSamples);
	}

	@Override
	public void renderScene(final Bucket bucket, final World world,
			final ArrayIntegerMatrix result, final ViewPlane viewPlane, final Tracer tracer) {
		// TODO : Its almost working, but its not finished
		Color color;
		double adjustedPixelSize = viewPlane.pixelSize / zoom;
		Point2d sp = new Point2d(0, 0);
		Point2d pp;
		Ray ray = new Ray(this.eye);
		final int n = (int) Math.sqrt((double) viewPlane.numSamples);
		final double invNumSamples = 1 / (double) viewPlane.numSamples;

		int xStart = bucket.getX();
		int xFinish = xStart + bucket.getWidth();
		int yStart = bucket.getY();
		int yFinish = yStart + bucket.getHeight();

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
						color.addEquals(tracer.traceRay(ray,
								world.getObjects(), new ShadeRec(world), Double.POSITIVE_INFINITY));

					}
				}
				color.multiplyEquals(invNumSamples);
				displayPixel(col, row, color, result);
			}
		}

	}

	private Vector3d rayDirection(final Point2d p) {
		final Vector3d dir = (u.scale(p.x)).add(v.scale(p.y)).sub(
				w.scale(distance));
		return dir.normalized;
	}
	
	private double getPixeSize(final int hRes, final int vRes, final double fov, final double distance) {
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
	public ViewPlane getViewPlane() {
		return this.vp;
	}

}
