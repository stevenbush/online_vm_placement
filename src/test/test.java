package test;

import java.util.ArrayList;

public class test {

	public static ArrayList<Integer> return_list() {
		ArrayList<Integer> integers = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			integers.add(i);
		}

		return integers;
	}

	public static void main(String[] args) {
		ArrayList<Integer> test_list = null;
		test_list = return_list();
		for (int i = 0; i < test_list.size(); i++) {
			System.out.println(test_list.get(i));
		}
	}

}
