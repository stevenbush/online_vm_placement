/**
 * This class is used to process every event by using method in L.Eyraud-Dubois,H.Larcheve queetal.,"Optimizingresourceallocation while handling sla violations in cloud computing platforms," in IPDPS- 27th IEEE International Parallel & Distributed Processing Symposium, 2013.
 * @author jiyuanshi(shijiyuan.seu@gmail.com)
 * @since 2013-12-14
 * @version V1.0
 */

package core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class OneDimensinal_ORA_Event_Processor extends Abstract_Event_Processor {

	/** a file writer used to write log */
	private BufferedWriter log_writer;
	/** the number of migrations */
	private Integer migration_num;
	/** the total number of vms */
	private Long total_vm_num;
	/** the Bins contain T-items */
	private LinkedList<Host> T_Bin_list;
	/** the Bins contain only T-items */
	private LinkedList<Host> TO_Bin_list;
	/** the unfill T-bins */
	private LinkedList<Host> UT_Bin_list;
	/** the Bins contain S-item */
	private LinkedList<Host> S_Bin_list;
	/** the Bins contain only one S-item */
	private LinkedList<Host> US_Bin_list;
	/** the Bins contain one S-item and some T-items */
	private LinkedList<Host> ST_Bin_list;
	/** the threshold of group */
	private Double group_threshold;

	public OneDimensinal_ORA_Event_Processor(BufferedWriter writer) {
		super();
		this.overload_threshold = 1.0;
		this.underload_threshold = 2.0 / 3.0;
		this.log_writer = writer;
		this.migration_num = 0;
		this.total_vm_num = (long) 0;
		this.T_Bin_list = new LinkedList<>();
		this.TO_Bin_list = new LinkedList<>();
		this.UT_Bin_list = new LinkedList<>();
		this.S_Bin_list = new LinkedList<>();
		this.US_Bin_list = new LinkedList<>();
		this.ST_Bin_list = new LinkedList<>();
		this.group_threshold = 1.0 / 3.0;
	}

	/**
	 * remove the host from the related list
	 * 
	 * @param host
	 */
	private void remove_host(Host host) {
		this.host_list.remove(host);
		this.T_Bin_list.remove(host);
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.S_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.ST_Bin_list.remove(host);
	}

	/**
	 * remove the host from the related bin type list
	 * 
	 * @param host
	 */
	private void remove_bin_type(Host host) {
		this.T_Bin_list.remove(host);
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.S_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.ST_Bin_list.remove(host);
	}

	/**
	 * update the bin type
	 * 
	 * @param host
	 */
	private void update_bin_type(Host host) {
		Integer T_num = 0;
		Integer S_num = 0;
		for (String id : host.getVm_list()) {
			VM current_vm = vm_table.get(id);
			if (current_vm.getCpu_demand() <= 1.0 / 2.0 && current_vm.getCpu_demand() > 1.0 / 3.0) { // S-item
				S_num = S_num + 1;
			}
			if (current_vm.getCpu_demand() <= 1.0 / 3.0 && current_vm.getCpu_demand() > 0.0) {// T-item
				T_num = T_num + 1;
			}
		}
		if (T_num > 0) {
			T_Bin_list.add(host);
		}
		if (T_num > 1 && S_num == 0) {
			TO_Bin_list.add(host);
			if (host.getCpu_utilization() <= 2.0 / 3.0) {
				UT_Bin_list.add(host);
			}
		}
		if (S_num > 0) {
			S_Bin_list.add(host);
		}
		if (S_num == 1 && T_num == 0) {
			US_Bin_list.add(host);
		}
		if (S_num > 0 && T_num > 0) {
			ST_Bin_list.add(host);
		}
	}

	/**
	 * this method is used to insert a S-item
	 * 
	 * @param vm
	 */
	private void insert(VM vm) {
		if (!US_Bin_list.isEmpty()) {
			// put this S-item into a exist US-bin
			Host host = US_Bin_list.get(0);
			vm.setDeployed_host(host);
			host.add_vm_in_group(vm, group_threshold);
			US_Bin_list.remove(host);
		} else {
			// open a new bin to accommodate this S-item
			Host new_host = new Host();
			vm.setDeployed_host(new_host);
			new_host.add_vm_in_group(vm, this.group_threshold);
			host_list.add(new_host);
			US_Bin_list.add(new_host);
			S_Bin_list.add(new_host);
		}
	}

	/**
	 * move a vm_group from source host to the destination host
	 * 
	 * @param src_host
	 * @param des_host
	 */
	private void move_group(Host src_host, Host des_host) {
		ArrayList<String> candidate_group = src_host.getGroup_list().get(0);
		src_host.delete_group(candidate_group, vm_table);
		for (String vm_id : candidate_group) {
			VM current_vm = vm_table.get(vm_id);
			current_vm.setDeployed_host(des_host);
			des_host.add_vm_in_group(current_vm, group_threshold);
		}
		des_host.update_group(vm_table, group_threshold);
	}

	/**
	 * this method is used to fill a L-bin
	 * 
	 * @param host
	 */
	private void fill(Host host) {
		while ((host.getCpu_utilization() <= 2.0 / 3.0)
				&& ((!this.T_Bin_list.isEmpty()) || (!this.ST_Bin_list.isEmpty()))) {
			if (!this.UT_Bin_list.isEmpty()) {
				Host current_bin = this.UT_Bin_list.get(0);
				this.move_group(current_bin, host);
				this.migration_num = this.migration_num + 1;
			} else {
				if (!this.TO_Bin_list.isEmpty()) {
					Host current_bin = this.TO_Bin_list.get(0);
					this.move_group(current_bin, host);
					this.migration_num = this.migration_num + 1;
				} else if (!this.ST_Bin_list.isEmpty()) {
					Host current_bin = this.TO_Bin_list.get(0);
					this.move_group(current_bin, host);
					this.migration_num = this.migration_num + 1;
				}
			}
		}
	}

	/**
	 * this method is used to merge two UT-bin
	 * 
	 * @param host1
	 * @param host2
	 */
	private void merge(Host host1, Host host2) {
		Host max_host;
		Host min_host;
		if (host1.getCpu_utilization() < host2.getCpu_utilization()) {
			min_host = host1;
			max_host = host2;
		} else {
			min_host = host2;
			max_host = host1;
		}
		while (min_host.getCpu_utilization() > 0 && max_host.getCpu_utilization() <= this.underload_threshold) {
			this.move_group(min_host, max_host);
			this.migration_num = this.migration_num + 1;
		}
	}

	/**
	 * this method is used to handle a overload host
	 * 
	 * @param host
	 */
	private void overload_handler(Host host) {
		ArrayList<VM> B_list = new ArrayList<>();
		ArrayList<VM> L_list = new ArrayList<>();
		ArrayList<VM> S_list = new ArrayList<>();
		ArrayList<VM> T_list = new ArrayList<>();
		for (String id : host.getVm_list()) {
			VM current_vm = vm_table.get(id);
			if (current_vm.getCpu_demand() > 2.0 / 3.0) { // B-item
				B_list.add(current_vm);
			}
			if (current_vm.getCpu_demand() <= 2.0 / 3.0 && current_vm.getCpu_demand() > 1.0 / 2.0) { // L-item
				L_list.add(current_vm);
			}
			if (current_vm.getCpu_demand() <= 1.0 / 2.0 && current_vm.getCpu_demand() > 1.0 / 3.0) { // S-item
				S_list.add(current_vm);
			}
			if (current_vm.getCpu_demand() <= 1.0 / 3.0 && current_vm.getCpu_demand() > 0.0) {// T-item
				T_list.add(current_vm);
			}
		}

		if (B_list.size() > 0) {
			VM current_vm = B_list.get(0);
			host.delete_vm_in_group(current_vm, this.group_threshold);
			// open a new bin to accommodate this B-item
			Host new_host = new Host();
			current_vm.setDeployed_host(new_host);
			new_host.add_vm_in_group(current_vm, this.group_threshold);
			host_list.add(new_host);
			this.migration_num = this.migration_num + 1;
		} else if (S_list.size() >= 2) {
			host.delete_vm_in_group(S_list.get(0), this.group_threshold);
			host.delete_vm_in_group(S_list.get(1), this.group_threshold);
			// open a new bin to accommodate the two S-item
			Host new_host = new Host();
			S_list.get(0).setDeployed_host(new_host);
			S_list.get(1).setDeployed_host(new_host);
			new_host.add_vm_in_group(S_list.get(0), this.group_threshold);
			new_host.add_vm_in_group(S_list.get(1), this.group_threshold);
			host_list.add(new_host);
			this.update_bin_type(new_host);
			this.migration_num = this.migration_num + 2;
		} else if (L_list.size() > 0) {
			Boolean flag = false;
			VM S_vm = null;
			for (VM vm : S_list) {
				if ((host.getCpu_utilization() - vm.getCpu_demand()) <= 1.0) {
					S_vm = vm;
					flag = true;
					break;
				}
			}
			if (flag) {
				host.delete_vm_in_group(S_vm, group_threshold);
				this.insert(S_vm);
				this.migration_num = this.migration_num + 1;
			} else {
				VM L_vm = L_list.get(0);
				host.delete_vm_in_group(L_vm, this.group_threshold);
				Host new_host = new Host();
				L_vm.setDeployed_host(new_host);
				new_host.add_vm_in_group(L_vm, this.group_threshold);
				host_list.add(new_host);
				fill(new_host);
				this.migration_num = this.migration_num + 1;
			}
		} else if (S_list.size() == 1) {
			VM S_vm = S_list.get(0);
			host.delete_vm_in_group(S_vm, group_threshold);
			this.insert(S_vm);
			this.migration_num = this.migration_num + 1;
		} else {
			VM max_T_vm = null;
			for (VM vm : T_list) {
				if (host.getCpu_utilization() - vm.getCpu_demand() <= 1.0) {
					max_T_vm = vm;
				}
			}

			host.delete_vm_in_group(max_T_vm, this.group_threshold);
			host.update_group(vm_table, this.group_threshold);

			if (!UT_Bin_list.isEmpty()) {
				// put this T-item into the exist UT-bin
				Host UT_host = UT_Bin_list.get(0);
				max_T_vm.setDeployed_host(UT_host);
				UT_host.add_vm_in_group(max_T_vm, group_threshold);
				UT_host.update_group(vm_table, group_threshold);
				if (host.getCpu_utilization() > 2.0 / 3.0) {
					UT_Bin_list.remove(UT_host);
				}
			} else {
				// open a new T-bin to accommodate this T-item
				Host new_host = new Host();
				max_T_vm.setDeployed_host(new_host);
				new_host.add_vm_in_group(max_T_vm, group_threshold);
				new_host.update_group(vm_table, group_threshold);
				host_list.add(new_host);
				T_Bin_list.add(new_host);
				TO_Bin_list.add(new_host);
				UT_Bin_list.add(new_host);
			}

			this.migration_num = this.migration_num + 1;

		}
	}

	/**
	 * this method is used to handle a underload host
	 * 
	 * @param host
	 */
	private void underload_handler(Host host) {
		Integer L_num = 0;
		Integer S_num = 0;
		VM S_item = null;
		for (String id : host.getVm_list()) {
			VM current_vm = vm_table.get(id);
			if (current_vm.getCpu_demand() <= 2.0 / 3.0 && current_vm.getCpu_demand() > 1.0 / 2.0) { // L-item
				L_num = L_num + 1;
			}
			if (current_vm.getCpu_demand() <= 1.0 / 2.0 && current_vm.getCpu_demand() > 1.0 / 3.0) { // S-item
				S_num = S_num + 1;
				S_item = current_vm;
			}
		}

		if (L_num > 0) {
			this.fill(host);
		} else {
			if (S_num > 0) {
				host.delete_vm_in_group(S_item, group_threshold);
				this.insert(S_item);
				this.migration_num = this.migration_num + 1;
			}
			if (!host.getVm_list().isEmpty() && !UT_Bin_list.isEmpty()) {
				if (!UT_Bin_list.contains(host)) {
					Host UT_host = UT_Bin_list.getFirst();
					this.remove_bin_type(UT_host);
					this.merge(host, UT_host);
					this.update_bin_type(UT_host);
					if (UT_host.getVm_list().isEmpty()) {
						this.remove_host(UT_host);
					}
				}
			}
		}
	}

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
			if (new_vm.getCpu_demand() > (2.0 / 3.0)) { // B-item
				// open a new bin to accommodate this B-item
				Host new_host = new Host();
				new_vm.setDeployed_host(new_host);
				new_host.add_vm_in_group(new_vm, this.group_threshold);
				host_list.add(new_host);
			} else if (new_vm.getCpu_demand() <= (2.0 / 3.0) && new_vm.getCpu_demand() > 1.0 / 2.0) { // L-item
				// open a new bin to accommodate this L-item and fill this L-bin
				Host new_host = new Host();
				new_vm.setDeployed_host(new_host);
				new_host.add_vm_in_group(new_vm, this.group_threshold);
				host_list.add(new_host);
				fill(new_host);
			} else if (new_vm.getCpu_demand() <= 1.0 / 2.0 && new_vm.getCpu_demand() > 1.0 / 3.0) { // S-item
				this.insert(new_vm);
			} else { // T-item
				if (!UT_Bin_list.isEmpty()) {
					// put this T-item into the exist UT-bin
					Host host = UT_Bin_list.get(0);
					new_vm.setDeployed_host(host);
					host.add_vm_in_group(new_vm, group_threshold);
					host.update_group(vm_table, group_threshold);
					if (host.getCpu_utilization() >= 2.0 / 3.0) {
						UT_Bin_list.remove(host);
					}
				} else {
					// open a new T-bin to accommodate this T-item
					Host new_host = new Host();
					new_vm.setDeployed_host(new_host);
					new_host.add_vm_in_group(new_vm, group_threshold);
					new_host.update_group(vm_table, group_threshold);
					host_list.add(new_host);
					T_Bin_list.add(new_host);
					TO_Bin_list.add(new_host);
					UT_Bin_list.add(new_host);
				}
			}
		}

	}

	@Override
	protected void process_update_event(String[] event) {
		this.migration_num = 0;
		String vm_id = event[1] + "-" + event[2];
		Double cpu_demand = Double.valueOf(event[6]);
		Double mem_demand = Double.valueOf(event[7]);
		if (vm_table.containsKey(vm_id)) {
			VM current_vm = vm_table.get(vm_id);
			current_vm.update_group(cpu_demand, mem_demand, group_threshold);
			Host deployed_host = current_vm.getDeployed_host();
			deployed_host.update_group(vm_table, this.group_threshold);
			remove_bin_type(deployed_host);
			if (deployed_host.getCpu_utilization() > this.overload_threshold) {
				this.overload_handler(deployed_host);
			}
			if (deployed_host.getCpu_utilization() < this.underload_threshold) {
				this.underload_handler(deployed_host);
			}
			this.update_bin_type(deployed_host);

			if (deployed_host.getVm_list().isEmpty()) {
				this.remove_host(deployed_host);
			}
		}

	}

	@Override
	protected void process_finish_event(String[] event) {
		this.migration_num = 0;
		String vm_id = event[1] + "-" + event[2];
		if (vm_table.containsKey(vm_id)) {
			VM finish_vm = vm_table.get(vm_id);
			Host deployed_host = finish_vm.getDeployed_host();
			deployed_host.delete_vm_in_group(finish_vm, group_threshold);
			deployed_host.update_group(vm_table, this.group_threshold);
			// if after deletion of vm, this host become empty, just remove this host from host list
			if (deployed_host.getVm_list().isEmpty()) {
				this.remove_host(deployed_host);
			} else {
				remove_bin_type(deployed_host);
				if (deployed_host.getCpu_utilization() <= this.underload_threshold) {
					underload_handler(deployed_host);
				}
				update_bin_type(deployed_host);

				if (deployed_host.getVm_list().isEmpty()) {
					this.remove_host(deployed_host);
				}
			}
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
		// TODO Auto-generated method stub
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

		// event_time+time_overhead+migrations_number+host_number+ccurrent_vm_number+total_vm_number
		String log_string = event_time + "," + String.valueOf(time_overhead) + "," + String.valueOf(this.migration_num)
				+ "," + String.valueOf(this.host_list.size()) + "," + String.valueOf(this.vm_table.size()) + ","
				+ String.valueOf(this.total_vm_num);

		// record log information
		try {
			this.log_writer.write(log_string);
			this.log_writer.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
