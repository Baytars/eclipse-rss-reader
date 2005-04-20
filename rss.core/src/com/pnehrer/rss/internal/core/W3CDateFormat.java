/*
 * Created on Nov 12, 2004
 * Version $Id$
 */
package com.pnehrer.rss.internal.core;

import java.io.IOException;
import java.io.StringReader;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * <p>A date formatter that implements the 
 * <a href="http://www.w3.org/TR/NOTE-datetime">W3C-style date/time format</a>.
 * The formats are as follows.
 * Exactly the components shown here must be present, with exactly this
 * punctuation.
 * Note that the "T" appears literally in
 * the string, to indicate the beginning of the time element, as specified in
 * ISO 8601.
 * </p>
 * 
 * <pre>
 *    Year:
 *       YYYY (eg 1997)
 *    Year and month:
 *       YYYY-MM (eg 1997-07)
 *    Complete date:
 *       YYYY-MM-DD (eg 1997-07-16)
 *    Complete date plus hours and minutes:
 *       YYYY-MM-DDThh:mmTZD (eg 1997-07-16T19:20+01:00)
 *    Complete date plus hours, minutes and seconds:
 *       YYYY-MM-DDThh:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
 *    Complete date plus hours, minutes, seconds and a decimal fraction of a
 * second
 *       YYYY-MM-DDThh:mm:ss.sTZD (eg 1997-07-16T19:20:30.45+01:00)
 * </pre>
 * 
 * <p>where:</p>
 * 
 * <pre>
 *      YYYY = four-digit year
 *      MM   = two-digit month (01=January, etc.)
 *      DD   = two-digit day of month (01 through 31)
 *      hh   = two digits of hour (00 through 23) (am/pm NOT allowed)
 *      mm   = two digits of minute (00 through 59)
 *      ss   = two digits of second (00 through 59)
 *      s    = one or more digits representing a decimal fraction of a second
 *      TZD  = time zone designator (Z or +hh:mm or -hh:mm)
 * </pre>
 * 
 * <p>This implementation will handle up to three digits in the decimal
 * fraction of a second; it'll ignore the rest, if there are more. When
 * formatting, the trailing zeros are truncated (e.g., 12.510 will be
 * formatted as 12.51).</p>
 * 
 * <p>This implementation supports two ways of handling time zone offsets:</p>
 * 
 * <ol>
 * <li>Times are expressed in UTC (Coordinated Universal Time), with a
 *     special UTC designator ("Z").
 * <li>Times are expressed in local time, together with a time zone offset 
 *     in hours and minutes.  A time zone offset of "+hh:mm" indicates that
 *     the date/time uses a local time zone which is "hh" hours and "mm" 
 *     minutes ahead of UTC.  A time zone offset of "-hh:mm" indicates that
 *     the date/time uses a local time zone which is "hh" hours and "mm" 
 *     minutes behind UTC.
 * </ol>
 * 
 * <h2>Examples</h2>
 * 
 * <p><samp>1994-11-05T08:15:30-05:00</samp> corresponds to November 5, 1994,
 * 8:15:30 am, US Eastern Standard Time.</p>
 * 
 * <p><samp>1994-11-05T13:15:30Z</samp> corresponds to the same instant.</p>
 * 
 * @author <a href="mailto:pnehrer@freeshell.org">Peter Nehrer</a>
 */
public class W3CDateFormat extends DateFormat {

	private static final int MILLIS_IN_HOUR = 60 * 60 * 1000;

