package ar.edu.itba.it.cg.yart.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

public final class YartExecutorFactory {

	public static ExecutorService newCachedThreadPool() {
		return Executors.newCachedThreadPool(new YartThreadFactory(
				"JavaRayPool", "JavaRayThread"));
	}

	public static ExecutorService newFixedThreadPool(int nThreads) {
		return Executors.newFixedThreadPool(nThreads, new YartThreadFactory(
				"JavaRayPool", "JavaRayThread"));
	}

	public static class YartThreadFactory implements ThreadFactory {
		private final ThreadGroup group;
		private final String threadPrefix;
		private final AtomicLong threadIndex = new AtomicLong();

		public YartThreadFactory(String groupName, String threadPrefix) {
			this.group = new ThreadGroup(groupName);
			this.threadPrefix = threadPrefix;
		}

		@Override
		public Thread newThread(Runnable runnable) {
			return new Thread(group, runnable, group.getName() + "_"
					+ threadPrefix + "_" + threadIndex.getAndIncrement());
		}
	}
}
