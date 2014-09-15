package experiment;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class test {

	public static void main(String[] args) {
		try {
			CSVReader reader = new CSVReader(
					new FileReader(
							"/Users/jiyuanshi/Downloads/googleclusterdata/jobs_information/events-info-transformed_part-00247-of-00500.csv"));
			CSVWriter writer = new CSVWriter(new FileWriter("yourfile.csv"), CSVWriter.DEFAULT_SEPARATOR,
					CSVWriter.NO_QUOTE_CHARACTER);
			List<String[]> items_list = reader.readAll();
			Integer counter = 0;
			Integer list_size = items_list.size();
			for (String[] item_line : items_list) {
				counter = counter + 1;
				System.out.println((counter * 100) / list_size + "%");

				for (int i = 0; i < item_line.length; i++) {
					System.out.print(item_line[i] + ',');
				}
				System.out.println();

				//writer.writeNext(item_line);
			}
			writer.flush();
			writer.close();
			reader.close();
			System.out.println("Finished...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
