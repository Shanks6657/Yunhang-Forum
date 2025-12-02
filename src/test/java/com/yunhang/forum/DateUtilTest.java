package com.yunhang.forum;

import com.yunhang.forum.util.DateUtil;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilTest {

    @Test
    void nowShouldReturnStandardFormattedString() {
        String nowStr = DateUtil.now();
        assertNotNull(nowStr);
        assertTrue(nowStr.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    void formatAndParseRoundTrip() {
        var time = LocalDateTime.of(2025, 12, 2, 19, 30, 15);
        var s = DateUtil.format(time);
        assertEquals("2025-12-02 19:30:15", s);
        var parsed = DateUtil.parse(s);
        assertEquals(time, parsed);
    }

    @Test
    void relativeTimeMinutesHoursAndYesterday() {
        var justNow = LocalDateTime.now().minusSeconds(30);
        assertEquals("刚刚", DateUtil.getRelativeTime(justNow));

        var tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        assertEquals("10 分钟前", DateUtil.getRelativeTime(tenMinutesAgo));

        var twoHoursAgo = LocalDateTime.now().minusHours(2);
        assertEquals("2 小时前", DateUtil.getRelativeTime(twoHoursAgo));

        var yesterdayNoon = LocalDate.now().minusDays(1).atTime(12, 0);
        assertTrue(DateUtil.getRelativeTime(yesterdayNoon).startsWith("昨天 "));

        var threeDaysAgo = LocalDate.now().minusDays(3).atTime(12, 0);
        assertTrue(DateUtil.getRelativeTime(threeDaysAgo).matches("\\d{4}-\\d{2}-\\d{2}"));
    }
}

