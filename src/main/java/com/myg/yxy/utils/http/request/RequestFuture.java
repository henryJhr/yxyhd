package com.myg.yxy.utils.http.request;

import org.apache.http.impl.client.FutureRequestExecutionMetrics;
import org.apache.http.impl.client.FutureRequestExecutionService;
import org.apache.http.impl.client.HttpRequestFutureTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * HttpRequestFutureTask包装器,带有FutureRequestExecutionMetrics
 * 这是一个异步的执行结果, 你可以取消任务
 * 并且带有跟踪指标,可以查看这任务的开始、结束时间，任务执行时长，请求数量
 * @author mdc
 * @date 2017年6月11日
 * @param <V>
 */
public class RequestFuture<V> {
	
	/**
	 * 日志
	 */
	protected static Logger logger = LoggerFactory.getLogger(RequestFuture.class);
	
	/**
	 * 任务
	 */
	private HttpRequestFutureTask<V> task;
	
	private FutureRequestExecutionService executionService;
	
	public RequestFuture(final HttpRequestFutureTask<V> task, final FutureRequestExecutionService executionService) {
		this.task = task;
		this.executionService = executionService;
	}

	public boolean cancel(final boolean mayInterruptIfRunning) {
		return task.cancel(mayInterruptIfRunning);
	}

	/**
	 * @return the time in millis the task was scheduled.
	 */
	public long scheduledTime() {
		return task.scheduledTime();
	}

	/**
	 * @return the time in millis the task was started.
	 */
	public long startedTime() {
		return task.startedTime();
	}

	/**
	 * @return the time in millis the task was finished/cancelled.
	 */
	public long endedTime() {
		return task.endedTime();
	}

	/**
	 * @return the time in millis it took to make the request (excluding the
	 *         time it was scheduled to be executed).
	 */
	public long requestDuration() {
		return task.requestDuration();
	}

	/**
	 * @return the time in millis it took to execute the task from the moment it
	 *         was scheduled.
	 */
	public long taskDuration() {
		return task.taskDuration();
	}
	
	/**
	 * @return 获取{@link FutureRequestExecutionMetrics}
	 */
	public FutureRequestExecutionMetrics metrics() {
		return executionService.metrics();
	}
	
	/**
	 * 关闭连接池,将中断所有任务的执行
	 * @author mdc
	 * @date 2017年6月11日
	 */
	public void shutdown(){
		try {
			executionService.close();
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}
	
	/**
	 * 阻塞获取返回值，返回值的结果由ResponseHandler返回
	 * @author mdc
	 * @date 2017年6月11日
	 * @return 由ResponseHandler处理后的值
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public V get() throws InterruptedException, ExecutionException{
		return task.get();
	}
	
	/**
	 * 阻塞获取返回值,当超时后抛出异常，返回值的结果由ResponseHandler返回
	 * @author mdc
	 * @date 2017年6月11日
	 * @param timeout 超时时长
	 * @param unit 时间单位
	 * @return 由ResponseHandler处理后的值
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException 
	 */
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException{
		return task.get(timeout, unit);
	}

	@Override
	public String toString() {
		return task.toString();
	}
}
