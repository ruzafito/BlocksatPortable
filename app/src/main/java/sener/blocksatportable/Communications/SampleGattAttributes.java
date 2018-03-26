package sener.blocksatportable.Communications;

import java.util.HashMap;

/**
 * <p>
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 * </p>
 *
 * @author  Sergio del Horno
 * @version 1.0.0
 * @since   2018-03-10
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap();
    public static String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
    public static String BLOCKSAT_uuid = "00002a39-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

//    public static int getAssignedNumber(UUID uuid) {
//        // Keep only the significant bits of the UUID
//        return (int) ((uuid.getMostSignificantBits() & 0x0000FFFF00000000L) >> 32);
//    }
//
//    public static int getAssignedNumber(String uuid_text) {
//        UUID uuid = UUID.fromString(uuid_text);
//        // Keep only the significant bits of the UUID
//        return (int) ((uuid.getMostSignificantBits() & 0x0000FFFF00000000L) >> 32);
//    }
}