	/**
	 * Creates an instance with the default calendar.
	 */
	public W3CDateFormat() {
		calendar = Calendar.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.text.DateFormat#format(java.util.Date, java.lang.StringBuffer,
	 *      java.text.FieldPosition)
	 */
	public StringBuffer format(Date date, StringBuffer buf, FieldPosition pos) {
		TimeZone tz = TimeZone.getDefault();
		StringBuffer tzOffset;
		if (tz.equals(TimeZone.getTimeZone("GMT")))
			tzOffset = new StringBuffer("Z");
		else {
	        int offset = tz.getRawOffset();
			if (offset > 0)
				tzOffset = new StringBuffer("+");
			else {
				tzOffset = new StringBuffer("-");
				offset = Math.abs(offset);
			}
	
	        tzOffset.append(MessageFormat.format(
				"{0,number,00}:{1,number,00}", 
				new Object[] {
					new Integer(offset / MILLIS_IN_HOUR),
					new Integer(offset % MILLIS_IN_HOUR)}));
					
			StringBuffer tzName = new StringBuffer("GMT");
			tz = TimeZone.getTimeZone(tzName.append(tzOffset).toString());
		}
		
		setTimeZone(tz);
        calendar.setTime(date);
		if (calendar.isSet(Calendar.YEAR))
			format(buf, pos, DateFormat.YEAR_FIELD,
					calendar.get(Calendar.YEAR), 4);
		else
			return buf;

		if (calendar.isSet(Calendar.MONTH)) {
			buf.append('-');
			format(buf, pos, DateFormat.MONTH_FIELD, calendar
					.get(Calendar.MONTH) + 1, 2);
		} else
			return buf;

		if (calendar.isSet(Calendar.DATE)) {
			buf.append('-');
			format(buf, pos, DateFormat.DATE_FIELD,
					calendar.get(Calendar.DATE), 2);
		} else
			return buf;

		if (!calendar.isSet(Calendar.HOUR_OF_DAY))
			return buf;

		buf.append('T');
		format(buf, pos, DateFormat.HOUR_OF_DAY0_FIELD, calendar
				.get(Calendar.HOUR_OF_DAY), 2);
		buf.append(':');
		format(buf, pos, DateFormat.MINUTE_FIELD,
				calendar.get(Calendar.MINUTE), 2);

		if (calendar.isSet(Calendar.SECOND)) {
			buf.append(':');
			format(buf, pos, DateFormat.SECOND_FIELD, calendar
					.get(Calendar.SECOND), 2);
		}

		if (calendar.isSet(Calendar.MILLISECOND)) {
			buf.append('.');
			int val = calendar.get(Calendar.MILLISECOND);
			int r;
			while (val > 0 && (r = val % 10) == 0)
				val = val / 10;

			if (pos.getField() == Calendar.MILLISECOND)
				pos.setBeginIndex(buf.length());

			buf.append(val);
			if (pos.getField() == Calendar.MILLISECOND)
				pos.setEndIndex(buf.length());
		}

		if (pos.getField() == DateFormat.TIMEZONE_FIELD)
			pos.setBeginIndex(buf.length());

		buf.append(tzOffset);
		if (pos.getField() == DateFormat.TIMEZONE_FIELD)
			pos.setEndIndex(buf.length());

		return buf;
	}

