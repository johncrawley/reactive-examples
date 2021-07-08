package com.jcrawley.reactiveexamples;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jcrawley.reactiveexamples.domain.Person;
import com.jcrawley.reactiveexamples.repository.PersonRepositoryImpl;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class PersonRepositoryImplTest {

	
	PersonRepositoryImpl personRepository;
	
	@BeforeEach
	void setup() {
		personRepository = new PersonRepositoryImpl();
	}
	
	@Test
	void getByIdBlock() {
		Mono <Person> personMono = personRepository.findById(1);
		Person person = personMono.block();
		System.out.println("person: " + person.toString());
	}
	
	
	@Test
	void getByIdSubscribe() {
		Mono<Person> personMono = personRepository.findById(1);

		StepVerifier.create(personMono).expectNextCount(1).verifyComplete();
		StepVerifier.create(personMono).expectNextCount(3).expectError();
		
		personMono.subscribe(person -> {
			System.out.println(person.toString());
		});
	}
	
	@Test
	void getByIdMapFunction() {
		Mono<Person> personMono = personRepository.findById(1);
		
		personMono.map(person -> {
			return person.getFirstName();
			})
		.subscribe( firstName -> {
			System.out.println("from map: " + firstName);
		});
	}

	
	@Test
	void fluxTestBlockFirst() {
		Flux<Person> personFlux = personRepository.findAll();
		Person person = personFlux.blockFirst();
		System.out.println("flux blockFirst(): "  + person.toString());
		
	}
	

	
	@Test
	void fluxTestSubscribe() {
		Flux<Person> personFlux = personRepository.findAll();
		
		StepVerifier.create(personFlux).expectNextCount(4).verifyComplete();
		
		personFlux.subscribe( person -> {
			System.out.println("person flux item : " + person.toString());
		});
	}
	
	
	@Test
	void fluxToListMono() {
		Flux<Person> personFlux = personRepository.findAll();
		Mono<List<Person>> personListMono = personFlux.collectList();
		
		personListMono.subscribe(list -> {
			list.forEach(person -> {
				System.out.println("person item from Mono<List> : " + person.toString());
			});
		});
	}
	
	
	
	//Filtering objects out of a stream of Flux objects
	@Test
	void findPersonById(){
		Flux<Person> personFlux = personRepository.findAll();
		
		final Integer id = 3;
		
		Mono<Person> personMono = personFlux.filter(
				person ->  person.getId() == id).next();
		
		personMono.subscribe( person -> {
			System.out.println("findPersonById: " + id + " : " + person.toString());
		});
	}

	
	/*
	 *  .single() will throw an exception if 0, or many results are returned
	 * 
	 *  we use the doOnError() to catch this exception
	 *  and we can use the onErrorReturn to return an empty object
	 */
	@Test
	void findPersonByIdNotFoundWithException(){
		Flux<Person> personFlux = personRepository.findAll();
		
		final Integer id = -8;
		
		Mono<Person> personMono = personFlux.filter(
				person ->  person.getId() == id).single();
		
		personMono.doOnError(throwable -> {
			System.out.println("Problem, id not found!");
		}).onErrorReturn(Person.builder().id(id).build())
				
		.subscribe( person -> {
			System.out.println("findPersonById: " + id + " : " + person.toString());
		});
	}
	
	
	//assignment 1
	@Test
	public void findPersonByExactId() {
		int id1 = 1;
		Mono<Person> p1 = personRepository.findById(id1);
			
		p1.subscribe( person ->{ 
			assertEquals(id1, person.getId());
		});
	
		int id2 = 4;
		Mono<Person> p2 = personRepository.findById(id2);
		
		p2.subscribe( person ->{ 
			assertEquals(id2, person.getId());
		});
		
	}
	
}
