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

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations.
 *
 * @author Maciej Szalزد
 */
public class DateTimeUtils {

	// Assuming clinic's local timezone is UTC for simplicity in this example.
	// In a real application, this would be fetched from configuration or user profile.
	private static final ZoneId CLINIC_ZONE_ID = ZoneId.of("UTC");

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/**
	 * Converts an OffsetDateTime from UTC to the clinic's local timezone and formats it.
	 *
	 * @param dateTime The OffsetDateTime in UTC.
	 * @return Formatted date-time string in the clinic's local timezone, or an empty string if input is null.
	 */
	public static String formatUtcToLocal(OffsetDateTime dateTime) {
		if (dateTime == null) {
			return "";
		}
		return dateTime.atZoneSameInstant(CLINIC_ZONE_ID).format(DATE_TIME_FORMATTER);
	}

	/**
	 * Converts an OffsetDateTime from UTC to the clinic's local timezone and formats it.
	 * This method is specifically for check-in/check-out times.
	 *
	 * @param dateTime The OffsetDateTime in UTC.
	 * @return Formatted date-time string in the clinic's local timezone, or an empty string if input is null.
	 */
	public static String formatUtcToLocalForVisit(OffsetDateTime dateTime) {
		return formatUtcToLocal(dateTime);
	}

	/**
	 * Converts a LocalDateTime to an OffsetDateTime in UTC.
	 *
	 * @param dateTime The LocalDateTime to convert.
	 * @return OffsetDateTime in UTC.
	 */
	public static OffsetDateTime convertLocalDateTimeToUtcOffsetDateTime(LocalDateTime dateTime) {
		if (dateTime == null) {
			return null;
		}
		return dateTime.atOffset(ZoneOffset.UTC);
	}

	/**
	 * Converts a LocalDateTime to an OffsetDateTime in UTC.
	 * This method is specifically for check-in/check-out times.
	 *
	 * @param dateTime The LocalDateTime to convert.
	 * @return OffsetDateTime in UTC.
	 */
	public static OffsetDateTime convertLocalDateTimeToUtcOffsetDateTimeForVisit(LocalDateTime dateTime) {
		return convertLocalDateTimeToUtcOffsetDateTime(dateTime);
	}

}
