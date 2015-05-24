package ar.edu.itba.it.cg.yart.geometry.primitives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ar.edu.itba.it.cg.yart.geometry.Point3d;
import ar.edu.itba.it.cg.yart.geometry.Vector3d;
import ar.edu.itba.it.cg.yart.geometry.primitives.mesh.Mesh;
import ar.edu.itba.it.cg.yart.parser.ParserUtils;

public class MeshBox extends Mesh {
	
	private static final int[] triindices = ParserUtils.parseIntegerArray("0 1 2 0 2 3 4 5 6 4 6 7 8 9 10 8 10 11 12 13 14 12 14 15 16 17 18 16 18 19 20 21 22 20 22 23".split("\\s"));
	private static final Point3d[] p = ParserUtils.parsePointArray("-1 1 -1 -1 -1 -1 -1 -1 1 -1 1 1 -1 1 1 1 1 1 1 1 -1 -1 1 -1 1 1 1 1 -1 1 1 -1 -1 1 1 -1 -1 -1 -1 1 -1 -1 1 -1 1 -1 -1 1 -1 -1 -1 -1 1 -1 1 1 -1 1 -1 -1 1 -1 1 1 1 1 -1 1 1 -1 -1 1".split("\\s"));
	private static final Vector3d[] n = ParserUtils.parseVectorArray("-1 0 -0 -1 0 -0 -1 0 -0 -1 0 -0 0 1 -0 0 1 -0 0 1 -0 0 1 -0 1 0 -0 1 0 -0 1 0 -0 1 0 -0 0 -1 -0 0 -1 -0 0 -1 -0 0 -1 -0 -0 0 -1 -0 0 -1 -0 0 -1 -0 0 -1 -0 0 1 -0 0 1 -0 0 1 -0 0 1".split("\\s"));
	private static final double[] uv = ParserUtils.parseDoubleArray("1 1 0 1 0 0 1 0 0 0 1 0 1 1 0 1 0 0 1 0 1 1 0 1 1 1 0 1 0 0 1 0 0 0 1 0 1 1 0 1 0 0 1 0 1 1 0 1".split("\\s"));
	private static double[] uList;
	private static double[] vList;
	
	public MeshBox() {
		super(Arrays.asList(p), Arrays.asList(n), convertIndices(), getU(uv), getV(uv), false);
	}
	
	private static List<Integer> convertIndices() {
		List<Integer> indices = new ArrayList<Integer>(triindices.length);
		for (int i : triindices) {
            indices.add(i);
        }
		return indices;
	}
	
	private static double[] getU(double[] uvList) {
		if (uList == null) {
			fillUV(uvList);
		}
		return uList;
	}
	
	private static double[] getV(double[] uvList) {
		if (vList == null) {
			fillUV(uvList);
		}
		return vList;
	}
	
	private static void fillUV(double[] uvList) {
		int items = (int) Math.ceil(uvList.length / 2);
    	uList = new double[items];
    	vList = new double[items];
    	for (int i = 0; i < uList.length; i++) {
    		uList[i] = uvList[i * 2 + 1];
    		vList[i] = uvList[i * 2];
    	}
	}
}
