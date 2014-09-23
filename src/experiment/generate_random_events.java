package experiment;

import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.math3.distribution.UniformRealDistribution;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class generate_random_events {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("No input path and resultpath specified.");
			System.out.println("please enter \"generate_worst_events inputpath resultpath\"");
			return;
		}

		File input_path = new File(args[0]);
		File result_path = new File(args[1]);

		System.out.println("inputpath: " + input_path.getAbsolutePath());
		System.out.println("resultpath: " + result_path.getAbsolutePath());

		FileFilter fileFilter = new PrefixFileFilter("events-info-");
		File file_list[] = input_path.listFiles(fileFilter);

		UniformRealDistribution tiny_seed_distribution = new UniformRealDistribution(0, 0.25);
		UniformRealDistribution normal_seed_distribution = new UniformRealDistribution(0.25, 1.0);

		Double seed_value = 0.0;

		for (int i = 0; i < file_list.length; i++) {
			System.out.println(file_list[i]);
			try {
				CSVReader reader = new CSVReader(new FileReader(file_list[i]));
				String[] file_path = file_list[i].getAbsolutePath().split("/");
				String file_name = file_path[file_path.length - 1];
				CSVWriter writer = new CSVWriter(new FileWriter(result_path + "/" + "random-" + file_name),
						CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER);
				List<String[]> items_list = reader.readAll();
				Integer counter = 0;
				Integer list_size = items_list.size();
				Double tiny_size = 0.0;
				// Double normal_size = 0.0;
				// Double big_size = 0.0;
				System.out.println("String generating synthetic events...");
				for (String[] item_line : items_list) {
					if (!item_line[3].equals("4")) {
						System.out.print(file_list[i] + ": ");
						counter = counter + 1;
						System.out.println((counter * 100) / list_size + "%");

						if (tiny_size <= 0.5) {
							Double CPU = tiny_seed_distribution.sample();
							Double MEM = tiny_seed_distribution.sample();
							item_line[6] = String.valueOf(CPU);
							item_line[7] = String.valueOf(MEM);
							tiny_size = tiny_size + Math.max(CPU, MEM);
						} else {
							Double CPU = normal_seed_distribution.sample();
							Double MEM = normal_seed_distribution.sample();
							item_line[6] = String.valueOf(CPU);
							item_line[7] = String.valueOf(MEM);
							tiny_size = 0.0;
						}

					}
					writer.writeNext(item_line);
				}

				writer.flush();
				writer.close();
				reader.close();
				System.out.println("Generating finish...");

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
