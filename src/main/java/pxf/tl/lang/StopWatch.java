package pxf.tl.lang;


import pxf.tl.function.LongSupplierThrow;

/**
 * 状态
 */
interface State {

    /**
     * 该方法用于确定秒表是否已启动，暂停的秒表也可以开始观看。
     *
     * @return 如果秒表已启动返回 {@code true}，否则 {@code false}
     */
    boolean isStarted();

    /**
     * 此方法用于查明秒表是否已停止，尚未启动且已显式停止的秒表被视为已停止。
     *
     * @return 如果秒表已停止返回 {@code true}，否则 {@code false}
     */
    boolean isStopped();

    /**
     * 此方法用于查明秒表是否已暂停。
     *
     * @return 如果秒表已暂停返回 {@code true}，否则 {@code false}
     */
    boolean isSuspended();
}

/**
 * 秒表
 *
 * @author potatoxf
 */
public class StopWatch implements State {

    private static final SequenceSelector<State> STATE_LIST =
            SequenceSelector.of(
                    new State() {
                        @Override
                        public boolean isStarted() {
                            return false;
                        }

                        @Override
                        public boolean isStopped() {
                            return false;
                        }

                        @Override
                        public boolean isSuspended() {
                            return false;
                        }
                    },
                    new State() {
                        @Override
                        public boolean isStarted() {
                            return true;
                        }

                        @Override
                        public boolean isStopped() {
                            return false;
                        }

                        @Override
                        public boolean isSuspended() {
                            return false;
                        }
                    },
                    new State() {
                        @Override
                        public boolean isStarted() {
                            return true;
                        }

                        @Override
                        public boolean isStopped() {
                            return false;
                        }

                        @Override
                        public boolean isSuspended() {
                            return true;
                        }
                    },
                    new State() {
                        @Override
                        public boolean isStarted() {
                            return false;
                        }

                        @Override
                        public boolean isStopped() {
                            return true;
                        }

                        @Override
                        public boolean isSuspended() {
                            return false;
                        }
                    });
    private static final byte UNSTARTED = 0;
    private static final byte RUNNING = 1;
    private static final byte SUSPENDED = 2;
    private static final byte STOPPED = 3;
    /**
     * 时间提供器
     */
    private final LongSupplierThrow<RuntimeException> timeSupplier;
    /**
     * The current running state of the StopWatch.
     */
    private byte runningState = UNSTARTED;
    /**
     * Whether the stopwatch has a split time recorded.
     */
    private boolean spliteState = false;
    /**
     * The start time.
     */
    private long startTime;
    /**
     * The stop time.
     */
    private long stopTime;

    private StopWatch(LongSupplierThrow<RuntimeException> timeSupplier) {
        this.timeSupplier = timeSupplier;
    }

    /**
     * 创建秒表并启动
     *
     * @return {@code StopWatch}
     */
    public static StopWatch createStarted() {
        return createStarted(System::currentTimeMillis);
    }

    /**
     * 创建秒表并启动
     *
     * @param timeSupplier 时间提供器
     * @return {@code StopWatch}
     */
    public static StopWatch createStarted(LongSupplierThrow<RuntimeException> timeSupplier) {
        StopWatch stopWatch = create(timeSupplier);
        stopWatch.start();
        return stopWatch;
    }

    /**
     * 创建秒表
     *
     * @return {@code StopWatch}
     */
    public static StopWatch create() {
        return create(System::currentTimeMillis);
    }

    /**
     * 创建秒表
     *
     * @param timeSupplier 时间提供器
     * @return {@code StopWatch}
     */
    public static StopWatch create(LongSupplierThrow<RuntimeException> timeSupplier) {
        if (timeSupplier == null) {
            throw new IllegalArgumentException("The time supplier must no null");
        }
        return new StopWatch(timeSupplier);
    }

    /**
     * Start the stopwatch.
     *
     * <p>This method starts a new timing session, clearing any previous values.
     *
     * @throws IllegalStateException if the StopWatch is already running.
     */
    public void start() {
        if (runningState == STOPPED) {
            throw new IllegalStateException("Stopwatch must be reset before being restarted. ");
        }
        if (runningState != UNSTARTED) {
            throw new IllegalStateException("Stopwatch already started. ");
        }
        startTime = timeSupplier.getAsLong();
        runningState = RUNNING;
    }

