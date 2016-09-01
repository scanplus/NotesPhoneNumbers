package de.scanplus.notesphonenumbers;

import org.apache.logging.log4j.LogManager;
import static org.quartz.JobBuilder.newJob;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import static org.quartz.SimpleScheduleBuilder.repeatHourlyForever;
import org.quartz.Trigger;
import static org.quartz.TriggerBuilder.newTrigger;
import org.quartz.impl.StdSchedulerFactory;

public class App {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(App.class);

    private static Scheduler SCHED = null;

    public static void main(String[] args) throws SchedulerException {
        addShutdownHook();
        SCHED = StdSchedulerFactory.getDefaultScheduler();
        SCHED.start();

        JobDetail job = newJob(UpdateAddressJob.class)
                .withIdentity("UpdateAddresses", "UpdateGroup")
                .build();
        Trigger trigger = newTrigger()
                .withIdentity("UpdateAddresses", "UpdateGroup")
                .startNow().withSchedule(repeatHourlyForever())
                .build();
        SCHED.scheduleJob(job, trigger);
    }

    private static void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    LOG.info("Scheduler shutdown has begun");
                    SCHED.shutdown();
                } catch (SchedulerException ex) {
                    LOG.error("Error in shutdown", ex);
                }
            }
        });
    }

}
