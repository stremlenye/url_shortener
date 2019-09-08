package eu.ankudinov.urlshortener.common

import java.util.concurrent.{ExecutorService, Executors, RejectedExecutionHandler, SynchronousQueue, ThreadPoolExecutor, TimeUnit}

import com.twitter.concurrent.NamedPoolThreadFactory

import scala.concurrent.ExecutionContext

object executors {

  val cpus: Int = Runtime.getRuntime.availableProcessors
  val min: Int = 5 * cpus
  val max: Int = 15 * cpus
  final val defaultKeepAliveSeconds = 60L

  lazy val global: ThreadPoolExecutor = daemon("api.global", min, max)

  lazy val globalEc: ExecutionContext = ExecutionContext.fromExecutor(global)

  /** Thread pool for blocking IO with unbounded queue. */
  lazy val blockingIoExecutor: ExecutorService =
    Executors.newFixedThreadPool(10 * cpus, new NamedPoolThreadFactory("spotcap.api.blockingIoExecutor", makeDaemons = true))

  /** ExecutionContext for blocking IO with unbounded queue. */
  lazy val blockingIoEc: ExecutionContext = ExecutionContext.fromExecutor(blockingIoExecutor)

  private def daemon(
                      context: String,
                      minThreads: Int,
                      maxThreads: Int,
                      keepAliveSeconds: Long = defaultKeepAliveSeconds,
                      policy: RejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy
                    ): ThreadPoolExecutor = {
    val factory = new NamedPoolThreadFactory(context, makeDaemons = true)
    val queue = new SynchronousQueue[Runnable]
    val pool = new ThreadPoolExecutor(minThreads, maxThreads, keepAliveSeconds, TimeUnit.SECONDS, queue, factory)
    pool.setRejectedExecutionHandler(policy)
    pool
  }
}
