package ar.edu.itba.it.cg.yart.tracer;

import ar.edu.itba.it.cg.yart.color.Color;
import ar.edu.itba.it.cg.yart.matrix.ArrayColorMatrix;

public class RenderResult {

	private long renderStartTime;
	private long sceneLoadingStartTime;
	private long preprocessingStartTime;
	
	private long renderTime;
	private long sceneLoadingTime;
	private long preprocessingTime;
	private ArrayColorMatrix pixels;
	
	private int triangles;
	
	private int benchmarkRuns = 0;
	private long averageTime = 0;
	private boolean displayRenderTime = false;
	
	public void setPixels(final ArrayColorMatrix pixels) {
		this.pixels = pixels;
	}

	public ArrayColorMatrix getPixels() {
		return pixels;
	}

	public long getSceneLoadingTime() {
		return sceneLoadingTime;
	}

	public long getPreprocessingTime() {
		return preprocessingTime;
	}

	public long getRenderTime() {
		return renderTime;
	}
	
	public long getTotalTime() {
		return getSceneLoadingTime() + getPreprocessingTime() + getRenderTime();
	}
	
	public void startSceneLoading() {
		sceneLoadingStartTime = System.currentTimeMillis();
	}
	
	public void startPreprocessing() {
		preprocessingStartTime = System.currentTimeMillis();
	}
	
	public void startRender() {
		renderStartTime = System.currentTimeMillis();
	}

	public void finishSceneLoading() {
		this.sceneLoadingTime = getElapsedTime(sceneLoadingStartTime);
	}

	public void finishPreprocessing() {
		this.preprocessingTime = getElapsedTime(preprocessingStartTime);
	}

	public void finishRender() {
		this.renderTime = getElapsedTime(renderStartTime);
	}
	
	public void setBenchmarkRuns(final int benchmarkRuns) {
		this.benchmarkRuns = benchmarkRuns;
	}
	
	public int getBenchmarkRuns() {
		return benchmarkRuns;
	}
	
	public void setAverageTime(final long averageTime) {
		this.averageTime = averageTime;
	}
	
	public long getAverageTime() {
		return averageTime;
	}
	
	public void setDisplayRenderTime(final boolean display) {
		this.displayRenderTime = display;
	}
	
	public boolean isDisplayRenderTime() {
		return displayRenderTime;
	}
	
	public void setTriangles(final int triangles) {
		this.triangles = triangles;
	}
	
	public int getTriangles() {
		return triangles;
	}

	private long getElapsedTime(final long startTime) {
		return System.currentTimeMillis() - startTime;
	}
}
