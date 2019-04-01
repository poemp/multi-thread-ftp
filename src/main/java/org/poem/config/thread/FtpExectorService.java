package org.poem.config.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PreDestroy;
import java.util.concurrent.*;

/**
 * @author
 * @since
 */
public class FtpExectorService {

  private static final Logger LOGGER = LoggerFactory.getLogger(FtpExectorService.class);
  /**
   * 执行线程
   */
  private static final ExecutorService SERVICE;
  private static final Integer DEFAULT_THREAD_AMOUNT =  2 << 4;

  /**
   * init executor thread
   */
  static {
    SERVICE = Executors.newFixedThreadPool(DEFAULT_THREAD_AMOUNT);
  }


  public <T> T run(Callable<T> callable){
    Future<T> future = FtpExectorService.SERVICE.submit(callable);
    try {
      return future.get();
    } catch (InterruptedException | ExecutionException e) {
      LOGGER.error(e.getMessage(),e);
      e.printStackTrace();
    }
    return null;
  }

  /**
   * 直接执行
   * @param runnable
   */
  public void run(Runnable runnable){
    FtpExectorService.SERVICE.execute(runnable);
  }


  @PreDestroy
  public void destroy(){
    LOGGER.info(" Destroy Executors .");
    FtpExectorService.SERVICE.shutdown();
  }
}
