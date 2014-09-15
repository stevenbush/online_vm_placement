/**
 * This is a abstract event processor class
 * 
 *@author jiyuanshi(shijiyuan.seu@gmail.com)
 *@since 2013-12-14
 *@version V1.0
 */
package core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;

public abstract class Abstract_Event_Processor {

	/** this comparator is used to sort list according the cpu demand */
	protected Comparator<Abstract_Host> cpu_comparator;
	/** this comparator is used to sort list according the mem demand */
	protected Comparator<Abstract_Host> mem_comparator;

	public Abstract_Event_Processor() {
		// this.overload_threshold = 1.0;
		// this.underload_threshold = 2.0 / 3.0;
		// this.vm_table = new Hashtable<>();
		// this.host_list = new ArrayList<Abstract_Host>();
		this.cpu_comparator = new Comparator<Abstract_Host>() {
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
		this.mem_comparator = new Comparator<Abstract_Host>() {
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
	}

	/**
	 * this method is used to process the submit event
	 * 
	 * @param event
	 * @throws Exception 
	 */
	protected abstract void process_submit_event(String[] event) throws Exception;

	/**
	 * this method is used to process the update event
	 * 
	 * @param event
	 * @throws Exception 
	 */
	protected abstract void process_update_event(String[] event) throws Exception;

	/**
	 * this method is used to process the finish event
	 * 
	 * @param event
	 * @throws Exception 
	 */
	protected abstract void process_finish_event(String[] event) throws Exception;

	/**
	 * this function is used to process the incoming event
	 * 
	 * @param event
	 *            : the event string
	 */
	public abstract void process(String[] event);
}
