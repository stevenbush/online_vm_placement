/**
 * This class is used to run simulation. Specifically this class read the workload file and extract every event information, 
 * then it call the corresponding event processing class to process every event.
 * 
 * @author jiyuanshi
 * @since 2013-12-12
 * @version v1.0
 */
package test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFilter;

import au.com.bytecode.opencsv.CSVReader;

public class Test_Simulator {

	private static final Object[] Double = null;
	private static final boolean Ture = false;

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("No input path and resultpath specified.");
			System.out.println("please enter \"simulator inputpath resultpath\"");
			return;
		}

		File input_path = new File(args[0]);
		File result_path = new File(args[1]);

		System.out.println("inputpath: " + input_path.getAbsolutePath());
		System.out.println("resultpath: " + result_path.getAbsolutePath());

		FileFilter fileFilter = new WildcardFilter("*events-info*");
		File file_list[] = input_path.listFiles(fileFilter);

		try {
			FileWriter fw = new FileWriter(result_path + "/result", Ture);
			BufferedWriter writer = new BufferedWriter(fw);

			test_event_processor event_processor = new test_event_processor("online_sjy");

			for (int i = 0; i < file_list.length; i++) {
				String[] name_list = file_list[i].getAbsolutePath().split("/");
				String file_name = name_list[name_list.length - 1];
				System.out.println(file_name);
				CSVReader reader = new CSVReader(new FileReader(file_list[i]));
				List<String[]> items_list = reader.readAll();
				for (String[] item_line : items_list) {
					event_processor.process(item_line);
				}

				Collection<ArrayList<Double>> values = event_processor.getEvent_table().values();
				System.out.println(values.size());

				writer.write(String.valueOf(file_list[i]));
				writer.newLine();
				writer.write(String.valueOf(values.size()));
				writer.newLine();

			}
			writer.close();
			System.out.println("Finish...");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
