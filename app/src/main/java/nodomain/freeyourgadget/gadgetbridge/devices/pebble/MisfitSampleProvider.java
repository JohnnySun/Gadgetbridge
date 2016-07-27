package nodomain.freeyourgadget.gadgetbridge.devices.pebble;

import java.util.List;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import nodomain.freeyourgadget.gadgetbridge.devices.AbstractSampleProvider;
import nodomain.freeyourgadget.gadgetbridge.devices.SampleProvider;
import nodomain.freeyourgadget.gadgetbridge.entities.DaoSession;
import nodomain.freeyourgadget.gadgetbridge.entities.PebbleMisfitSample;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;

public class MisfitSampleProvider extends AbstractSampleProvider<PebbleMisfitSample> {

    protected final float movementDivisor = 300f;

    protected MisfitSampleProvider(GBDevice device, DaoSession session) {
        super(device, session);
    }

    @Override
    public int normalizeType(int rawType) {
        return rawType;
    }

    @Override
    public int toRawActivityKind(int activityKind) {
        return (byte) activityKind;
    }

    @Override
    public float normalizeIntensity(int rawIntensity) {
        return rawIntensity / movementDivisor;
    }

    @Override
    public List getAllActivitySamples(int timestamp_from, int timestamp_to) {
        return null;
    }

    @Override
    public List getActivitySamples(int timestamp_from, int timestamp_to) {
        return null;
    }

    @Override
    public List getSleepSamples(int timestamp_from, int timestamp_to) {
        return null;
    }

    @Override
    public void changeStoredSamplesType(int timestampFrom, int timestampTo, int kind) {

    }

    @Override
    public void changeStoredSamplesType(int timestampFrom, int timestampTo, int fromKind, int toKind) {

    }

    @Override
    public AbstractDao<PebbleMisfitSample, ?> getSampleDao() {
        return null;
    }

    @Override
    protected Property getRawKindSampleProperty() {
        return null;
    }

    @Override
    protected Property getTimestampSampleProperty() {
        return null;
    }

    @Override
    protected Property getDeviceIdentifierSampleProperty() {
        return null;
    }

    @Override
    public int fetchLatestTimestamp() {
        return 0;
    }

    @Override
    public PebbleMisfitSample createActivitySample() {
        return null;
    }

    @Override
    public int getID() {
        return SampleProvider.PROVIDER_PEBBLE_MISFIT;
    }
}
