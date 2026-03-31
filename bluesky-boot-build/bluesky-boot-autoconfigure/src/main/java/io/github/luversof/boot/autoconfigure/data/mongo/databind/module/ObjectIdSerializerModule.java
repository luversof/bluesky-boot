package io.github.luversof.boot.autoconfigure.data.mongo.databind.module;

import io.github.luversof.boot.autoconfigure.data.mongo.databind.ObjectIdSerializer;
import org.bson.types.ObjectId;
import tools.jackson.core.json.PackageVersion;
import tools.jackson.databind.module.SimpleModule;

public class ObjectIdSerializerModule extends SimpleModule {
    private static final long serialVersionUID = 1L;

    public ObjectIdSerializerModule() {
        super("JacksonXmlModule", PackageVersion.VERSION);
        this.addSerializer(ObjectId.class, new ObjectIdSerializer());
    }
}
