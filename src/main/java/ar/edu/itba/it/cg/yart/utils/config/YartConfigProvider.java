package ar.edu.itba.it.cg.yart.utils.config;

public final class YartConfigProvider {

	private static final String CORES_QTY_KEY = "cores";
	private static final String BUCKET_SIZE_KEY = "bucketSize";
	private static final String DISTANCE_KEY = "distance";

	private final YartConfigReader reader;

	private static YartConfigProvider instance = null;

	protected YartConfigProvider() {
		this.reader = new YartConfigReader();
	};
	
	public static YartConfigProvider getInstance() {
	      if(instance == null) {
	         instance = new YartConfigProvider();
	      }
	      return instance;
	}

	public int getCoresQty() {
		final String qty = this.reader.getKey(CORES_QTY_KEY);
		return Integer.valueOf(qty);
	}

	public int getBucketSize() {
		final String size = this.reader.getKey(BUCKET_SIZE_KEY);
		return Integer.valueOf(size);
	}

	public int getDistance() {
		final String distance = this.reader.getKey(DISTANCE_KEY);
		return Integer.valueOf(distance);
	}

}
