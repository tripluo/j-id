/** Copyright 2010-2012 Twitter, Inc. */
package net.jrouter.id.impl;

import java.util.logging.Logger;
import lombok.AccessLevel;

/**
 * https://github.com/twitter/snowflake
 * An object that generates IDs.
 * This is broken into a separate class in case
 * we ever want to support multiple worker threads
 * per process
 */
@lombok.Getter
public class IdWorker {

    private static final Logger LOG = Logger.getLogger(IdWorker.class.getName());

    public static final long DEFAULT_WORKERID_BITS = 5L;

    public static final long DEFAULT_DATACENTERID_BITS = 5L;

    private long workerId;

    private long datacenterId;

    private long sequence = 0L;

    //20101104 09:42:54
    @lombok.Getter(AccessLevel.NONE)
    private final long twepoch = 1288834974657L;

    private final long workerIdBits = DEFAULT_WORKERID_BITS;

    private final long datacenterIdBits = DEFAULT_DATACENTERID_BITS;

    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    private final long sequenceBits = 12L;

    private final long workerIdShift = sequenceBits;

    private final long datacenterIdShift = sequenceBits + workerIdBits;

    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    private long lastTimestamp = -1L;

    private final long maxGlobalWorkerId = -1L ^ (-1L << (workerIdBits + datacenterIdBits));

    public IdWorker(long globalWorkerId) {
        this(globalWorkerId & (-1L ^ (-1L << DEFAULT_WORKERID_BITS)), globalWorkerId >> DEFAULT_WORKERID_BITS);
    }

    public IdWorker(long workerId, long datacenterId) {
        // sanity check for workerId
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
        LOG.info(String.format("worker starting. timestamp left shift %d, datacenter id bits %d, worker id bits %d, sequence bits %d, workerid %d", timestampLeftShift, datacenterIdBits, workerIdBits, sequenceBits, workerId));
    }

    /**
     * Generate next id.
     *
     * @return the next id.
     */
    public synchronized long nextId() {//NOPMD
        long timestamp = timeGen();
        if (timestamp < lastTimestamp) {
            LOG.severe(String.format("clock is moving backwards.  Rejecting requests until %d.", lastTimestamp));
            throw new IllegalArgumentException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;
        return ((timestamp - initialTimeMillis()) << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    /**
     * Generate next TimeMillis.
     *
     * @param lastTimestamp last TimeMillis.
     *
     * @return the next TimeMillis.
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * Generate time.
     *
     * @return Current TimeMillis.
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 初始时间。
     *
     * @return 初始时间。
     */
    protected long initialTimeMillis() {
        return twepoch;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////

    /** 所能生成的最小id */
    public long minId() {
        return (1 << timestampLeftShift) | (datacenterId << datacenterIdShift) | (workerId << workerIdShift) | sequence;
    }

    /** 根据生成的id解析时间（毫秒） */
    public long parseTimeMillis(long id) {
        return (id >> timestampLeftShift) + initialTimeMillis();
    }

    /** 根据生成的id解析datacenterId */
    public long parseDatacenterId(long id) {
        return (id >> datacenterIdShift) & maxDatacenterId;
    }

    /** 根据生成的id解析workerId */
    public long parseWorkerId(long id) {
        return (id >> workerIdShift) & maxWorkerId;
    }

    /** 根据生成的id解析globalWorkerId */
    public long parseGlobalWorkerId(long id) {
        return (id >> workerIdShift) & maxGlobalWorkerId;
    }

    /** 根据生成的id解析sequence */
    public long parseSequence(long id) {
        return id & sequenceMask;
    }

}
