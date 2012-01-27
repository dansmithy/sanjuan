package com.github.dansmithy.sanjuan.rest.serialize;

import java.io.IOException;
import java.util.Date;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.joda.time.DateTimeZone;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.ISODateTimeFormat;

public class Iso8601CustomDateSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

		String isoTime = ISODateTimeFormat.dateTime().withChronology(ISOChronology.getInstance()).withZone(DateTimeZone.UTC).print(value.getTime());
		jgen.writeString(isoTime);

	}

}
