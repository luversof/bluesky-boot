
package net.luversof.boot.autoconfigure.data.mongo.databind.module;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.databind.module.SimpleModule;

import net.luversof.boot.autoconfigure.data.mongo.databind.ObjectIdSerializer;

public class ObjectIdSerializerModule extends SimpleModule {
	private static final long serialVersionUID = 1L;

	public ObjectIdSerializerModule() {
		super("JacksonXmlModule", PackageVersion.VERSION);
		this.addSerializer(ObjectId.class, new ObjectIdSerializer());
	}
}
