package com.lizhi.xiaohashu.distributed.id.generator.biz.core.segment.model;

import java.util.concurrent.atomic.AtomicLong;

public class Segment {
    private AtomicLong value = new AtomicLong(0);
    private volatile long max;
    private volatile int step;
    private com.lizhi.xiaohashu.distributed.id.generator.biz.core.segment.model.SegmentBuffer buffer;

    public Segment(com.lizhi.xiaohashu.distributed.id.generator.biz.core.segment.model.SegmentBuffer buffer) {
        this.buffer = buffer;
    }

    public AtomicLong getValue() {
        return value;
    }

    public void setValue(AtomicLong value) {
        this.value = value;
    }

    public long getMax() {
        return max;
    }

    public void setMax(long max) {
        this.max = max;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public com.lizhi.xiaohashu.distributed.id.generator.biz.core.segment.model.SegmentBuffer getBuffer() {
        return buffer;
    }

    public long getIdle() {
        return this.getMax() - getValue().get();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Segment(");
        sb.append("value:");
        sb.append(value);
        sb.append(",max:");
        sb.append(max);
        sb.append(",step:");
        sb.append(step);
        sb.append(")");
        return sb.toString();
    }
}
