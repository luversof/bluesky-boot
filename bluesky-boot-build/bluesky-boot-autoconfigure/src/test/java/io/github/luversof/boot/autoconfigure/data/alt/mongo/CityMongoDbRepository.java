package io.github.luversof.boot.autoconfigure.data.alt.mongo;

import org.springframework.data.repository.Repository;

import io.github.luversof.boot.autoconfigure.data.mongo.city.City;

public interface CityMongoDbRepository extends Repository<City, Long> {

}