    /**
     * Stop the stopwatch.
     *
     * <p>This method ends a new timing session, allowing the time to be retrieved.
     *
     * @throws IllegalStateException if the StopWatch is not running.
     */
    public void stop() {
        if (runningState != RUNNING && runningState != SUSPENDED) {
            throw new IllegalStateException("Stopwatch is not running. ");
        }
        if (runningState == RUNNING) {
            stopTime = timeSupplier.getAsLong();
        }
        runningState = STOPPED;
    }

    /**
     * Suspend the stopwatch for later resumption.
     *
     * <p>This method suspends the watch until it is resumed. The watch will not include time between
     * the suspend and resume calls in the total time.
     *
     * @throws IllegalStateException if the StopWatch is not currently running.
     */
    public void suspend() {
        if (runningState != RUNNING) {
            throw new IllegalStateException("Stopwatch must be running to suspend. ");
        }
        stopTime = timeSupplier.getAsLong();
        runningState = SUSPENDED;
    }

    /**
     * Resume the stopwatch after a suspend.
     *
     * <p>This method resumes the watch after it was suspended. The watch will not include time
     * between the suspend and resume calls in the total time.
     *
     * @throws IllegalStateException if the StopWatch has not been suspended.
     */
    public void resume() {
        if (runningState != SUSPENDED) {
            throw new IllegalStateException("Stopwatch must be suspended to resume. ");
        }
        startTime += timeSupplier.getAsLong() - stopTime;
        runningState = RUNNING;
    }

    /**
     * Resets the stopwatch. Stops it if need be.
     *
     * <p>This method clears the internal values to allow the object to be reused.
     */
    public void reset() {
        runningState = UNSTARTED;
        spliteState = true;
    }

    /**
     * Split the time.
     *
     * <p>This method sets the stop time of the watch to allow a time to be extracted. The start time
     * is unaffected, enabling {@link #unsplit()} to continue the timing from the original start
     * point.
     *
     * @throws IllegalStateException if the StopWatch is not running.
     */
    public void split() {
        if (runningState != RUNNING) {
            throw new IllegalStateException("Stopwatch is not running. ");
        }
        stopTime = timeSupplier.getAsLong();
        spliteState = true;
    }

    /**
     * Remove a split.
     *
     * <p>This method clears the stop time. The start time is unaffected, enabling timing from the
     * original start point to continue.
     *
     * @throws IllegalStateException if the StopWatch has not been split.
     */
    public void unsplit() {
        if (!spliteState) {
            throw new IllegalStateException("Stopwatch has not been split. ");
        }
        spliteState = false;
    }

    /**
     * Returns the time this stopwatch was started.
     *
     * @return the time this stopwatch was started
     * @throws IllegalStateException if this StopWatch has not been started
     */
    public long getStartTime() {
        if (runningState == UNSTARTED) {
            throw new IllegalStateException("Stopwatch has not been started");
        }
        return startTime;
    }

    /**
     * Get the time on the stopwatch in nanoseconds.
     *
     * <p>This is either the time between the start and the moment this method is called, or the
     * amount of time between start and stop.
     *
     * @return the time in nanoseconds
     */
    public long getTime() {
        if (runningState == STOPPED || runningState == SUSPENDED) {
            return stopTime - startTime;
        } else if (runningState == UNSTARTED) {
            return 0;
        } else if (runningState == RUNNING) {
            return timeSupplier.getAsLong() - startTime;
        }
        throw new RuntimeException("Illegal running state has occurred.");
    }

    /**
     * Get the split time on the stopwatch.
     *
     * <p>This is the time between start and latest split.
     *
     * @return the split time in milliseconds
     * @throws IllegalStateException if the StopWatch has not yet been split.
     */
    public long getSplitTime() {
        if (!spliteState) {
            throw new IllegalStateException("Stopwatch must be split to get the split time. ");
        }
        return stopTime - startTime;
    }

    /**
     * 该方法用于确定秒表是否已启动，暂停的秒表也可以开始观看。
     *
     * @return 如果秒表已启动返回 {@code true}，否则 {@code false}
     */
    @Override
    public boolean isStarted() {
        return STATE_LIST.selectAction(runningState).isStarted();
    }

    /**
     * 此方法用于查明秒表是否已停止，尚未启动且已显式停止的秒表被视为已停止。
     *
     * @return 如果秒表已停止返回 {@code true}，否则 {@code false}
     */
    @Override
    public boolean isStopped() {
        return STATE_LIST.selectAction(runningState).isStopped();
    }

    /**
     * 此方法用于查明秒表是否已暂停。
     *
     * @return 如果秒表已暂停返回 {@code true}，否则 {@code false}
     */
    @Override
    public boolean isSuspended() {
        return STATE_LIST.selectAction(runningState).isSuspended();
    }
}
