package callStack.profiler

import groovy.transform.CompileStatic

import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@CompileStatic
class ProfFuture<T> implements Future<T> {

    Future<ProfAsyncResult<T>> underlyingFuture

    @Override
    boolean cancel(boolean mayInterruptIfRunning) {
        return underlyingFuture.cancel(mayInterruptIfRunning)
    }

    @Override
    boolean isCancelled() {
        return underlyingFuture.cancelled
    }

    @Override
    boolean isDone() {
        return underlyingFuture.done
    }

    @Override
    T get() throws InterruptedException, ExecutionException {
        ProfAsyncResult<T> profAsyncResult = underlyingFuture.get()
        documentProfiling(profAsyncResult)
        return profAsyncResult.res
    }

    @Override
    T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        ProfAsyncResult<T> profAsyncResult = underlyingFuture.get(timeout,unit)
        documentProfiling(profAsyncResult)
        return profAsyncResult.res
    }

    private void documentProfiling(ProfAsyncResult<T> profAsyncResult) {
        profAsyncResult.profileEvent.concurrent = true
        if (CProf?.parent) {
            CProf?.parent.addChild(profAsyncResult.profileEvent)
        }
    }
}
