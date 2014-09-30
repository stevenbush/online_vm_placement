package test;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;

public class test {

	public static void main(String[] args) {
		UniformIntegerDistribution distribution = new UniformIntegerDistribution(0, 1);

		for (int i = 0; i < 20; i++) {
			System.out.println(distribution.sample());
		}
	}

}
