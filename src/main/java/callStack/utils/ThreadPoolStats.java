package callStack.utils;

public interface ThreadPoolStats {

    public int getMaximumPoolSize();

    public int getCurrentPoolSize();

    public int getActivePoolSize();
}
