# Call Stack Profiler for Groovy 

> Profile your code with negligible performance and memory overhead.

This profiler keeps track of the method calls and outputs method call hierarchy allowing developers 
to quickly comprehend execution time breakdown. 
- The profiler is **fast** and is appropriate to have track and output in a production system
- Use ``@Profile`` to easily annotate Groovy methods OR wrap logic in a closure OR manually start/stop events
- Naturally fits into the service based architecture
- Provides support for delegating concurrent tasks to a Thread Pool 

Consider the following class where methods are annotated with the ``@Profile`` annotation:
```groovy
class Example {
    @Profile
    void m1() {
        m2()
    }

    @Profile
    void m2() {
        5.times {
            m3()
        }
    }

    @Profile
    void m3() {
        Thread.sleep(100)
    }
}
```

and then

```groovy
static void main(String[] args) {
   new Example().m1()
   println CProf.prettyPrint()
}
```

then the output is: 
```
|-> Example.m1 (1) : 501ms [000ms]
|     |-> Example.m2 (1) : 501ms [000ms]
|     |     |-> Example.m3 (5) : 501ms
```

The output provides method call hierarchy as well as the following information:
- Total method execution time: number in ms, seconds and/or minutes 
- ``(N)``: number of times method was called, m2() was called once and m3() called 5 times
- ``[N ms]``: execution time which was not accounted by child methods/logic; this happens when either not all of the child methods/logic is profiled OR there is a GC or JVM overhead


  
 
 

