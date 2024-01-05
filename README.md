## Micronaut Daemon Example

This example demonstrates how to use Micronaut to create a daemon application.

### Run the application

Every two seconds, the current time is printed to the console.

```bash
./gradlew runWithSchedule
```

### Problem

When upgrading to Micronaut 4.2.1, the application no longer runs. You will see the following error:

```
ERROR com.example.ScheduleReader - Error while starting Action: No bean of type [com.example.Action] exists. Make sure the bean is not disabled by bean requirements (enable trace logging for 'io.micronaut.context.condition' to check) and if the bean is enabled then ensure the class is declared a bean and annotation processing is enabled (for Java and Kotlin the 'micronaut-inject-java' dependency should be configured as an annotation processor).
io.micronaut.context.exceptions.NoSuchBeanException: No bean of type [com.example.Action] exists. Make sure the bean is not disabled by bean requirements (enable trace logging for 'io.micronaut.context.condition' to check) and if the bean is enabled then ensure the class is declared a bean and annotation processing is enabled (for Java and Kotlin the 'micronaut-inject-java' dependency should be configured as an annotation processor).
	at io.micronaut.context.DefaultBeanContext.newNoSuchBeanException(DefaultBeanContext.java:2773)
	at io.micronaut.context.DefaultApplicationContext.newNoSuchBeanException(DefaultApplicationContext.java:304)
	at io.micronaut.context.DefaultBeanContext.resolveBeanRegistration(DefaultBeanContext.java:2735)
	at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:1729)
	at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:856)
	at io.micronaut.context.DefaultBeanContext.getBean(DefaultBeanContext.java:848)
	at com.example.ScheduleReader.runPlanner(ScheduleReader.java:42)
	at java.base/java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:539)
	at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:264)
	at java.base/java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:304)
	at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1136)
	at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:635)
	at java.base/java.lang.Thread.run(Thread.java:833)
```