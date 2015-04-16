package ar.edu.itba.it.cg.yart.utils.config;

public class YartConfigProvider {

	private static final String CORES_QTY_KEY = "cores";
	
	private final YartConfigReader reader;
	
	public YartConfigProvider() {
		this.reader = new YartConfigReader();
	}

	public int getCoresQty() {
		final String qty = this.reader.getKey(CORES_QTY_KEY);
		return Integer.valueOf(qty);
	}
	
}
