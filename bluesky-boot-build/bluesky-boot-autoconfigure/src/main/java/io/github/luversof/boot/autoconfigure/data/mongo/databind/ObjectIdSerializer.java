package io.github.luversof.boot.autoconfigure.data.mongo.databind;

import org.bson.types.ObjectId;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

public class ObjectIdSerializer extends ValueSerializer<ObjectId> {

	@Override
	public void serialize(ObjectId value, tools.jackson.core.JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
		gen.writeString(value.toString());
	}

}
