/**
 * This class is used to process every event for two dimensional situation by using method in
 * L.Eyraud-Dubois,H.Larcheveˆqueetal.,
 * "Optimizingresourceallocation while handling sla violations in cloud computing platforms," in IPDPS- 27th IEEE
 * International Parallel & Distributed Processing Symposium, 2013.
 * 
 * @author jiyuanshi(shijiyuan.seu@gmail.com)
 * @since 2013-12-14
 * @version V1.0
 */

package core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import org.apache.commons.math3.analysis.function.Max;

public class TwoDimensional_ORA_Event_Processor extends Abstract_Event_Processor {

	/** the hash table used to store the vms */
	private Hashtable<String, VM> vm_table;
	/** the list used to store the host */
	private ArrayList<ORA_Host> host_list;
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
	/** the Bins contain only T-items */
	private LinkedList<ORA_Host> TO_Bin_list;
	/** the unfill T-bins */
	private LinkedList<ORA_Host> UT_Bin_list;
	/** the Bins contain S-item */
	private LinkedList<ORA_Host> S_Bin_list;
	/** the Bins contain only one S-item */
	private LinkedList<ORA_Host> US_Bin_list;
	/** the Bins contain one S-item and some T-items */
	private LinkedList<ORA_Host> ST_Bin_list;
	/** the total cpu workload size */
	private Double total_cpu_workload_size;
	/** the total mem workload size */
	private Double total_mem_workload_size;

	public TwoDimensional_ORA_Event_Processor(BufferedWriter writer) {
		super();
		this.overload_threshold = 1.0;
		this.underload_threshold = 2.0 / 3.0;
		this.vm_table = new Hashtable<>();
		this.host_list = new ArrayList<ORA_Host>();
		this.log_writer = writer;
		this.migration_num = 0;
		this.total_vm_num = (long) 0;
		this.TO_Bin_list = new LinkedList<>();
		this.UT_Bin_list = new LinkedList<>();
		this.S_Bin_list = new LinkedList<>();
		this.US_Bin_list = new LinkedList<>();
		this.ST_Bin_list = new LinkedList<>();
		this.total_cpu_workload_size = 0.0;
		this.total_mem_workload_size = 0.0;
	}

	/**
	 * remove the host from the related list
	 * 
	 * @param host
	 */
	private void remove_host(ORA_Host host) {
		this.host_list.remove(host);
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.S_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.ST_Bin_list.remove(host);
	}

