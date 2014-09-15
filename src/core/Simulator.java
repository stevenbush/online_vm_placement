/**
 * This class is used to run simulation. Specifically this class read the workload file and extract every event
 * information, then it call the corresponding event processing class to process every event.
 * 
 * @author jiyuanshi
 * @since 2013-12-12
 * @version v1.0
 */

package core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.filefilter.WildcardFilter;

import au.com.bytecode.opencsv.CSVReader;

public class Simulator {

	public static void main(String[] args) {
		if (args.length < 4) {
			System.out.println("No input path, resultpath, algorithm name and result name specified.");
			System.out.println("please enter \"simulator inputpath resultpath algorithm_name result_name\"");
			return;
		}

		if (!args[2].equals("one_bestfit") && !args[2].equals("one_ORA") && !args[2].equals("one_ARP")
				&& !args[2].equals("one_ROBP") && !args[2].equals("two_bestfit") && !args[2].equals("two_ORA")
				&& !args[2].equals("two_ARP") && !args[2].equals("two_ROBP")) {
			System.out
					.println("the input algorithm name is wrong, please input the correct algorithm name: (one_bestfit, one_ORA, one_ARP, one_ROBP, two_bestfit, two_ORA, two_ARP, two_ROBP)");
			return;
		}

		File input_path = new File(args[0]);
		File result_path = new File(args[1]);
		String algorithm_name = args[2];
		String result_name = args[3];

		System.out.println("inputpath: " + input_path.getAbsolutePath());
		System.out.println("resultpath: " + result_path.getAbsolutePath());
		System.out.println("algorithm_name: " + algorithm_name);
		System.out.println("result_name: " + result_name);

		FileFilter fileFilter = new WildcardFilter("*events-info*");
		File file_list[] = input_path.listFiles(fileFilter);

		Arrays.sort(file_list);

		try {
			FileWriter fw = new FileWriter(result_path + "/" + result_name + "_" + algorithm_name + "_resultlog");
			BufferedWriter writer = new BufferedWriter(fw);
			System.out.println("Start Simulation...");

			Abstract_Event_Processor event_processor;

			if (algorithm_name.equals("one_bestfit")) {
				event_processor = new OneDimensional_BestFit_Event_Processor(writer);
			} else if (algorithm_name.equals("one_ORA")) {
				event_processor = new OneDimensional_ORA_Event_Processor(writer);
			} else if (algorithm_name.equals("one_ARP")) {
				event_processor = new OneDimensional_ARP_Event_Processor(writer);
			} else if (algorithm_name.equals("one_ROBP")) {
				event_processor = new OneDimensional_ROBP_Event_Processor(writer);
			} else if (algorithm_name.equals("two_bestfit")) {
				event_processor = new TwoDimensional_BestFit_Event_Processor(writer);
			} else if (algorithm_name.equals("two_ORA")) {
				event_processor = new TwoDimensional_ORA_Event_Processor(writer);
			} else if (algorithm_name.equals("two_ARP")) {
				event_processor = new TwoDimensional_ARP_Event_Processor(writer);
			} else if (algorithm_name.equals("two_ROBP")) {
				event_processor = new TwoDimensional_ROBP_Event_Processor(writer);
			} else {
				System.out.println("no algorithm specified.");
				writer.close();
				return;
			}

			for (int i = 0; i < file_list.length; i++) {
				String[] name_list = file_list[i].getAbsolutePath().split("/");
				String file_name = name_list[name_list.length - 1];
				System.out.println("processing file: " + file_name);
				CSVReader reader = new CSVReader(new FileReader(file_list[i]));
				List<String[]> items_list = reader.readAll();
				Integer total_num = items_list.size();
				Integer counter = 0;
				for (String[] item_line : items_list) {
					counter++;
					System.out.println("processing " + file_name + ": " + (counter * 100) / total_num + "%");
					event_processor.process(item_line);
				}
				reader.close();
				writer.flush();
			}

			writer.close();
			System.out.println("Finish Simulation...");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
