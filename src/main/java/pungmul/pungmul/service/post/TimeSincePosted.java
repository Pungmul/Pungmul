package pungmul.pungmul.service.post;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

@Component
public class TimeSincePosted {

    public String getTimeSincePostedText(LocalDateTime postedTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(postedTime, now);
        long minutes = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else if (days < 7) {
            return days + "일 전";
        } else {
            // 7일 이상이면 'M:d' 형식으로 날짜만 표시 (앞에 0 제거)
            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .appendValue(ChronoField.MONTH_OF_YEAR)   // 월에서 앞의 0 제거
                    .appendLiteral("월 ")                       // ':' 구분자
                    .appendValue(ChronoField.DAY_OF_MONTH)    // 일에서 앞의 0 제거
                    .appendLiteral("일")                       // ':' 구분자
                    .toFormatter();
            return postedTime.format(formatter);
        }
    }
}