	/**
	 * clear the bin type from the related list
	 * 
	 * @param host
	 */
	private void clear_bin_type(ORA_Host host) {
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
	private void update_bin_type(ORA_Host host) {
		Integer B_num = host.getB_num_intwo();
		Integer L_num = host.getL_num_intwo();
		Integer S_num = host.getS_num_intwo();
		Integer T_num = host.getT_num_intwo();

		// first clear the host type
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.S_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.ST_Bin_list.remove(host);

		if (T_num > 0 && S_num == 0 && L_num == 0 && B_num == 0) {
			TO_Bin_list.add(host);
			if (host.getMax_uitilization() <= 2.0 / 3.0) {
				UT_Bin_list.add(host);
			}
		}
		if (S_num > 0 && B_num == 0 && L_num == 0) {
			S_Bin_list.add(host);
		}
		if (S_num == 1 && T_num == 0 && L_num == 0 && B_num == 0) {
			US_Bin_list.add(host);
		}
		if (S_num > 0 && T_num > 0 && L_num == 0 && B_num == 0) {
			ST_Bin_list.add(host);
		}

		if (host.getVm_list().isEmpty()) {
			this.remove_host(host);
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
			ORA_Host host = US_Bin_list.get(0);
			vm.setDeployed_host(host);
			host.add_vm(vm);
			this.update_bin_type(host);
		} else {
			// open a new bin to accommodate this S-item
			ORA_Host new_host = new ORA_Host();
			vm.setDeployed_host(new_host);
			new_host.add_vm(vm);
			this.update_bin_type(new_host);
			host_list.add(new_host);
		}
	}

	/**
	 * move a vm_group from source host to the destination host
	 * 
	 * @param src_host
	 * @param des_host
	 */
	private void move_group(ORA_Host src_host, ORA_Host des_host) {
		ArrayList<String> candidate_group = src_host.get_group_intwo(vm_table).get(0);
		for (String vm_id : candidate_group) {
			VM current_vm = vm_table.get(vm_id);
			src_host.delete_vm(current_vm);
			current_vm.setDeployed_host(des_host);
			des_host.add_vm(current_vm);
		}
		// this.update_bin_type(src_host);
		// this.update_bin_type(des_host);
		if (src_host.getVm_list().isEmpty()) {
			this.remove_host(src_host);
		}
	}

	/**
	 * this method is used to fill a L-bin
	 * 
	 * @param host
	 */
	private void fill(ORA_Host host) {
		while ((host.getMax_uitilization() <= 2.0 / 3.0)
				&& ((!this.TO_Bin_list.isEmpty()) || (!this.ST_Bin_list.isEmpty()))) {
			if (!this.UT_Bin_list.isEmpty()) {
				ORA_Host current_bin = this.UT_Bin_list.get(0);
				this.move_group(current_bin, host);
				this.update_bin_type(current_bin);
				this.migration_num = this.migration_num + 1;
			} else {
				if (!this.TO_Bin_list.isEmpty()) {
					ORA_Host current_bin = this.TO_Bin_list.get(0);
					this.move_group(current_bin, host);
					this.update_bin_type(current_bin);
					this.migration_num = this.migration_num + 1;
				} else if (!this.ST_Bin_list.isEmpty()) {
					ORA_Host current_bin = this.ST_Bin_list.get(0);
					this.move_group(current_bin, host);
					this.update_bin_type(current_bin);
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
	private void merge(ORA_Host host1, ORA_Host host2) {
		ORA_Host max_host;
		ORA_Host min_host;
		if (host1.getMax_uitilization() < host2.getMax_uitilization()) {
			min_host = host1;
			max_host = host2;
		} else {
			min_host = host2;
			max_host = host1;
		}

		while (min_host.getMax_uitilization() > 0 && max_host.getMax_uitilization() <= this.underload_threshold) {
			this.move_group(min_host, max_host);
			this.migration_num = this.migration_num + 1;
		}
	}

	/**
	 * this method is used to handle a overload host
	 * 
	 * @param host
	 */
	private void overload_handler(ORA_Host host) {
		ArrayList<VM> B_list = new ArrayList<>();
		ArrayList<VM> L_list = new ArrayList<>();
		ArrayList<VM> S_list = new ArrayList<>();
		ArrayList<VM> T_list = new ArrayList<>();
		for (String id : host.getVm_list()) {
			VM current_vm = vm_table.get(id);
			if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 2.0 / 3.0) { // B-item
				B_list.add(current_vm);
			}
			if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 2.0 / 3.0
					&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 2.0) { // L-item
				L_list.add(current_vm);
			}
			if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 1.0 / 2.0
					&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 3.0) { // S-item
				S_list.add(current_vm);
			}
			if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 1.0 / 3.0
					&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 0.0) {// T-item
				T_list.add(current_vm);
			}
		}

		if (B_list.size() > 0) {
			VM current_vm = B_list.get(0);
			host.delete_vm(current_vm);
			// open a new bin to accommodate this B-item
			ORA_Host new_host = new ORA_Host();
			current_vm.setDeployed_host(new_host);
			new_host.add_vm(current_vm);
			host_list.add(new_host);
			this.update_bin_type(new_host);
			this.update_bin_type(host);
			this.migration_num = this.migration_num + 1;
		} else if (S_list.size() >= 2) {
			host.delete_vm(S_list.get(0));
			host.delete_vm(S_list.get(1));
			// open a new bin to accommodate the two S-item
			ORA_Host new_host = new ORA_Host();
			S_list.get(0).setDeployed_host(new_host);
			S_list.get(1).setDeployed_host(new_host);
			new_host.add_vm(S_list.get(0));
			new_host.add_vm(S_list.get(1));
			host_list.add(new_host);
			this.update_bin_type(new_host);
			this.update_bin_type(host);
			this.migration_num = this.migration_num + 2;
		} else if (L_list.size() > 0) {
			Boolean flag = false;
			VM S_vm = null;
			for (VM vm : S_list) {
				if (host.getMax_uitilization() - Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 1.0) {
					S_vm = vm;
					flag = true;
					break;
				}
			}
			if (flag) {
				host.delete_vm(S_vm);
				// this.update_bin_type(host);
				this.insert(S_vm);
				this.migration_num = this.migration_num + 1;
			} else {
				VM L_vm = L_list.get(0);
				host.delete_vm(L_vm);
				ORA_Host new_host = new ORA_Host();
				L_vm.setDeployed_host(new_host);
				new_host.add_vm(L_vm);
				host_list.add(new_host);
				fill(new_host);
				this.update_bin_type(new_host);
				this.update_bin_type(host);
				this.migration_num = this.migration_num + 1;
			}
		} else if (S_list.size() == 1) {
			VM S_vm = S_list.get(0);
			host.delete_vm(S_vm);
			this.insert(S_vm);
			this.update_bin_type(host);
			this.migration_num = this.migration_num + 1;
		} else {
			VM max_T_vm = null;
			for (VM vm : T_list) {
				if (host.getMax_uitilization() - Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 1.0) {
					max_T_vm = vm;
				}
			}

			host.delete_vm(max_T_vm);

			if (!UT_Bin_list.isEmpty()) {
				// put this T-item into the exist UT-bin
				ORA_Host UT_host = UT_Bin_list.get(0);
				max_T_vm.setDeployed_host(UT_host);
				UT_host.add_vm(max_T_vm);
				this.update_bin_type(UT_host);
				this.update_bin_type(host);
			} else {
				// open a new T-bin to accommodate this T-item
				ORA_Host new_host = new ORA_Host();
				max_T_vm.setDeployed_host(new_host);
				new_host.add_vm(max_T_vm);
				host_list.add(new_host);
				this.update_bin_type(new_host);
				this.update_bin_type(host);
			}

			this.migration_num = this.migration_num + 1;

		}
	}

	/**
	 * this method is used to handle a underload host
	 * 
	 * @param host
	 */
	private void underload_handler(ORA_Host host) {
		Integer L_num = 0;
		Integer S_num = 0;
		VM S_item = null;
		for (String id : host.getVm_list()) {
			VM current_vm = vm_table.get(id);
			if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 2.0 / 3.0
					&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 2.0) { // L-item
				L_num = L_num + 1;
			}
			if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 1.0 / 2.0
					&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 3.0) { // S-item
				S_num = S_num + 1;
				S_item = current_vm;
			}
		}

