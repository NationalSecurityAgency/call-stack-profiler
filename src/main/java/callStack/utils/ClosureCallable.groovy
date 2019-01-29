package callStack.utils

import java.util.concurrent.Callable

class ClosureCallable<T> implements Callable<T>{
    Closure<T> closure
    @Override
    T call() throws Exception {
        return closure.call()
    }
}
