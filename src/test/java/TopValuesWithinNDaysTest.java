import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDateTime;

public class TopValuesWithinNDaysTest {
    @Test
    public void testRecentTimesSize() {
        TopValuesWithinNDays topValues = new TopValuesWithinNDays(3);
        topValues.addValue("f", LocalDateTime.now().minusDays(5));
        topValues.addValue("e", LocalDateTime.now().minusDays(4));
        topValues.addValue("d", LocalDateTime.now().minusDays(3));
        topValues.addValue("c", LocalDateTime.now().minusDays(2));
        topValues.addValue("b", LocalDateTime.now().minusDays(1));
        topValues.addValue("a", LocalDateTime.now());

        Assert.assertTrue(topValues.isTopKValue("a", 3));
        Assert.assertFalse(topValues.isTopKValue("e", 3));
        Assert.assertEquals(3, topValues.getRecentTimesSize());
    }

    @Test
    public void testIsNotTopValue() {
        TopValuesWithinNDays topValues = new TopValuesWithinNDays(3);
        topValues.addValue("f", LocalDateTime.now().minusDays(5));
        topValues.addValue("f", LocalDateTime.now().minusDays(5));
        topValues.addValue("f", LocalDateTime.now().minusDays(5));
        topValues.addValue("f", LocalDateTime.now().minusDays(5));
        topValues.addValue("e", LocalDateTime.now().minusDays(4));
        topValues.addValue("d", LocalDateTime.now().minusDays(3));
        topValues.addValue("c", LocalDateTime.now().minusDays(2));
        topValues.addValue("b", LocalDateTime.now().minusDays(1));
        topValues.addValue("a", LocalDateTime.now());

        Assert.assertFalse(topValues.isTopKValue("f", 3));
    }

    @Test
    public void testIsTopValue() {
        TopValuesWithinNDays topValues = new TopValuesWithinNDays(6);
        topValues.addValue("f", LocalDateTime.now().minusDays(5));
        topValues.addValue("f", LocalDateTime.now().minusDays(5));
        topValues.addValue("f", LocalDateTime.now().minusDays(5));
        topValues.addValue("f", LocalDateTime.now().minusDays(5));
        topValues.addValue("e", LocalDateTime.now().minusDays(4));
        topValues.addValue("d", LocalDateTime.now().minusDays(3));
        topValues.addValue("c", LocalDateTime.now().minusDays(2));
        topValues.addValue("b", LocalDateTime.now().minusDays(1));
        topValues.addValue("a", LocalDateTime.now());

        Assert.assertTrue(topValues.isTopKValue("f", 1));
    }
}