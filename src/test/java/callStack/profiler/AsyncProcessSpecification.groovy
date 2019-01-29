package callStack.profiler

import callStack.profiler.AsyncProcess
import spock.lang.Specification

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


class AsyncProcessSpecification extends Specification{

    AsyncProcess asyncProcess
    void cleanup(){
        asyncProcess.stop()
    }

    def "Able to async process closure code"(){
        AtomicInteger count = new AtomicInteger()

        asyncProcess = new AsyncProcess()
        asyncProcess.start()
        when:
        asyncProcess.async {
            count.andIncrement
        }
        asyncProcess.async {
            count.andIncrement
        }
        asyncProcess.async {
            count.andIncrement
        }
        waitFor(count, 3)

        then:
        count.get() == 3
    }

    private void waitFor(AtomicInteger count, int numToWait) {
        int num = 0
        while (count.get() != numToWait && num < 10) {
            Thread.sleep(200)
            num++
        }
    }

    def "Async code may throw exceptions"(){
        AtomicInteger count = new AtomicInteger()

        asyncProcess = new AsyncProcess()
        asyncProcess.start()
        when:
        asyncProcess.async {
            if(true){throw new IllegalArgumentException("fail")}
            count.andIncrement
        }
        asyncProcess.async {
            count.andIncrement
        }
        asyncProcess.async {
            count.andIncrement
        }
        waitFor(count, 2)

        then:
        count.get() == 2
    }

    def "Execute real slow closure"(){
        AtomicInteger count = new AtomicInteger(0)

        asyncProcess = new AsyncProcess()
        asyncProcess.start()
        when:
        long start = System.currentTimeMillis()
        asyncProcess.async {
            Thread.sleep(5000)
            count.getAndIncrement()
        }

        long diff = System.currentTimeMillis()-start
        int num = 0
        while (count.get() != 1 && num < 10) {
            Thread.sleep(1000)
            num++
        }
        then:
        diff < 1000
        count.get() == 1
    }


    def "throw an exception if async queue is full"(){

        asyncProcess = new AsyncProcess(queueSize:2)
        asyncProcess.start()

        asyncProcess.async { Thread.sleep(50000) }
        asyncProcess.async { Thread.sleep(1) }

        Throwable t
        when:
        try {
            asyncProcess.async { Thread.sleep(50000) }
            asyncProcess.async { Thread.sleep(50000) }
        } catch (IllegalStateException e){
            e.printStackTrace()
            t = e
        }

        then:
        asyncProcess.stop()
        t.message == "Queue full"
    }


    def "support drop-if-full option"(){

        AtomicInteger count = new AtomicInteger(0)

        asyncProcess = new AsyncProcess(queueSize:1, dropIfFull:true)
        asyncProcess.start()

        asyncProcess.async {
            TimeUnit.SECONDS.sleep(6)
        }
        asyncProcess.async {
        }
        asyncProcess.async {
        }

        when:

        boolean kept = asyncProcess.async {
            count.incrementAndGet()
        }

        TimeUnit.SECONDS.sleep(10)

        then:
        !kept
        count.get().intValue() == 0
    }

}
