package sener.blocksatportable.Communications;

import java.util.HashMap;
import java.util.UUID;

/// TODO: Consider delete this class

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
    public static String BLE_BATTERY_SERVICE = "0000180f-0000-1000-8000-00805f9b34fb";
    public static String BATTERY_CHARACTERISTIC = "00002a19-0000-1000-8000-00805f9b34fb";
    public static String POSITION_SERVICE = "00001819-0000-1000-8000-00805f9b34fb";
    public static String POSITION_CHARACTERISTIC = "00002a2f-0000-1000-8000-00805f9b34fb";
    public static String SIGNAL_SERVICE = "00001804-0000-1000-8000-00805f9b34fb";
    public static String SIGNAL_CHARACTERISTIC = "00002a07-0000-1000-8000-00805f9b34fb";
    public static String GSM_SERVICE = "00110011-4455-6677-8899-aabbccddeeff";
    public static String GSM_STATE_CHARACTERISTIC = "00000002-0000-1000-8000-00805f9b34fb";
    public static String GSM_SIGNAL_CHARACTERISTIC = "00000003-0000-1000-8000-00805f9b34fb";
    public static String GSM_RX_CHARACTERISTIC = "00000004-0000-1000-8000-00805f9b34fb";
    public static String GSM_TX_CHARACTERISTIC = "00000005-0000-1000-8000-00805f9b34fb";
    public static String DESCRIPTOR = "00002902-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put(BLE_BATTERY_SERVICE, "Battery Service");
        attributes.put(POSITION_SERVICE, "Position Service");
        attributes.put(SIGNAL_SERVICE, "BLE Signal Service");
        attributes.put(GSM_SERVICE, "GSM Service");
        attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
        // Sample Characteristics.
        attributes.put(BATTERY_CHARACTERISTIC, "Battery Level");
        attributes.put(POSITION_CHARACTERISTIC, "Position 2D");
        attributes.put(SIGNAL_CHARACTERISTIC, "BLE Signal");
        attributes.put(GSM_STATE_CHARACTERISTIC, "GSM State");
        attributes.put(GSM_SIGNAL_CHARACTERISTIC, "GSM Signal");
        attributes.put(GSM_RX_CHARACTERISTIC, "GSM Rx Message");
        attributes.put(GSM_TX_CHARACTERISTIC, "GSM Tx Message");
        attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
        // Descriptor
        attributes.put(DESCRIPTOR, "Descriptor UUID");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }

    public static int getAssignedNumber(UUID uuid) {
        // Keep only the significant bits of the UUID
        return (int) ((uuid.getMostSignificantBits() & 0x0000FFFF00000000L) >> 32);
    }

    public static String getUuidString(UUID uuid) {
        // Keep only the significant bits of the UUID
        String uuidString = uuid.toString();

        return uuidString.substring(4,8);
    }
}
