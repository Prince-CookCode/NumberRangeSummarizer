import numberrangesummarizer.NumberRangeSummarizer;

import java.util.*;
import java.util.stream.Collectors;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main implements NumberRangeSummarizer {
	public static void main (String[] args) {

	}

	@Override
	public Collection<Integer> collect(String input) {
		// Check for null input
		Optional<String> stringOptional = Optional.ofNullable(input);
		String stringValue = stringOptional.orElseThrow(()-> new NullPointerException("Input cannot be null"));

		if (stringValue.trim().isEmpty()) {
			throw new IllegalArgumentException("Input cannot be Empty");
		}

		String [] number = input.split(",");
		List<Integer> numbersList = Arrays.stream(number)
				.map(String::trim) // no trailing spaces
				.filter(num -> num.chars().allMatch(Character::isDigit)) // only digits
				.map(Integer::parseInt)
				.sorted().distinct().collect(Collectors.toList()); // sorted, no duplicates

		if (numbersList.isEmpty()) {
			throw new IllegalArgumentException("Input must contain at least number");
		}

		return numbersList ;
	}

	@Override
	public String summarizeCollection(Collection<Integer> input) {

		if (input == null || input.isEmpty() ) {
			return null;
		}

		List<Integer> sortedList = input.stream().collect(Collectors.toList());
		List<String> result = new ArrayList<>();
		int start = sortedList.get(0);
		int end = sortedList.get(0);

		for (int i = 1; i < sortedList.size(); i++) {
            if (sortedList.get(i) != end + 1) {
                result.add(start == end ? String.valueOf(start) : start + "-" + end);
                start = sortedList.get(i);
            }
            end = sortedList.get(i);
        }

		result.add(start == end ? String.valueOf(start) : start + "-" + end);
		return String.join(",", result);
	}
}

