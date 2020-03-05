import java.time.LocalDateTime;
import java.util.*;

public class TopValuesWithinNDays {
    private Queue<IdToTime> recentTimes;
    private final int n;

    public TopValuesWithinNDays(int n) {
        this.n = n;
        this.recentTimes = new LinkedList<>();
    }

    public void addValue(String id, LocalDateTime time) {
        recentTimes.add(new IdToTime(id, time));
    }

    public void addValue(String id) {
        addValue(id, LocalDateTime.now());
    }

    public boolean isTopKValue(String id, int k) {
        Map<String, Long> counts = new HashMap<>();

        Queue<IdToTime> tempTimes = new LinkedList<>(recentTimes);
        for (IdToTime idToTime : tempTimes) {
            if (idToTime.time.isBefore(LocalDateTime.now().minusDays(n))) {
                if (!recentTimes.isEmpty()) {
                    recentTimes.poll();
                }
            } else {
                if (counts.containsKey(idToTime.id)) {
                    long count = counts.get(idToTime.id);
                    counts.put(idToTime.id, count + 1);
                } else {
                    counts.put(idToTime.id, 1L);
                }
            }
        }

        List<Map.Entry<String, Long>> values = new ArrayList<>(counts.entrySet());
        values.sort((first, second) -> Long.compare(first.getValue(), second.getValue()) * -1);
        for (int i = 0; i < k && i < values.size(); i++) {
            String value = values.get(i).getKey();
            if (id.equals(value)) {
                return true;
            }
        }

        return false;
    }

    public int getRecentTimesSize() {
        return recentTimes.size();
    }

    private static class IdToTime {
        public String id;
        public LocalDateTime time;

        public IdToTime(String id, LocalDateTime time) {
            this.id = id;
            this.time = time;
        }
    }
}
