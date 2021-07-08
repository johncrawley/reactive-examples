package com.jcrawley.reactiveexamples.repository;

import com.jcrawley.reactiveexamples.domain.Person;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class PersonRepositoryImpl implements PersonRepository {

	Person joe = new Person(1, "Joe", "Smith");
	Person mary = new Person(2, "Mary", "Russel");
	Person mark = new Person(3, "Mark", "Kelly");
	Person kate = new Person(4, "Kate", "Thompson");
	
	@Override
	public Mono<Person> findById(Integer id) {
		Mono<Person> personMono = findAll().filter( p -> p.getId() == id).single();
		return personMono;
	}

	
	@Override
	public Flux<Person> findAll() {
		return Flux.just(joe, mary, mark, kate);
	}

}
