package thebitcrafter.autoreminder.domain.model;

import lombok.*;

import java.time.*;
import java.time.YearMonth;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(of = {"id", "type", "title", "recurrence", "active"})
public class Event {
    @EqualsAndHashCode.Include
    private UUID id;
    @NonNull private EventType type;
    @NonNull private String title;
    private String description;
    @NonNull private LocalDate eventDate; // base date (e.g. birthday original date)
    @NonNull private LocalTime time; // time of day to trigger
    @NonNull private ZoneId zone; // time zone
    @NonNull private Recurrence recurrence; // recurrence rule
    private boolean active; // active flag

    public Optional<ZonedDateTime> nextTriggerAfter(ZonedDateTime now) {
        if (!active) return Optional.empty();
        now = now.withZoneSameInstant(zone);

        switch (recurrence) {
            case YEARLY:
                return Optional.of(nextYearly(now));
            case MONTHLY:
                return Optional.of(nextMonthly(now));
            case WEEKLY:
                return Optional.of(nextWeekly(now));
            case DAILY:
                return Optional.of(nextDaily(now));
            case NONE:
            default:
                ZonedDateTime oneOff = ZonedDateTime.of(eventDate, time, zone);
                return oneOff.isAfter(now) ? Optional.of(oneOff) : Optional.empty();
        }
    }

    private ZonedDateTime nextYearly(ZonedDateTime now) {
        int year = now.getYear();
        int day = Math.min(eventDate.getDayOfMonth(), YearMonth.of(year, eventDate.getMonth()).lengthOfMonth());
        LocalDate candidate = LocalDate.of(year, eventDate.getMonth(), day);
        ZonedDateTime dt = ZonedDateTime.of(candidate, time, zone);
        if (!dt.isAfter(now)) {
            int nextYear = year + 1;
            int nextDay = Math.min(eventDate.getDayOfMonth(), YearMonth.of(nextYear, eventDate.getMonth()).lengthOfMonth());
            dt = ZonedDateTime.of(LocalDate.of(nextYear, eventDate.getMonth(), nextDay), time, zone);
        }
        return dt;
    }

    private ZonedDateTime nextMonthly(ZonedDateTime now) {
        YearMonth ym = YearMonth.of(now.getYear(), now.getMonth());
        int day = Math.min(eventDate.getDayOfMonth(), ym.lengthOfMonth());
        ZonedDateTime dt = ZonedDateTime.of(LocalDate.of(ym.getYear(), ym.getMonth(), day), time, zone);
        if (!dt.isAfter(now)) {
            YearMonth next = ym.plusMonths(1);
            int d2 = Math.min(eventDate.getDayOfMonth(), next.lengthOfMonth());
            dt = ZonedDateTime.of(LocalDate.of(next.getYear(), next.getMonth(), d2), time, zone);
        }
        return dt;
    }

    private ZonedDateTime nextWeekly(ZonedDateTime now) {
        DayOfWeek target = eventDate.getDayOfWeek();
        ZonedDateTime base = now.withZoneSameInstant(zone);
        int diff = (target.getValue() - base.getDayOfWeek().getValue() + 7) % 7;
        ZonedDateTime dt = ZonedDateTime.of(base.toLocalDate().plusDays(diff), time, zone);
        if (!dt.isAfter(base)) {
            dt = dt.plusWeeks(1);
        }
        return dt;
    }

    private ZonedDateTime nextDaily(ZonedDateTime now) {
        ZonedDateTime dt = ZonedDateTime.of(now.toLocalDate(), time, zone);
        if (!dt.isAfter(now)) {
            dt = dt.plusDays(1);
        }
        return dt;
    }
}