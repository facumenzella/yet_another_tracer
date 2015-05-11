package ar.edu.itba.it.cg.yart.utils.config;

public class YartConfigProvider {

	private static final String CORES_QTY_KEY = "cores";
	private static final String BUCKET_SIZE_KEY = "bucketSize";
	private static final String MAX_T_KEY = "maxT";
	private static final String DISTANCE_KEY = "distance";
	
	private final YartConfigReader reader;
	
	public YartConfigProvider() {
		this.reader = new YartConfigReader();
	}

	public int getCoresQty() {
		final String qty = this.reader.getKey(CORES_QTY_KEY);
		return Integer.valueOf(qty);
	}
	
	public int getBucketSize() {
		final String size = this.reader.getKey(BUCKET_SIZE_KEY);
		return Integer.valueOf(size);
	}
	
	public int getMaxT() {
		final String maxT = this.reader.getKey(MAX_T_KEY);
		return Integer.valueOf(maxT);
	}
	
	public int getDistance() {
		final String distance = this.reader.getKey(DISTANCE_KEY);
		return Integer.valueOf(distance);
	}
	
}
