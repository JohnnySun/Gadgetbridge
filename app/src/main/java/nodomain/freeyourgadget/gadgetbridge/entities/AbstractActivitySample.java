package nodomain.freeyourgadget.gadgetbridge.entities;

import nodomain.freeyourgadget.gadgetbridge.model.ActivitySample;
import nodomain.freeyourgadget.gadgetbridge.util.DateTimeUtils;

public abstract class AbstractActivitySample extends AbstractSample implements ActivitySample {
    @Override
    public int getKind() {
        return getProvider().normalizeType(getRawKind());
    }

    @Override
    public float getIntensity() {
        return getProvider().normalizeIntensity(getRawIntensity());
    }

    public abstract void setRawKind(int kind);

    public abstract void setRawIntensity(int intensity);

    public abstract void setSteps(int steps);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "timestamp=" + DateTimeUtils.formatDateTime(DateTimeUtils.parseTimeStamp(getTimestamp())) +
                ", intensity=" + getIntensity() +
                ", steps=" + getSteps() +
                ", type=" + getKind() +
                ", userId=" + getUserId() +
                ", deviceId=" + getDeviceId() +
                '}';
    }
}
