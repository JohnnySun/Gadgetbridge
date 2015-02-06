package nodomain.freeyourgadget.gadgetbridge;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.SimpleTimeZone;

public class PebbleProtocol {
    static final short ENDPOINT_FIRMWARE = 1;
    static final short ENDPOINT_TIME = 11;
    static final short ENDPOINT_FIRMWAREVERSION = 16;
    static final short ENDPOINT_PHONEVERSION = 17;
    static final short ENDPOINT_SYSTEMMESSAGE = 18;
    static final short ENDPOINT_MUSICCONTROL = 32;
    static final short ENDPOINT_PHONECONTROL = 33;
    static final short ENDPOINT_APPLICATIONMESSAGE = 48;
    static final short ENDPOINT_LAUNCHER = 49;
    static final short ENDPOINT_LOGS = 2000;
    static final short ENDPOINT_PING = 2001;
    static final short ENDPOINT_LOGDUMP = 2002;
    static final short ENDPOINT_RESET = 2003;
    static final short ENDPOINT_APP = 2004;
    static final short ENDPOINT_APPLOGS = 2006;
    static final short ENDPOINT_NOTIFICATION = 3000;
    static final short ENDPOINT_RESOURCE = 4000;
    static final short ENDPOINT_SYSREG = 5000;
    static final short ENDPOINT_FCTREG = 5001;
    static final short ENDPOINT_APPMANAGER = 6000;
    static final short ENDPOINT_DATALOG = 6778;
    static final short ENDPOINT_RUNKEEPER = 7000;
    static final short ENDPOINT_SCREENSHOT = 8000;
    static final short ENDPOINT_PUTBYTES = (short) 48879;

    static final byte NOTIFICATION_EMAIL = 0;
    static final byte NOTIFICATION_SMS = 1;
    static final byte NOTIFICATION_TWITTER = 2;
    static final byte NOTIFICATION_FACEBOOK = 3;

    static final byte PHONECONTROL_ANSWER = 1;
    static final byte PHONECONTROL_HANGUP = 2;
    static final byte PHONECONTROL_GETSTATE = 3;
    static final byte PHONECONTROL_INCOMINGCALL = 4;
    static final byte PHONECONTROL_OUTGOINGCALL = 5;
    static final byte PHONECONTROL_MISSEDCALL = 6;
    static final byte PHONECONTROL_RING = 7;
    static final byte PHONECONTROL_START = 8;
    static final byte PHONECONTROL_END = 9;

    static final short LENGTH_PREFIX = 4;
    static final short LENGTH_SETTIME = 9;
    static final short LENGTH_PHONEVERSION = 17;

    static final byte TIME_GETTIME = 0;
    static final byte TIME_SETTIME = 2;

    static final byte PHONEVERSION_APPVERSION = 2; // increase this if pebble complains

    static final int PHONEVERSION_SESSION_CAPS_GAMMARAY = (int) 0x80000000;

    static final int PHONEVERSION_REMOTE_CAPS_TELEPHONY = 0x00000010;
    static final int PHONEVERSION_REMOTE_CAPS_SMS = 0x00000020;
    static final int PHONEVERSION_REMOTE_CAPS_GPS = 0x00000040;
    static final int PHONEVERSION_REMOTE_CAPS_BTLE = 0x00000080;
    static final int PHONEVERSION_REMOTE_CAPS_REARCAMERA = 0x00000100;
    static final int PHONEVERSION_REMOTE_CAPS_ACCEL = 0x00000200;
    static final int PHONEVERSION_REMOTE_CAPS_GYRO = 0x00000400;
    static final int PHONEVERSION_REMOTE_CAPS_COMPASS = 0x00000800;

    static final byte PHONEVERSION_REMOTE_OS_UNKNOWN = 0;
    static final byte PHONEVERSION_REMOTE_OS_IOS = 1;
    static final byte PHONEVERSION_REMOTE_OS_ANDROID = 2;
    static final byte PHONEVERSION_REMOTE_OS_OSX = 3;
    static final byte PHONEVERSION_REMOTE_OS_LINUX = 4;
    static final byte PHONEVERSION_REMOTE_OS_WINDOWS = 5;

