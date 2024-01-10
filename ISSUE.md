[![Stackoverflow](https://img.shields.io/badge/stackoverflow-77764338-orange)](https://stackoverflow.com/questions/77764338/micronaut-injected-beancontext-cannot-retrieve-beans)
[![Github Issue](https://img.shields.io/badge/github-not_yet_submitted-purple)](https://github.com/micronaut-projects/micronaut-guides/issues/new)


In my application that is not a webservice application, so doesn't provide a rest endpoint, I'm using an injected `BeanContext` to create instances of other beans whenever I need them. Those beans are `@Prototype` beans, not `@Singleton` as they should only exists when needed and discarded after use.

The code worked fine with micronaut 3.6.3 but when I upgrade to 4.2.2, the injected BeanContext cannot find any bean. `beanContext.getBean(ProtoTypeBean.class);` will throw a `NoSuchBeanException`

To clarify what I mean, I have a reproducible case in [my github repository](https://github.com/auricgoldfinger/micronaut-daemon).

A simplified version of the code looks like this:

The main application starts the `ApplicationContext` which creates an instance of ScheduleReader and calls `run()`

```java
public static void main(String[] args) {
    try (final ApplicationContext context = ApplicationContext.run()) {
        context.getBean(ScheduleReader.class).run();
    }
}
```

In the `ScheduleReader.run()` method, I use the injected `BeanContext` to create an instance of another Bean

```java
@Singleton
@Slf4j
public class ScheduleReader implements Runnable {

   private final BeanContext beanContext;

   @Inject
   ScheduleReader(final BeanContext beanContext) {
      this.beanContext = beanContext;
   }

   @Override
   public void run() {
      beanContext.getBean(Action.class).run();
   }

}
```

`Action` is a `@Prototype` as well, but at this point, I get the following exception:

```
12:22:29.946 [pool-1-thread-1] ERROR com.example.ScheduleReader - Error while starting Action: No bean of type [com.example.Action] exists. Make sure the bean is not disabled by bean requirements (enable trace logging for 'io.micronaut.context.condition' to check) and if the bean is enabled then ensure the class is declared a bean and annotation processing is enabled (for Java and Kotlin the 'micronaut-inject-java' dependency should be configured as an annotation processor).
io.micronaut.context.exceptions.NoSuchBeanException: No bean of type [com.example.Action] exists. Make sure the bean is not disabled by bean requirements (enable trace logging for 'io.micronaut.context.condition' to check) and if the bean is enabled then ensure the class is declared a bean and annotation processing is enabled (for Java and Kotlin the 'micronaut-inject-java' dependency should be configured as an annotation processor).
	at io.micronaut.context.DefaultBeanContext.newNoSuchBeanException(DefaultBeanContext.java:2773)
	at io.micronaut.context.DefaultApplicationContext.newNoSuchBeanException(DefaultApplicationContext.java:304)
	at io.micronaut.context.DefaultBeanContext.resolveBeanRegistration(DefaultBeanContext.java:2735)
	at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:1729)
	at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:856)
	at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:848)
	at com.example.ScheduleReader.runPlanner(ScheduleReader.java:41)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:539)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:304)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	at java.base/java.lang.Thread.run(Thread.java:833)
```

When I use another `ApplicationContext.run()` in `ScheduleReader`, then I can use that application context to retrieve other beans, but I cannot imagine you now have to start ApplicationContexts every time you want to create a new instance of a bean...

The Micronaut guides are focused on launching applications with rest controllers, but as said, this is just a stand-alone application that doesn't expose endpoints. I couldn't find any guide for such application. I also didn't find any clue in the migration guides on what was changed or how to solve this problem.

What am I missing?

_I asked this question on [stackoverflow](https://stackoverflow.com/questions/77764338/micronaut-injected-beancontext-cannot-retrieve-beans) without any success, so I'm trying here._

## Reason for problem
For some reason, with the upgrade of Micronaut, the autocloseable is called while the
`ScheduledExecutorService` is still running.

Because the `ApplicationContext` is closed, `DefaultBeanContext#stop()` is called which
clears the `beanDefinitionClasses` cache. At that point, no bean is found anymore, which 
causes the exception.

The previous version of Micronaut didn't clear this cache, so the problem didn't occur.

## Solution
One solution is to keep the ScheduleReader running using a `Thread.currentThread().join();`
but other solutions could be possible as well.