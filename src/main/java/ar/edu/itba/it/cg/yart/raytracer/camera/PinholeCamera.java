package ar.edu.itba.it.cg.yart.raytracer.camera;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.MutableVector3d;
import ar.edu.itba.it.cg.yart.geometry.Point2d;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;
import ar.edu.itba.it.cg.yart.raytracer.Ray;
import ar.edu.itba.it.cg.yart.raytracer.ShadeRec;
import ar.edu.itba.it.cg.yart.raytracer.ViewPlane;
import ar.edu.itba.it.cg.yart.raytracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.raytracer.interfaces.RayTracer;
import ar.edu.itba.it.cg.yart.raytracer.tracer.ColorTracer;
import ar.edu.itba.it.cg.yart.raytracer.world.World;

public class PinholeCamera extends CameraAbstract {

	private final double distance;
	private final double zoom;
	private final Point2d sp = new Point2d(0, 0);

	// Default value according to LuxRender specs.
	private double fov = 90;

	public PinholeCamera(final Point3d eye, final Point3d lookat,
			final Vector3d up, final double distance, final double zoom) {
		super(eye, lookat, up);
		this.distance = distance;
		this.zoom = zoom;
	}

	@Override
	public void renderScene(final Bucket bucket, RayTracer rayTracer,
			final ArrayIntegerMatrix result) {

		// TODO : Its almost working, but its not finished
		Color blackColor = Color.blackColor();
		Color color = new Color(0, 0, 0, 0);
		ViewPlane viewPlane = rayTracer.getViewPlane();
		double adjustedPixelSize = viewPlane.pixelSize / zoom;
		Point2d pp;
		Ray ray = new Ray(this.eye);
		final int n = (int) Math.sqrt((double) rayTracer.getNumSamples());
		final double invNumSamples = 1 / (double) rayTracer.getNumSamples();

		int xStart = bucket.getX();
		int xFinish = xStart + bucket.getWidth();
		int yStart = bucket.getY();
		int yFinish = yStart + bucket.getHeight();

		World world = rayTracer.getWorld();
		ColorTracer tracer = rayTracer.getTracer();
		int row = yStart;
		int col = xStart;
		while (row < yFinish) {
			color.copy(blackColor);
			int i = 0;
			int j = 0;
			while (i < n) {
				final double distributionX;
				final double distributionY;
				if (n == 1) {
					distributionX = distributionY = 0;
				} else {
					distributionX = (j + Math.random()) / n;
					distributionY = (i + Math.random()) / n;
				}
				final double x = adjustedPixelSize
						* (col - 0.5 * viewPlane.hRes + sp.x + distributionX);
				final double y = adjustedPixelSize
						* (0.5 * viewPlane.vRes - row + sp.y + distributionY);

				Vector3d d = (u.scale(x)).add(v.scale(y)).sub(w.scale(distance)).normalizedVector();
				// ray direction
//				MutableVector3d mu = new MutableVector3d(u);
//				mu.scale(x);
//				mu.add(v.scale(y));
//				mu.sub(w.scale(distance));
//				ray.direction = mu.normalize();
				ray.direction = d;
				Color c = world.getTree().traceRay(ray, tracer,
						new ShadeRec(world));
				color.addEquals(c);
				j++;
				if (j == n) {
					j = 0;
					i++;
				}
			}
			color.multiplyEquals(invNumSamples);

			// mapping color
			Color mappedColor = color;
			final double maxValue = Math.max(color.r, Math.max(color.g, color.b));
			if (maxValue > 1.0) {
				mappedColor = color.multiplyEquals(1 / maxValue);
			}
			// now we display the pixel
			result.put(col, row, mappedColor.toInt());
			col++;
			if (col == xFinish) {
				col = xStart;
				row++;
			}
		}
	}

	public double getFov() {
		return fov;
	}

	public void setFov(double fov) {
		if (fov < 0) {
			fov = 0;
		} else if (fov >= 180) {
			fov = 179;
		}

		this.fov = fov;

		invalidateViewPlane();
	}
	
	private double getPixelSize(final int hRes, final int vRes) {
		double pixelSize = 1;

		final double min = Math.min(hRes, vRes);
		// We need radians, not degrees!!!
		final double radians = Math.toRadians(fov / 2);
		final double length = 2 * distance * Math.tan(radians);

		if (min == hRes) {
			pixelSize = length / hRes;
		} else {
			pixelSize = length / vRes;
		}

		return Math.abs(pixelSize);
	}

	@Override
	public ViewPlane calculateViewPlane(int hRes, int vRes) {
		if (isViewPlaneInvalid()) {
			viewPlane = new ViewPlane(hRes, vRes, getPixelSize(hRes, vRes));
			viewPlaneInvalidated = false;
		}
		return viewPlane;
	}

}
