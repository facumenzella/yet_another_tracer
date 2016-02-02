package ar.edu.itba.it.cg.yart.light;


import ar.edu.itba.it.cg.yart.acceleration_estructures.fkdtree.Stack;
import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.tracer.Ray;
import ar.edu.itba.it.cg.yart.tracer.ShadeRec;
import ar.edu.itba.it.cg.yart.transforms.Matrix4d;

import java.awt.image.BufferedImage;
import java.nio.Buffer;

public class AmbientLight extends AbstractLight {
	
	private double ls;
	private Color color;
	private Vector3d direction;
	
	private Color L;
	private BufferedImage environmentMap;
	
	public AmbientLight(final Color color) {
		this(1, color);
	}
	
	public AmbientLight(final double ls, final Color color) {
		this.ls = ls;
		this.color = color;
		this.direction = new Vector3d(0,0,0);
		this.L = this.mL(null);
	}

	public AmbientLight(final BufferedImage environmentMap) {
		this.ls = 1;
		this.environmentMap = environmentMap;
		this.direction = new Vector3d(0,0,0);
		this.color = new Color(0, 0, 0);
		this.L = color;
	}
	
	@Override
	public Vector3d getDirection(final ShadeRec sr) {
		return this.direction;
	}
	
	@Override
	public Color L(final ShadeRec sr) {
		if (environmentMap != null) {
			return Color.blackColor();
		}
		return this.L;
	}
	
	public Color mL(final ShadeRec sr) {
		if (environmentMap != null) {
			return Color.blackColor();
		}
		return color.multiply(ls);
	}

	@Override
	public boolean inShadow(Ray ray, ShadeRec sr, final Stack stack) {
		return false;
	}

	@Override
	public void applyTransformation(Matrix4d matrix) {
		this.direction = this.direction.transformByMatrix(matrix);
	}
	
	public Color getColor(final Vector3d direction) {
		if (environmentMap != null && direction != null) {
			return getEnvironmentColor(direction);
		}
		return getColor();
	}

	public Color getColor() {
		return L;
	}

	private Color getEnvironmentColor(final Vector3d direction) {
		final Vector3d dir = direction.normalizedVector();
		double theta = Math.acos(direction.z);
		double phi = Math.atan2(direction.y, direction.x);
		double u = (Math.PI + phi) / (2 * Math.PI);
		double v = theta / Math.PI;

		int color = environmentMap.getRGB((int) (u * environmentMap.getWidth()), (int) (v * environmentMap.getHeight()));
		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = (color) & 0xFF;
		double red = r / 255.0;
		double green = g / 255.0;
		double blue = b / 255.0;
		return new Color(red, green, blue);
	}

}
