package ar.edu.itba.it.cg.yart;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;

public class YartDefaults {
	
	public static final String LOG_FILE = "yart.log";
	
	public static final int DEFAULT_XRES = 800;
	public static final int DEFAULT_YRES = 600;
	public static final double GAMMA = 2.2;
	public static final double INV_GAMMA = 1 / GAMMA;
	public static final Point3d DEFAULT_EYE = new Point3d(200, 0, 0);
	public static final Point3d DEFAULT_LOOKAT = new Point3d(0, 0, 0);
	public static final Vector3d DEFAULT_UP = new Vector3d(0, 0, 1);
	public static final double DEFAULT_FOV = 90;
	public static final double DEFAULT_LENS_RADIUS = 0.00625;
	public static final double DEFAULT_FOCAL_DISTANCE = 0;
	public static final double DEFAULT_RAY_DEPTH = 1000;
	public static final int DEFAULT_MAX_HOPS = 10;
	public static final int DEFAULT_NUM_SAMPLES = 4;
	
	public static final float LIGHT_GAIN_MULTIPLIER = 1;

}
