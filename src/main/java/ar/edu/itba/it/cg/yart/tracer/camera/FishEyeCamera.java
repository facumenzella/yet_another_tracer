package ar.edu.itba.it.cg.yart.tracer.camera;

import java.util.concurrent.ThreadLocalRandom;

import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.matrix.ArrayColorMatrix;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;
import ar.edu.itba.it.cg.yart.tracer.Tracer;
import ar.edu.itba.it.cg.yart.tracer.ViewPlane;
import ar.edu.itba.it.cg.yart.tracer.buckets.Bucket;
import ar.edu.itba.it.cg.yart.tracer.strategy.TracerStrategy;
import ar.edu.itba.it.cg.yart.tracer.world.World;

public class FishEyeCamera extends CameraAbstract {

	private final static double kPIOn180 = Math.PI / 180;
	private final double psiMax;
	private double tMax;
	private final TracerStrategy strategy;

	private double distance;
	// Default value according to LuxRender specs.
	private double fov = 90;
	private double maxX = 1;
	private double minX = -1;
	private double maxY = 1;
	private double minY = -1;
	private double pixelSize;

	public FishEyeCamera(final Point3d eye, final Point3d lookat, final Vector3d up, final double distance,
			final double tMax, final double psiMax, final TracerStrategy strategy) {
		super(eye, lookat, up);
		this.distance = distance;
		this.tMax = tMax;
		this.psiMax = psiMax;
		this.strategy = strategy;
	}

	@Override
	public void renderScene(Bucket bucket, Tracer rayTracer, ArrayColorMatrix result, Stack stack) {
		Color blackColor = Color.BLACK;
		Color color = new Color(0, 0, 0, 0);

		ViewPlane viewPlane = rayTracer.getViewPlane();
		
		double adjustedPixelSize = getPixelSize(viewPlane.hRes, viewPlane.vRes);
		Ray ray = new Ray(this.eye);
		final int n = (int) Math.sqrt((double) rayTracer.getNumSamples());
		final double invNumSamples = 1 / (double) rayTracer.getNumSamples();

		int xStart = bucket.getX();
		int xFinish = xStart + bucket.getWidth();
		int yStart = bucket.getY();
		int yFinish = yStart + bucket.getHeight();

		World world = rayTracer.getWorld();
		ShadeRec sr = new ShadeRec(world);
		final double[] d = new double[3];

		int row = yStart;
		int col = xStart;
		while (row < yFinish) {
			color.r = blackColor.r;
			color.g = blackColor.g;
			color.b = blackColor.b;
			color.a = blackColor.a;
			int i = 0;
			int j = 0;
			while (i < n) {
				// This is supposed to be the sampler, we need to work on this
				final double distributionX;
				final double distributionY;
				if (n == 1) {
					distributionX = distributionY = 0;
				} else {
					final double ri = ThreadLocalRandom.current().nextDouble();
					distributionX = (j + ri) / n;
					distributionY = (i + ri) / n;
				}
				// this is different from the book
				// we do not remember why
				final double px = adjustedPixelSize
						* (col - 0.5 * viewPlane.hRes + distributionX);
				final double py = adjustedPixelSize
						* (0.5 * viewPlane.vRes - row + distributionY);

				final double rSquared = computeRayDirection(d, px, py, adjustedPixelSize);
				if (rSquared <= 1.0) {
					ray.direction = d;
					ray.origin.set(this.eye);
					sr.hitObject = false;
					Color c = world.getTree().traceRay(ray, sr, tMax, stack, strategy);
					// System.out.println(c);
					color.r += c.r;
					color.g += c.g;
					color.b += c.b;
				}
				j++;
				if (j == n) {
					j = 0;
					i++;
				}
			}
			color.r *= invNumSamples;
			color.g *= invNumSamples;
			color.b *= invNumSamples;

			result.put((row * result.cols) + col, color);
			col++;
			if (col == xFinish) {
				col = xStart;
				row++;
			}
		}
	}

	private double computeRayDirection(final double[] direction, final double x, final double y, 
			final double adjustedPixelSize) {
		final double rx = (2.0 * x) / (adjustedPixelSize * viewPlane.hRes);
		final double ry = (2.0 * y) / (adjustedPixelSize * viewPlane.vRes);
		final double rSquared =  rx * rx + ry * ry;
		
		if (rSquared > 1) {
			// just to return something greater than 1
			return 2;
		}
		
		final double r = Math.sqrt(rSquared);
		final double psi = r * this.psiMax * kPIOn180;

		final double sinPsi = Math.sin(psi);
		final double cosPsi = Math.cos(psi);
		final double sinAlpha = ry / r;
		final double cosAlpha = rx / r;

		direction[0] = (u.x * sinPsi * cosAlpha) + (v.x * sinPsi * sinAlpha) - (w.x * cosPsi);
		direction[1] = (u.y * sinPsi * cosAlpha) + (v.y * sinPsi * sinAlpha) - (w.y * cosPsi);
		direction[2] = (u.z * sinPsi * cosAlpha) + (v.z * sinPsi * sinAlpha) - (w.z * cosPsi);
		
		return 0;
	}

	@Override
	public ViewPlane calculateViewPlane(int hRes, int vRes) {
		if (isViewPlaneInvalid()) {
			viewPlane = new ViewPlane(hRes, vRes, this.pixelSize);
			viewPlaneInvalidated = false;
		}
		return viewPlane;
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
	
	public void setScreenWindow(final double minX, final double maxX, final double minY, final double maxY) {
		this.maxX = maxX;
		this.minX = minX;
		this.maxY = maxY;
		this.minY = minY;
		invalidateViewPlane();
	}

	private double getPixelSize(final int hRes, final int vRes) {
		double pixelSize = 1;

		final double min = Math.min(hRes, vRes);
		// We need radians, not degrees!!!
		final double radians = Math.toRadians(fov / 2);
		double length = 2 * distance * Math.tan(radians);
		double mult = Math.min(Math.abs(maxX - minX) / 2, Math.abs(maxY - minY) / 2);

		length *= mult;

		if (min == hRes) {
			pixelSize = length / hRes;
		} else {
			pixelSize = length / vRes;
		}

		return Math.abs(pixelSize);
	}

	public void setTMax(final double tMax) {
		this.tMax = tMax;
	}

}
