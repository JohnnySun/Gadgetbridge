package nodomain.freeyourgadget.gadgetbridge.entities;

import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.model.Sample;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;

public abstract class AbstractSample implements Sample {
    private SampleProvider mProvider;

    @Override
    public SampleProvider getProvider() {
        return mProvider;
    }

    public void setProvider(SampleProvider provider) {
        mProvider = provider;
    }

    public abstract void setTimestamp(int timestamp);

    public abstract void setUserId(Long userId);

    public abstract Long getUserId();

    public abstract void setDeviceId(Long deviceId);

    public abstract Long getDeviceId();

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "timestamp=" + DateTimeUtils.formatDateTime(DateTimeUtils.parseTimeStamp(getTimestamp())) +
                ", userId=" + getUserId() +
                ", deviceId=" + getDeviceId() +
                '}';
    }
}
