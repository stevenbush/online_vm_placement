/**
 * This class is a simple demo event processor to test the program framework
 * 
 * @author jiyuanshi (shijiyuan.seu@gmail.com)
 * @since 2013-12-12
 * @version V1.0
 */

package test;

import java.util.ArrayList;
import java.util.Hashtable;

public class test_event_processor {

	private String algorithm_name;
	private Hashtable<String, ArrayList<Double>> event_table;

	public test_event_processor(String algorithm_name) {
		super();
		this.algorithm_name = algorithm_name;
		this.event_table = new Hashtable<>();
	}

	/**
	 * this function is used to process the incoming event
	 * 
	 * @param event
	 *            : the event string
	 */
	public void process(String[] event) {
		String event_id = event[1] + "-" + event[2];
		String event_type = event[3];
		ArrayList<Double> value_list = new ArrayList<>();
		value_list.add(Double.valueOf(event_type));
		value_list.add(Double.valueOf(Double.valueOf(event[6])));
		value_list.add(Double.valueOf(Double.valueOf(event[7])));

		if (event_type.equals("1")) {
			if (!event_table.containsKey(event_id)) {
				event_table.put(event_id, value_list);
			}
		}

		if (event_type.equals("8")) {
			if (event_table.containsKey(event_id)) {
				event_table.put(event_id, value_list);
			}
		}

		if (event_type.equals("4")) {
			if (event_table.containsKey(event_id)) {
				event_table.remove(event_id);
			}
		}
	}

	public Hashtable<String, ArrayList<Double>> getEvent_table() {
		return event_table;
	}
}
