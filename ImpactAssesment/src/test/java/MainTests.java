import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class MainTests {

	private final Collection<Integer> expected = Arrays.asList(1,2,3);
	private Collection<Integer> actual ;
	private Main main;

	@BeforeEach
	public void setup(){
		actual = new ArrayList<>();
		main = new Main();
	}

	@Test
	void testCollect_WithSpaces(){
		String  input = " 1 , 2 , 3";
		actual = main.collect(input);

		assertEquals(expected, actual);
		assertIterableEquals(expected , actual);
		assertDoesNotThrow(() -> main.collect(input));
	}

	@Test
	void testCollect_WithoutSpaces(){

		String input = "1,2,3";
		actual = main.collect(input);

		assertEquals(expected, actual);
		assertIterableEquals(actual, expected);
		assertDoesNotThrow(() -> main.collect(input));
	}

	/**
	 * When given an input mains and non numerics, filter and return a list with numerics only
	 */
	@Test
	void testCollect_hasNonNumeric(){

		String inputString = "1,abc,2,3,b,5,6,8" ;

		Collection<Integer> actual  = main.collect(inputString);

		Collection<Integer> expected = Arrays.asList(1,2,3,5,6,8);


		assertEquals(actual, expected);
		assertDoesNotThrow( ()-> main.collect(inputString),"Should throw an exception");
		assertIterableEquals(actual , expected, "Iterables should be deeply equal");

		/*
		 * check  if they refer to same object in memory.
		 */
		assertNotSame(actual, expected);

	}

	@Test
	public void testCollect_EmptyInput_ThrowsException(){
		try {
			main.collect("");
			fail("Expected IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			assertEquals("Input cannot be Empty", e.getMessage());
		}
		assertThrows(IllegalArgumentException.class ,()-> main.collect(" "));
	}

	@Test
	public void testCollect_Null_Input_ThrowException(){
		// Test null input
		try {
			main.collect(null);
			fail("Expected NullPointerException");
		} catch (NullPointerException e) {
			assertEquals("Input cannot be null", e.getMessage());
		}
		assertThrows(NullPointerException.class ,()-> main.collect(null));
	}

	/**
	 * Changed these Test to use the collect() method as a helper method
	 */
	@Test
	public void testSummarizeCollectionEmpty() {

		Collection<Integer> input = new ArrayList<>();

		assertNull(main.summarizeCollection(input));

		assertIterableEquals(input, new ArrayList<>());
		assertDoesNotThrow(()->main.summarizeCollection(input));
	}

	/**
	 * given  a non-empty input like a,b,c,d,e
	 */
	@Test
	void testSummarizedCollectionInputNoNumerics(){

		String inputString = "a, b, c, d, e, f";

		assertThrows(IllegalArgumentException.class , ()-> main.collect(inputString));
	}

	/**
	 * Test with a single main in the input.
	 */
	@Test
	public void testSummarizeCollectionSingleNumber() {


		Collection<Integer> input = main.collect("1");
		String expected = "1";
		String actual = main.summarizeCollection(input);

		assertNotNull(actual);
		assertEquals(expected, actual);
		assertDoesNotThrow( () -> main.collect("1"));
	}

	@Test
	public void testSummarizeCollectionNoDuplicates() {
		Collection<Integer> input = main.collect("1, 2, 3, 4, 5");
		String expected = "1-5";
		String actual = main.summarizeCollection(input);

		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testSummarizeCollectionMultipleAdjacentDuplicates() {
		Collection<Integer> input = main.collect("1, 2, 2, 3, 3, 3, 4, 5, 5, 6, 6, 6,9,10,11,13");
		String  expected = "1-6,9-11,13";
		String  actual = main.summarizeCollection(input);

		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testSummarizeCollectionSimpleInput() {
		Collection<Integer> input = main.collect("1,3,6,7,8,12,13,14,15,21,22,23,24,31");
		String expected = "1,3,6-8,12-15,21-24,31";
		String actual = main.summarizeCollection(input);

		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	@Test
	public void testSummarizeCollectionUnorderedWithDuplicates() {
		Collection<Integer> input = main.collect("11,11,10,9,9,1,1, 2,2,2, 13,14,15, 4, 5,6,6,19,19");
		String expected = "1-2,4-6,9-11,13-15,19";
		String actual = main.summarizeCollection(input);

		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	@Test
	void testSummarizeCollectionUnorderedWithEmptyString(){

		assertNull(main.summarizeCollection(Collections.emptyList()));
		assertNull(main.summarizeCollection(null));

	}


	/**
	 *  Parameterized Tests
	 * @param inputString csv string of mains
	 * @param expected a list of Integers
	 */
	@ParameterizedTest
	@MethodSource("provideCollectionForMultiple")
	void testMultipleCollections(String inputString , Collection<Integer> expected){
		assertEquals(expected, main.collect(inputString) );
	}
	private static Stream<Arguments> provideCollectionForMultiple(){
		return Stream.of(
				Arguments.of("1" , Collections.singletonList(1)),
				Arguments.of("1 , 2 , 3" , Arrays.asList(1,2,3)),
				Arguments.of("1,abc,2,3,b,5,6,8", Arrays.asList(1,2,3,5,6,8))
		);
	}


	@ParameterizedTest
	@CsvSource(value = {"1,2,3:1-3" , "1,abc,2,3,b,5,6,8:1-3,5-6,8"} ,  delimiter = ':')
	void testSummarizeCollectionCSSource(String input, String expected){

		String actual = main.summarizeCollection(main.collect(input));

		assertEquals(expected , actual);
	}

}
