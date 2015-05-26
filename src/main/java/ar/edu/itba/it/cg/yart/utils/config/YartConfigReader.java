package ar.edu.itba.it.cg.yart.utils.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class YartConfigReader {

	private final Properties properties;
	private static final String BASE = "src/main/resources/";
	private static final String PROPERTIES_FILE = "config.properties";

	protected YartConfigReader() {
		this.properties = new Properties();
		this.load();
	}

	private void load() {
		StringBuilder builder = new StringBuilder();
		builder.append(BASE).append(PROPERTIES_FILE);
		try {
			this.properties.load(ClassLoader
					.getSystemResourceAsStream(PROPERTIES_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected String getKey(final String key) {
		final String aKey = this.properties.getProperty(key);
		return aKey;
	}

}