    static byte[] encodeMessage(short endpoint, byte type, String[] parts) {
        // Calculate length first
        int length = LENGTH_PREFIX + 1;
        for (String s : parts) {
            length += (1 + s.getBytes().length);
        }

        // Encode Prefix
        ByteBuffer buf = ByteBuffer.allocate(length);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) (length - LENGTH_PREFIX));
        buf.putShort(endpoint);
        buf.put(type);

        // Encode Pascal-Style Strings
        for (String s : parts) {

            int partlength = s.getBytes().length;
            if (partlength > 255) partlength = 255;
            buf.put((byte) partlength);
            buf.put(s.getBytes(), 0, partlength);
        }

        return buf.array();
    }

    public static byte[] encodeSMS(String from, String body) {
        Long ts = System.currentTimeMillis() / 1000;
        ts += SimpleTimeZone.getDefault().getOffset(ts) / 1000;
        String tsstring = ts.toString();  // SIC
        String[] parts = {from, body, tsstring};

        return encodeMessage(ENDPOINT_NOTIFICATION, NOTIFICATION_SMS, parts);
    }

    public static byte[] encodeEmail(String from, String subject, String body) {
        Long ts = System.currentTimeMillis() / 1000;
        ts += SimpleTimeZone.getDefault().getOffset(ts) / 1000;
        String tsstring = ts.toString(); // SIC
        String[] parts = {from, body, tsstring, subject};

        return encodeMessage(ENDPOINT_NOTIFICATION, NOTIFICATION_EMAIL, parts);
    }

    public static byte[] encodeSetTime(long ts) {
        if (ts == -1) {
            ts = System.currentTimeMillis() / 1000;
            ts += SimpleTimeZone.getDefault().getOffset(ts) / 1000;
        }
        ByteBuffer buf = ByteBuffer.allocate(LENGTH_SETTIME);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) (LENGTH_SETTIME - LENGTH_PREFIX));
        buf.putShort(ENDPOINT_TIME);
        buf.put(TIME_SETTIME);
        buf.putInt((int) ts);

        return buf.array();
    }

    public static byte[] encodeIncomingCall(String number, String name, byte state) {
        String cookie = "000"; // That's a dirty trick to make the cookie part 4 bytes long :P
        String[] parts = {cookie, number, name};
        return encodeMessage(ENDPOINT_PHONECONTROL, state, parts);
    }

    public static byte[] encodePhoneVersion(byte os) {
        ByteBuffer buf = ByteBuffer.allocate(LENGTH_PHONEVERSION);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.putShort((short) (LENGTH_PHONEVERSION - LENGTH_PREFIX));
        buf.putShort(ENDPOINT_PHONEVERSION);
        buf.put(PHONEVERSION_APPVERSION);

        buf.putInt(-1); // TODO: Find out what this is (cookie?)

        if (os == PHONEVERSION_REMOTE_OS_ANDROID) {
            buf.putInt(PHONEVERSION_SESSION_CAPS_GAMMARAY);
        } else {
            buf.putInt(0);
        }
        buf.putInt(PHONEVERSION_REMOTE_CAPS_SMS | PHONEVERSION_REMOTE_CAPS_TELEPHONY | os);

        return buf.array();
    }

    // FIXME: that should return data into some unified struct/Class
    public static String decodeResponse(byte[] responseData) {
        ByteBuffer buf = ByteBuffer.wrap(responseData);
        buf.order(ByteOrder.BIG_ENDIAN);
        short length = buf.getShort();
        short endpoint = buf.getShort();
        byte extra = 0;

        switch (endpoint) {
            case ENDPOINT_PHONECONTROL:
                extra = buf.get();
                break;
            default:
                break;
        }
        String ret = "length: " + Short.toString(length) + " Endpoint:" + Short.toString(endpoint) + "/" + Byte.toString(extra);

        return ret;
    }
}