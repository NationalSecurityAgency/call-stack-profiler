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

## Features

### Custom Profile Name

When using ``@Profile`` annotation, by default profile names are derived from the method name and its parameters. 
You can supply a custom name by setting ``name`` attribute on the ``@Profile`` annotation:

```groovy
class Example {
    @Profile(name = 'veryCustomName')
    void m1() {
        m2()
    }

    @Profile
    void m2() {
        Thread.sleep(20)
    }
}
```

Then the output is:

```
|-> veryCustomName (1) : 020ms [000ms]
|     |-> Example.m2 (1) : 020ms
```

### Closure based Profiling

You can easily profile (and name) any bit of code by wrapping it in a closure:

```groovy
class Example {
    @Profile
    void m1() {
        m2()
        CProf.prof("Another Long Action") {
            // great logic
            Thread.sleep(1000)
        }
    }

    @Profile
    void m2() {
        Thread.sleep(20)
    }
}
```

Then the output is:
```
|-> Example.m1 (1) : 1s 020ms [000ms]
|     |-> Example.m2 (1) : 020ms
|     |-> Another Long Action (1) : 1s 
```

### Manually start/stop events

Start and stop profiling events can be managed manually:

```groovy
class Example {
    @Profile
    void m1() {
        m2()
        String name = "Another Long Action"
        CProf.start(name)
        try {
            // great logic
            Thread.sleep(1000)
        } finally {
            CProf.stop(name)
        }
    }

    @Profile
    void m2() {
        Thread.sleep(20)
    }
}
```

Then the output is:
```
|-> Example.m1 (1) : 1s 020ms [000ms]
|     |-> Example.m2 (1) : 020ms
|     |-> Another Long Action (1) : 1s 
```

If you select to manually manage start/stop events then please:
- always wrap logic in ``try/catch`` block to ensure the event is closed
- verify that the same name is used to start and end the event 

### Delegate concurrent tasks to a Thread Pool

### Each Call as its own event

If you are calling a method within a loop AND the loop has a reasonable (for display purposes) number of elements, 
then you may want to opt in displaying each method call as its own profiling events. 

Set attribute ``aggregateIntoSingleEvent = false`` for the ``@Profile`` annotation, for example:
 
```groovy
class Example {
    @Profile
    void m1() {
        5.times {
            m2()
        }
    }

    @Profile(aggregateIntoSingleEvent = false)
    void m2() {
        Thread.sleep(20)
    }
}
```  

Then the output is:
```
|-> Example.m1 (1) : 102ms [000ms]
|     |-> Example.m20_24 (1) : 021ms
|     |-> Example.m20_23 (1) : 020ms
|     |-> Example.m20_22 (1) : 020ms
|     |-> Example.m20_21 (1) : 021ms
|     |-> Example.m20_20 (1) : 020ms
```

### Exceptions

## How does it work?

### Entry method

### Compile Time


  
 
 

