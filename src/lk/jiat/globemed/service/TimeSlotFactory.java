package lk.jiat.globemed.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lk.jiat.globemed.model.TimeSlot;

public class TimeSlotFactory {

    private static final Map<String, TimeSlot> timeSlots = new HashMap<>();
    private static int createdInstances = 0;
    private static int requestedInstances = 0;

    public static TimeSlot getTimeSlot(String time, int duration, String timeSlotType) {
        requestedInstances++;

        String key = createKey(time, duration, timeSlotType);
        TimeSlot slot = timeSlots.get(key);

        if (slot == null) {
            slot = new TimeSlot(time, duration, timeSlotType);
            timeSlots.put(key, slot);
            createdInstances++;

            System.out.println(String.format("✨ New TimeSlot flyweight created: %s (Total: %d/%d)",
                    key, createdInstances, requestedInstances));
        } else {
            System.out.println(String.format("♻️ Reusing existing TimeSlot flyweight: %s", key));
        }

        return slot;
    }

    public static TimeSlot getConsultationSlot(String time, int duration) {
        return getTimeSlot(time, duration, "CONSULTATION");
    }

    public static TimeSlot getSurgerySlot(String time, int duration) {
        return getTimeSlot(time, duration, "SURGERY");
    }

    public static TimeSlot getCheckupSlot(String time, int duration) {
        return getTimeSlot(time, duration, "CHECKUP");
    }

    public static void initializeStandardSlots() {
        System.out.println("🏥 Initializing standard hospital time slots...");

        String[] morningTimes = {"08:00", "08:30", "09:00", "09:30", "10:00", "10:30", "11:00", "11:30"};
        for (String time : morningTimes) {
            getConsultationSlot(time, 30);
        }

        String[] afternoonTimes = {"13:00", "13:30", "14:00", "14:30", "15:00", "15:30", "16:00", "16:30"};
        for (String time : afternoonTimes) {
            getConsultationSlot(time, 30);
        }

        String[] surgeryTimes = {"08:00", "10:00", "14:00", "16:00"};
        for (String time : surgeryTimes) {
            getSurgerySlot(time, 120);
        }

        String[] checkupTimes = {"08:00", "08:15", "08:30", "08:45", "09:00", "09:15", "09:30", "09:45"};
        for (String time : checkupTimes) {
            getCheckupSlot(time, 15);
        }

        System.out.println(String.format("✅ Standard slots initialized. Created: %d, Total requests: %d",
                createdInstances, requestedInstances));
    }

    public static Map<String, TimeSlot> getSlotsByType(String timeSlotType) {
        Map<String, TimeSlot> result = new HashMap<>();

        for (Map.Entry<String, TimeSlot> entry : timeSlots.entrySet()) {
            if (entry.getValue().getTimeSlotType().equalsIgnoreCase(timeSlotType)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    public static void printStatistics() {
        System.out.println("\n📊 TimeSlot Flyweight Statistics:");
        System.out.println("================================");
        System.out.println("Total instances created: " + createdInstances);
        System.out.println("Total instances requested: " + requestedInstances);
        System.out.println("Memory saved: " + (requestedInstances - createdInstances) + " instances");
        System.out.println("Memory efficiency: "
                + String.format("%.1f%%", (1.0 - (double) createdInstances / requestedInstances) * 100));
        System.out.println("Unique time slots: " + timeSlots.size());

        System.out.println("\nTime slot types distribution:");
        Map<String, Integer> typeCount = new HashMap<>();
        for (TimeSlot slot : timeSlots.values()) {
            typeCount.merge(slot.getTimeSlotType(), 1, Integer::sum);
        }

        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " slots");
        }
        System.out.println("================================\n");
    }

    public static void clearAll() {
        timeSlots.clear();
        createdInstances = 0;
        requestedInstances = 0;
        System.out.println("🧹 All TimeSlot flyweights cleared");
    }

    public static int getCreatedInstancesCount() {
        return createdInstances;
    }

    public static int getRequestedInstancesCount() {
        return requestedInstances;
    }

    public static Set<String> getAllTimeSlotKeys() {
        return timeSlots.keySet();
    }

    private static String createKey(String time, int duration, String timeSlotType) {
        return time + "_" + duration + "_" + timeSlotType.toUpperCase();
    }
}
