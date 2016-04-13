package com.iliakplv.java;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Java8Test {

    public static void main(String[] args) {

	    List<String> list = Arrays.asList("A", "B", "C");

	    // parallel, lambda
	    list.parallelStream().forEach((s) -> System.out.println(s));

	    // functional interface
	    list.forEach(System.out::print);
	    System.out.println();

	    Converter<String, Integer> converter = Integer::valueOf;
	    System.out.println(converter.convert("42"));

	    Operation<Integer> add = (a, b) -> a + b;
	    System.out.println(add.perform(2, 3));

        // optional
        Optional<Integer> intOpt1 = Optional.of(5);
        Integer int2 = null;
        Optional<Integer> intOpt2 = Optional.ofNullable(int2);
        System.out.println(intOpt1.isPresent());
        System.out.println(intOpt2.isPresent());
    }


	@FunctionalInterface
	interface Converter<F, T> {
		T convert(F from);
	}

	@FunctionalInterface
	interface Operation<T> {
		T perform(T a, T b);
	}


	interface InterfaceWithDefaultImplementation {

		void abstractMethodOne();

		void abstractMethodTwo();

		default void defaultImplMethod() {
			abstractMethodOne();
			abstractMethodTwo();
		}
	}
}
