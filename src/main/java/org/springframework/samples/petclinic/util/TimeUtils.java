/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.samples.petclinic.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for time-related operations.
 */
public class TimeUtils {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	private static final ZoneId CLINIC_ZONE_ID = ZoneId.of("America/Los_Angeles"); // Example clinic timezone

	/**
	 * Calculates the duration between two LocalDateTime objects in minutes.
	 * If the end time is null, it means the visit is in progress, and the duration is calculated up to the current time.
	 *
	 * @param start The start time (check-in time).
	 * @param end The end time (check-out time), or null if the visit is ongoing.
	 * @return The duration in minutes, rounded to the nearest minute.
	 */
	public static long calculateDurationInMinutes(LocalDateTime start, LocalDateTime end) {
		if (start == null) {
			return 0;
		}
		LocalDateTime effectiveEnd = (end == null) ? LocalDateTime.now() : end;
		Duration duration = Duration.between(start, effectiveEnd);
		return duration.toMinutes();
	}

	/**
	 * Converts a UTC LocalDateTime to the clinic's local timezone.
	 *
	 * @param utcDateTime The UTC timestamp.
	 * @return The timestamp converted to the clinic's local timezone.
	 */
	public static LocalDateTime convertUtcToLocal(LocalDateTime utcDateTime) {
		if (utcDateTime == null) {
			return null;
		}
		ZonedDateTime utcZonedDateTime = utcDateTime.atZone(ZoneId.of("UTC"));
		return utcZonedDateTime.withZoneSameInstant(CLINIC_ZONE_ID).toLocalDateTime();
	}

	/**
	 * Formats a LocalDateTime object using the predefined DATE_TIME_FORMATTER.
	 *
	 * @param dateTime The LocalDateTime to format.
	 * @return The formatted date-time string.
	 */
	public static String formatLocalDateTime(LocalDateTime dateTime) {
		if (dateTime == null) {
			return "";
		}
		return dateTime.format(DATE_TIME_FORMATTER);
	}

}