		if (L_num > 0) {
			this.fill(host);
			this.update_bin_type(host);
		} else {
			if (S_num > 0) {
				host.delete_vm(S_item);
				// this.update_bin_type(host);
				this.insert(S_item);
				this.migration_num = this.migration_num + 1;
			}
			if (!host.getVm_list().isEmpty() && !UT_Bin_list.isEmpty()) {
				if (!UT_Bin_list.contains(host)) {
					ORA_Host UT_host = UT_Bin_list.getFirst();
					this.merge(host, UT_host);
					this.update_bin_type(UT_host);
					this.update_bin_type(host);
					if (UT_host.getVm_list().isEmpty()) {
						this.remove_host(UT_host);
					}
				}
			}
		}
		if (host.getVm_list().isEmpty()) {
			this.remove_host(host);
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
			if (Math.max(new_vm.getCpu_demand(), new_vm.getMem_demand()) > (2.0 / 3.0)) { // B-item
				// open a new bin to accommodate this B-item
				ORA_Host new_host = new ORA_Host();
				new_vm.setDeployed_host(new_host);
				new_host.add_vm(new_vm);
				host_list.add(new_host);
			} else if (Math.max(new_vm.getCpu_demand(), new_vm.getMem_demand()) <= (2.0 / 3.0)
					&& Math.max(new_vm.getCpu_demand(), new_vm.getMem_demand()) > 1.0 / 2.0) { // L-item
				// open a new bin to accommodate this L-item and fill this L-bin
				ORA_Host new_host = new ORA_Host();
				new_vm.setDeployed_host(new_host);
				new_host.add_vm(new_vm);
				host_list.add(new_host);
				fill(new_host);
				this.update_bin_type(new_host);
			} else if (Math.max(new_vm.getCpu_demand(), new_vm.getMem_demand()) <= 1.0 / 2.0
					&& Math.max(new_vm.getCpu_demand(), new_vm.getMem_demand()) > 1.0 / 3.0) { // S-item
				this.insert(new_vm);
			} else { // T-item
				if (!UT_Bin_list.isEmpty()) {
					// put this T-item into the exist UT-bin
					ORA_Host host = UT_Bin_list.get(0);
					new_vm.setDeployed_host(host);
					host.add_vm(new_vm);
					this.update_bin_type(host);
				} else {
					// open a new T-bin to accommodate this T-item
					ORA_Host new_host = new ORA_Host();
					new_vm.setDeployed_host(new_host);
					new_host.add_vm(new_vm);
					host_list.add(new_host);
					this.update_bin_type(new_host);
				}
			}

			this.total_cpu_workload_size = this.total_cpu_workload_size + cpu_demand;
			this.total_mem_workload_size = this.total_mem_workload_size + mem_demand;
		}
	}

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
			ORA_Host deployed_host = (ORA_Host) current_vm.getDeployed_host();
			// this.update_bin_type(deployed_host);
			if (deployed_host.getMax_uitilization() > this.overload_threshold) {
				this.clear_bin_type(deployed_host);
				this.overload_handler(deployed_host);
			}
			if (deployed_host.getMax_uitilization() < this.underload_threshold) {
				this.clear_bin_type(deployed_host);
				this.underload_handler(deployed_host);
			}
			this.update_bin_type(deployed_host);

			if (deployed_host.getVm_list().isEmpty()) {
				this.remove_host(deployed_host);
			}
			this.total_cpu_workload_size = this.total_cpu_workload_size - original_cpu_demand + cpu_demand;
			this.total_mem_workload_size = this.total_mem_workload_size - original_mem_demand + mem_demand;
		}
	}

	@Override
	protected void process_finish_event(String[] event) {
		this.migration_num = 0;
		String vm_id = event[1] + "-" + event[2];
		if (vm_table.containsKey(vm_id)) {
			VM finish_vm = vm_table.get(vm_id);
			ORA_Host deployed_host = (ORA_Host) finish_vm.getDeployed_host();
			deployed_host.delete_vm(finish_vm);
			// if after deletion of vm, this host become empty, just remove this host from host list
			if (deployed_host.getVm_list().isEmpty()) {
				this.remove_host(deployed_host);
			} else {
				// this.update_bin_type(deployed_host);
				if (deployed_host.getMax_uitilization() <= this.underload_threshold) {
					this.clear_bin_type(deployed_host);
					underload_handler(deployed_host);
				}
				this.update_bin_type(deployed_host);

				if (deployed_host.getVm_list().isEmpty()) {
					this.remove_host(deployed_host);
				}
			}
			this.total_cpu_workload_size = this.total_cpu_workload_size - finish_vm.getCpu_demand();
			this.total_mem_workload_size = this.total_mem_workload_size - finish_vm.getMem_demand();
			vm_table.remove(vm_id);
		}
	}

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
