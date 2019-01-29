package callStack.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CachedThreadPool extends AbstractThreadPool {

	public CachedThreadPool(String name) {
		m_pool = Executors.newCachedThreadPool(new NamedThreadFactory(name));
	}

	public CachedThreadPool(final String name, int minNumOfThreads, int maxNumOfThreads) {
		m_pool = new ThreadPoolExecutor(minNumOfThreads, maxNumOfThreads,
				60L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(),
				new NamedThreadFactory(name));
	}
}
