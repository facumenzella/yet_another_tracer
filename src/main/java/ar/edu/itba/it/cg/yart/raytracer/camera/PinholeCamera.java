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
	
	// Default value according to LuxRender specs.
	private double fov = 90;

	public PinholeCamera(final Point3d eye, final Point3d lookat, final Vector3d up, final double distance,
			final double zoom) {
		super(eye, lookat, up);
		this.distance = distance;
		this.zoom = zoom;
	}

	@Override
	public void renderScene(final Bucket bucket, RayTracer rayTracer, final ArrayIntegerMatrix result) {

		// TODO : Its almost working, but its not finished
		Color blackColor = Color.blackColor();
		Color color = new Color(0,0,0,0);
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
		ColorTracer tracer = rayTracer.getTracer();

		for (int row = yStart; row < yFinish; row++) { // up
			for (int col = xStart; col < xFinish; col++) { // across
				color.copy(blackColor);
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
		final MutableVector3d mu = new MutableVector3d(this.u);
		final MutableVector3d mv = new MutableVector3d(this.v);
		final MutableVector3d mw = new MutableVector3d(this.w);
		mu.scale(p.x);
		mv.scale(p.y);
		mw.scale(distance);
		mu.add(mv);
		mu.sub(mw);
		mu.normalize();
		return mu.inmutableCopy();
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
		final Color mappedColor = maxToOne(color);
		result.put(col, row, mappedColor.toInt());
	}

	private Color maxToOne(final Color c) {
		final double maxValue = Math.max(c.r, Math.max(c.g, c.b));
		if (maxValue > 1.0) {
			return c.multiplyEquals(1 / maxValue);
		}
		return c;
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
