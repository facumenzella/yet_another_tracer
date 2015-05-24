package ar.edu.itba.it.cg.yart.raytracer;

import ar.edu.itba.it.cg.yart.matrix.ArrayIntegerMatrix;

public class RenderResult {

	private long renderStartTime;
	private long sceneLoadingStartTime;
	private long preprocessingStartTime;
	
	private long renderTime;
	private long sceneLoadingTime;
	private long preprocessingTime;
	private ArrayIntegerMatrix pixels;
	
	private boolean displayRenderTime = false;
	
	public void setPixels(final ArrayIntegerMatrix pixels) {
		this.pixels = pixels;
	}

	public ArrayIntegerMatrix getPixels() {
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
	
	public void setDisplayRenderTime(final boolean display) {
		this.displayRenderTime = display;
	}
	
	public boolean isDisplayRenderTime() {
		return displayRenderTime;
	}

	private long getElapsedTime(final long startTime) {
		return System.currentTimeMillis() - startTime;
	}
}
