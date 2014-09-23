/**
 * This class is used to process every event when considering two dimensional resource demand
 * 
 * @author jiyuanshi(shijiyuan.seu@gmail.com)
 * @since 2013-12-15
 * @version V1.0
 */

package core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class TwoDimensional_BestFit_Event_Processor extends Abstract_Event_Processor {

	/** the hash table used to store the vms */
	public Hashtable<String, VM> vm_table;
	/** the list used to store the host */
	public ArrayList<BestFit_Host> host_list;
	/** the overload threshold */
	private Double overload_threshold;
	/** the underload threshold */
	private Double underload_threshold;
	/** a file writer used to write log */
	private BufferedWriter log_writer;
	/** the number of migrations */
	private Integer migration_num;
	/** the total number of vms */
	private Long total_vm_num;
	/** the total cpu workload size */
	private Double total_cpu_workload_size;
	/** the total mem workload size */
	private Double total_mem_workload_size;

	public TwoDimensional_BestFit_Event_Processor(BufferedWriter writer) {
		super();
		this.overload_threshold = 1.0;
		this.underload_threshold = 2.0 / 3.0;
		this.vm_table = new Hashtable<>();
		this.host_list = new ArrayList<BestFit_Host>();
		this.log_writer = writer;
		this.migration_num = 0;
		this.total_vm_num = (long) 0;
		this.total_cpu_workload_size = 0.0;
		this.total_mem_workload_size = 0.0;
	}

	/**
	 * This method is used to calculate the vectordot of a vm and a host
	 * 
	 * @param vm
	 * @param host
	 * @return the vectordot value
	 */
	private Double vector_dot(VM vm, BestFit_Host host) {
		Double vectordot_value = 0.0;
		vectordot_value = vm.getCpu_demand() * host.getCpu_utilization() + vm.getMem_demand()
				+ host.getMem_utilization();
		return vectordot_value;
	}

	/**
	 * use best fit method to put a vm into a host
	 * 
	 * @param vm
	 */
	private void put_vm(VM vm) {
		Boolean find_host = false;
		Double min_vectordot = Double.MAX_VALUE;
		BestFit_Host candidate_host = new BestFit_Host();
		for (BestFit_Host host : host_list) {
			if (vm.getCpu_demand() <= host.getCpu_gap() && vm.getMem_demand() <= host.getMem_gap()) {
				find_host = true;
				Double current_vectordot = vector_dot(vm, host);
				if (current_vectordot <= min_vectordot) {
					candidate_host = host;
					min_vectordot = current_vectordot;
				}
			}
		}

		// if can not find a exist host to accept the new vm, we open a new
		// host to accept this new vm
		if (!find_host) {
			BestFit_Host new_host = new BestFit_Host();
			vm.setDeployed_host(new_host);
			new_host.add_vm(vm);
			host_list.add(new_host);
		} else {
			vm.setDeployed_host(candidate_host);
			candidate_host.add_vm(vm);
		}
	}

	/**
	 * this method is used to process the submit event
	 * 
	 * @param event
	 */
	@Override
	protected void process_submit_event(String[] event) {
		this.migration_num = 0;
		String vm_id = event[1] + "-" + event[2];
		Double cpu_demand = Double.valueOf(event[6]);
		Double mem_demand = Double.valueOf(event[7]);
		if (!vm_table.containsKey(vm_id)) {
			this.total_vm_num = this.total_vm_num + 1;
			VM new_vm = new VM(vm_id, cpu_demand, mem_demand);
			vm_table.put(vm_id, new_vm);
			this.put_vm(new_vm);
			this.total_cpu_workload_size = this.total_cpu_workload_size + cpu_demand;
			this.total_mem_workload_size = this.total_mem_workload_size + mem_demand;
		}

	}

	/**
	 * this method is used to process the update event
	 * 
	 * @param event
	 */
	@Override
	protected void process_update_event(String[] event) {
		this.migration_num = 0;
		String vm_id = event[1] + "-" + event[2];
		Double cpu_demand = Double.valueOf(event[6]);
		Double mem_demand = Double.valueOf(event[7]);
		if (vm_table.containsKey(vm_id)) {
			Double original_cpu_demand = vm_table.get(vm_id).getCpu_demand();
			Double original_mem_demand = vm_table.get(vm_id).getMem_demand();
			VM current_vm = vm_table.get(vm_id);
			current_vm.update(cpu_demand, mem_demand);
			if ((current_vm.getDeployed_host().getCpu_utilization() > this.overload_threshold)
					|| (current_vm.getDeployed_host().getMem_utilization() > this.overload_threshold)) {
				// in order to avoid host overload, we just migrate the updated vm to other host
				BestFit_Host current_host = (BestFit_Host) current_vm.getDeployed_host();
				current_host.delete_vm(current_vm);
				// if after deletion of vm, this host become empty, just remove this host from host list
				if (current_host.getVm_list().isEmpty()) {
					host_list.remove(current_host);
				}
				this.put_vm(current_vm);
				this.migration_num++;
			}
			this.total_cpu_workload_size = this.total_cpu_workload_size - original_cpu_demand + cpu_demand;
			this.total_mem_workload_size = this.total_mem_workload_size - original_mem_demand + mem_demand;
		}

	}

	/**
	 * this method is used to process the finish event
	 * 
	 * @param event
	 */
	@Override
	protected void process_finish_event(String[] event) {
		this.migration_num = 0;
		String vm_id = event[1] + "-" + event[2];
		if (vm_table.containsKey(vm_id)) {
			VM finish_vm = vm_table.get(vm_id);
			BestFit_Host deployed_host = (BestFit_Host) finish_vm.getDeployed_host();
			deployed_host.delete_vm(finish_vm);
			// if after deletion of vm, this host become empty, just remove
			// this host from host list
			if (deployed_host.getVm_list().isEmpty()) {
				host_list.remove(deployed_host);
			}
			this.total_cpu_workload_size = this.total_cpu_workload_size - vm_table.get(vm_id).getCpu_demand();
			this.total_mem_workload_size = this.total_mem_workload_size - vm_table.get(vm_id).getMem_demand();
			vm_table.remove(vm_id);
		}

	}

	/**
	 * this function is used to process the incoming event
	 * 
	 * @param event
	 *            : the event string
	 */
	@Override
	public void process(String[] event) {
		String event_time = event[0];
		String event_type = event[3];
		Long begin_time = System.nanoTime();
		if (event_type.equals("1")) {
			process_submit_event(event);
		}

		if (event_type.equals("8")) {
			process_update_event(event);
		}

		if (event_type.equals("4")) {
			process_finish_event(event);
		}
		Long end_time = System.nanoTime();
		Long time_overhead = end_time - begin_time;

		// event_time+time_overhead+migrations_number+host_number+current_vm_number+total_vm_number+cpu_workload+mem_workload
		String log_string = event_time + "," + String.valueOf(time_overhead) + "," + String.valueOf(this.migration_num)
				+ "," + String.valueOf(this.host_list.size()) + "," + String.valueOf(this.vm_table.size()) + ","
				+ String.valueOf(this.total_vm_num) + "," + String.valueOf(this.total_cpu_workload_size) + ","
				+ String.valueOf(this.total_mem_workload_size);

		// record log information
		try {
			this.log_writer.write(log_string);
			this.log_writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
