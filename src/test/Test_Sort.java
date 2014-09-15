package test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.TreeSet;

import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import core.Abstract_Host;
import core.BestFit_Host;

public class Test_Sort {

	public static void main(String[] args) {

		LinkedList<Abstract_Host> cpu_sorted_list = new LinkedList<>();
		LinkedList<Abstract_Host> mem_sorted_list = new LinkedList<>();

		Comparator<Abstract_Host> cpu_comparator = new Comparator<Abstract_Host>() {

			@Override
			public int compare(Abstract_Host o1, Abstract_Host o2) {
				if (o1.getCpu_utilization() > o2.getCpu_utilization()) {
					return 1;
				} else if (o1.getCpu_utilization() < o2.getCpu_utilization()) {
					return -1;
				} else {
					return 0;
				}
			}

		};

		Comparator<Abstract_Host> mem_comparator = new Comparator<Abstract_Host>() {

			@Override
			public int compare(Abstract_Host o1, Abstract_Host o2) {
				if (o1.getMem_utilization() > o2.getMem_utilization()) {
					return 1;
				} else if (o1.getMem_utilization() < o2.getMem_utilization()) {
					return -1;
				} else {
					return 0;
				}
			}

		};

		UniformRealDistribution distribution = new UniformRealDistribution(0, 1);
		for (int i = 0; i < 100; i++) {
			Double cpu = distribution.sample();
			Double mem = distribution.sample();
			Abstract_Host host = new BestFit_Host();
			host.setCpu_utilization(cpu);
			host.setMem_utilization(mem);
			cpu_sorted_list.add(host);
			mem_sorted_list.add(host);
		}

		Abstract_Host host1 = new BestFit_Host();
		host1.setCpu_utilization(0.45);
		host1.setMem_utilization(0.65);
		cpu_sorted_list.add(host1);
		mem_sorted_list.add(host1);

		Abstract_Host host2 = new BestFit_Host();
		host2.setCpu_utilization(0.45);
		host2.setMem_utilization(0.65);
		cpu_sorted_list.add(host2);
		mem_sorted_list.add(host2);

		Collections.sort(cpu_sorted_list, cpu_comparator);
		Collections.sort(mem_sorted_list, mem_comparator);

		Integer counterInteger = 0;
		System.out.println("CPU:");
		System.out.println(cpu_sorted_list.size());
		for (Abstract_Host host : cpu_sorted_list) {
			counterInteger++;
			System.out.println(counterInteger);
			System.out.println("cpu:" + host.getCpu_utilization() + "-mem:" + host.getMem_utilization());
		}
		System.out.println("MEM:");
		counterInteger = 0;
		for (Abstract_Host host : mem_sorted_list) {
			counterInteger++;
			System.out.println(counterInteger);
			System.out.println("cpu:" + host.getCpu_utilization() + "-mem:" + host.getMem_utilization());
		}

		host1.setCpu_utilization(0.3);
		host1.setMem_utilization(0.7);

		host2.setCpu_utilization(0.3);
		host2.setMem_utilization(0.7);

		Collections.sort(cpu_sorted_list, cpu_comparator);
		Collections.sort(mem_sorted_list, mem_comparator);

		System.out.println("CPU:");
		counterInteger = 0;
		for (Abstract_Host host : cpu_sorted_list) {
			counterInteger++;
			System.out.println(counterInteger);
			System.out.println("cpu:" + host.getCpu_utilization() + "-mem:" + host.getMem_utilization());
		}
		System.out.println("MEM:");
		counterInteger = 0;
		for (Abstract_Host host : mem_sorted_list) {
			counterInteger++;
			System.out.println(counterInteger);
			System.out.println("cpu:" + host.getCpu_utilization() + "-mem:" + host.getMem_utilization());
		}
	}
}
