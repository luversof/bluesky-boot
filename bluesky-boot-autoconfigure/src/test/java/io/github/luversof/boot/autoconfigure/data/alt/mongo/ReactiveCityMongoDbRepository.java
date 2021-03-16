package io.github.luversof.boot.autoconfigure.data.alt.mongo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import io.github.luversof.boot.autoconfigure.data.mongo.city.City;

public interface ReactiveCityMongoDbRepository extends ReactiveCrudRepository<City, Long> {

}
