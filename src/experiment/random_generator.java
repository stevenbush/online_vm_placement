package experiment;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

public class random_generator {

	public static void main(String[] args) {
		UniformRealDistribution distribution = new UniformRealDistribution(0, 1);
		Double seed_value = 0.0;

		for (int i = 0; i < 100; i++) {
			seed_value = distribution.sample();
			System.out.println(seed_value);
			Double upper_bound = Math.min(1 / seed_value, 2);
			Double lower_bound = 0.5;
			UniformRealDistribution scalling_distribution = new UniformRealDistribution(lower_bound,
					upper_bound);
			System.out.println("upper_bound: " + upper_bound + "-" + "lower_bound: " + lower_bound);
			Double CPU = seed_value;
			Double MEM = seed_value * scalling_distribution.sample();
			System.out.println("CPU: " + CPU + "-" + "MEM: " + MEM);
		}

	}

}
