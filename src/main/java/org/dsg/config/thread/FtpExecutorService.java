package org.dsg.config.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * @author
 * @since
 */
@Component
public class FtpExecutorService {

    public static final Integer DEFAULT_THREAD_AMOUNT = 2  << 3 ;
    private static final Logger LOGGER = LoggerFactory.getLogger(FtpExecutorService.class);
    /**
     * 执行线程
     */
    private static final ExecutorService SERVICE;

    /**
     * init executor thread
     */
    static {
        SERVICE = Executors.newFixedThreadPool(DEFAULT_THREAD_AMOUNT);
    }


    public <T> T run(Callable<T> callable) {
        try {
            Future<T> future = FtpExecutorService.SERVICE.submit(callable);
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 直接执行
     *
     * @param runnable
     */
    public void run(Runnable runnable) {
        FtpExecutorService.SERVICE.submit(runnable);
    }


    @PreDestroy
    public void destroy() {
        LOGGER.info(" Destroy Executors .");
        FtpExecutorService.SERVICE.shutdown();
        FtpExecutorService.SERVICE.shutdownNow();
    }
}
