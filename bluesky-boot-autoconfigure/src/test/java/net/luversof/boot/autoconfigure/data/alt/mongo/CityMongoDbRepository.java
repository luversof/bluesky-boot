package net.luversof.boot.autoconfigure.data.alt.mongo;

import net.luversof.boot.autoconfigure.data.mongo.city.City;
import org.springframework.data.repository.Repository;

public interface CityMongoDbRepository extends Repository<City, Long> {

}