	private void format(StringBuffer buf, FieldPosition pos, int field,
			int val, int c) {
		StringBuffer format = new StringBuffer("{0,number,");
		char[] zeros = new char[c];
		Arrays.fill(zeros, '0');
		format.append(zeros);
		format.append('}');
		if (pos.getField() == field)
			pos.setBeginIndex(buf.length());

		buf.append(MessageFormat.format(format.toString(),
				new Object[] { new Integer(val) }));

		if (pos.getField() == field)
			pos.setEndIndex(buf.length());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.text.DateFormat#parse(java.lang.String,
	 *      java.text.ParsePosition)
	 */
	public Date parse(String str, ParsePosition pos) {
		Date result = null;
		try {
			Integer[] buf = new Integer[7];
			StringReader r = new StringReader(str.substring(pos.getIndex()));

			pos.setErrorIndex(pos.getIndex());
			buf[0] = parseNumber(r, 4);
			if (buf[0] == null)
				return null;

			pos.setErrorIndex(pos.getErrorIndex() + 4);
			int c = r.read();
			if (c != '-')
				return result = makeDate(buf);

			pos.setErrorIndex(pos.getErrorIndex() + 1);
			buf[1] = parseNumber(r, 2);
			if (buf[1] == null)
				return null;

			pos.setErrorIndex(pos.getErrorIndex() + 2);
			c = r.read();
			if (c != '-')
				return result = makeDate(buf);

			pos.setErrorIndex(pos.getErrorIndex() + 1);
			buf[2] = parseNumber(r, 2);
			if (buf[2] == null)
				return null;

			pos.setErrorIndex(pos.getErrorIndex() + 2);
			c = r.read();
			if (c != 'T')
				return result = makeDate(buf);

			pos.setErrorIndex(pos.getErrorIndex() + 1);
			buf[3] = parseNumber(r, 2);
			if (buf[3] == null)
				return null;

			pos.setErrorIndex(pos.getErrorIndex() + 2);
			if (r.read() != ':')
				return null;

			pos.setErrorIndex(pos.getErrorIndex() + 1);
			buf[4] = parseNumber(r, 2);
			if (buf[4] == null)
				return null;

			pos.setErrorIndex(pos.getErrorIndex() + 2);
			c = r.read();
			if (c == -1)
				return null;
			else if (c == ':') {
				pos.setErrorIndex(pos.getErrorIndex() + 1);
				buf[5] = parseNumber(r, 2);
				if (buf[5] == null)
					return null;

				pos.setErrorIndex(pos.getErrorIndex() + 2);
				c = r.read();
				if (c == '.') {
					pos.setErrorIndex(pos.getErrorIndex() + 1);
					int i = parseMillis(r, buf, 6);
					if (buf[6] == null)
						return null;

					pos.setErrorIndex(pos.getErrorIndex() + i);
					c = r.read();
				}
			}

			TimeZone GMT = TimeZone.getTimeZone("GMT");
			TimeZone tz;
			switch (c) {
			case 'Z':
				tz = GMT;
				pos.setErrorIndex(pos.getErrorIndex() + 1);
				break;

			case '+':
			case '-':
				pos.setErrorIndex(pos.getErrorIndex() + 1);
				char[] tzOffsetHi = parseDigits(r, 2);
				if (tzOffsetHi == null)
					return null;

				pos.setErrorIndex(pos.getErrorIndex() + 2);
				if (r.read() != ':')
					return null;

				pos.setErrorIndex(pos.getErrorIndex() + 1);
				char[] tzOffsetLo = parseDigits(r, 2);
				if (tzOffsetLo == null)
					return null;

				StringBuffer tzBuf = new StringBuffer("GMT");
				tzBuf.append((char) c);
				tzBuf.append(tzOffsetHi);
				tzBuf.append(':');
				tzBuf.append(tzOffsetLo);
				tz = TimeZone.getTimeZone(tzBuf.toString());
				if (tz == null)
					return null;

				pos.setErrorIndex(pos.getErrorIndex() + 2);
				break;

			default:
				return null;
			}

			setTimeZone(tz);
			try {
				return result = makeDate(buf);
			} catch (IllegalArgumentException e) {
				return null;
			}
		} catch (IOException e) {
			return null;
		} finally {
			if (result != null) {
				pos.setIndex(pos.getErrorIndex());
				pos.setErrorIndex(-1);
			}
		}
	}

	private Date makeDate(Integer[] buf) {
		calendar.clear();
		calendar.set(Calendar.YEAR, buf[0].intValue());
		if (buf[1] != null) {
			calendar.set(Calendar.MONTH, buf[1].intValue() - 1);
			if (buf[2] != null) {
				calendar.set(Calendar.DATE, buf[2].intValue());
				if (buf[3] != null) {
					calendar.set(Calendar.HOUR_OF_DAY, buf[3].intValue());
					calendar.set(Calendar.MINUTE, buf[4].intValue());
					if (buf[5] != null) {
						calendar.set(Calendar.SECOND, buf[5].intValue());
						if (buf[6] != null)
							calendar.set(Calendar.MILLISECOND, buf[6]
									.intValue());
					}
				}
			}
		}

		return calendar.getTime();
	}

	private char[] parseDigits(StringReader r, int c) throws IOException {
		char[] buf = new char[c];
		if (r.read(buf) != c)
			return null;

		for (int i = 0; i < c; ++i)
			if (!Character.isDigit(buf[i]))
				return null;

		return buf;
	}

	private Integer parseNumber(StringReader r, int c) throws IOException {
		char[] buf = parseDigits(r, c);
		return buf == null ? null : new Integer(String.valueOf(buf));
	}

	private int parseMillis(StringReader r, Integer[] result, int index)
			throws IOException {
		char[] buf = new char[3];
		Arrays.fill(buf, '0');
		int i = 0;
		while (true) {
			r.mark(1);
			int c = r.read();
			if (Character.isDigit((char) c)) {
				if (i < 3)
					buf[i] = (char) c;

				++i;
			} else
				break;
		}

		if (i > 0)
			result[index] = new Integer(String.valueOf(buf));

		r.reset();
		return i;
	}
}
