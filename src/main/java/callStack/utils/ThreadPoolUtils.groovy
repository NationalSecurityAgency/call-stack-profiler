package callStack.utils

import java.util.concurrent.Callable

class ThreadPoolUtils {

    static Callable callable(Closure closure) {
        return new ClosureCallable(closure: closure)
    }
}
