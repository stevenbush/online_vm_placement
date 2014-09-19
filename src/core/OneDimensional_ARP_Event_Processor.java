package core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

public class OneDimensional_ARP_Event_Processor extends Abstract_Event_Processor {

	/** the hash table used to store the vms */
	public Hashtable<String, VM> vm_table;
	/** the list used to store the host */
	private ArrayList<ARP_host> host_list;
	/** the overload threshold */
	private Double overload_threshold;
	/** the underload threshold */
	private Double underload_threshold;
	/** a file writer used to write log */
	private BufferedWriter log_writer;
	/** a file writer used to write hostlog */
	private BufferedWriter host_log_writer;
	/** the number of migrations */
	private Integer migration_num;
	/** the total number of vms */
	private Long total_vm_num;
	/** the Bins contain only T-items */
	private LinkedList<ARP_host> TO_Bin_list;
	/** the unfill T-bins */
	private LinkedList<ARP_host> UT_Bin_list;
	/** the Bins contain S-item */
	private LinkedList<ARP_host> S_Bin_list;
	/** the Bins contain only one S-item */
	private LinkedList<ARP_host> US_Bin_list;
	/** the Bins contain one S-item and some T-items */
	private LinkedList<ARP_host> ST_Bin_list;
	/** the unfill L-bin */
	private LinkedList<ARP_host> UL_Bin_list;
	/** the total cpu workload size */
	private Double total_cpu_workload_size;
	/** the total mem workload size */
	private Double total_mem_workload_size;
	/** the time to record the pre time */
	private long pre_time;

	public OneDimensional_ARP_Event_Processor(BufferedWriter writer, BufferedWriter hostwriter) {
		super();
		this.overload_threshold = 1.0;
		this.underload_threshold = 2.0 / 3.0;
		this.vm_table = new Hashtable<>();
		this.host_list = new ArrayList<ARP_host>();
		this.log_writer = writer;
		this.host_log_writer = hostwriter;
		this.migration_num = 0;
		this.total_vm_num = (long) 0;
		this.TO_Bin_list = new LinkedList<>();
		this.UT_Bin_list = new LinkedList<>();
		this.S_Bin_list = new LinkedList<>();
		this.US_Bin_list = new LinkedList<>();
		this.ST_Bin_list = new LinkedList<>();
		this.UL_Bin_list = new LinkedList<>();
		this.total_cpu_workload_size = 0.0;
		this.total_mem_workload_size = 0.0;
		this.pre_time = 0;
	}

	/**
	 * remove the host from the related list
	 * 
	 * @param host
	 */
	private void remove_host(ARP_host host) {
		this.host_list.remove(host);
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.S_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.ST_Bin_list.remove(host);
		this.UL_Bin_list.remove(host);
	}

	/**
	 * clear the bin type from the related list
	 * 
	 * @param host
	 */
	private void clear_bin_type(ARP_host host) {
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.S_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.ST_Bin_list.remove(host);
		this.UL_Bin_list.remove(host);
	}

	/**
	 * update the bin type
	 * 
	 * @param host
	 */
	private void update_bin_type(ARP_host host) {
		Integer B_num = host.getB_num();
		Integer L_num = host.getL_num();
		Integer S_num = host.getS_num();
		Integer T_num = host.getT_num();

		// first clear the host type
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.S_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.ST_Bin_list.remove(host);
		this.UL_Bin_list.remove(host);

		if (T_num > 0 && S_num == 0 && L_num == 0 && B_num == 0) {
			TO_Bin_list.add(host);
			if (host.getCpu_utilization() <= 2.0 / 3.0) {
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
		if (L_num > 0 && host.getCpu_utilization() <= 2.0 / 3.0 && B_num == 0) {
			this.UL_Bin_list.add(host);
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
			ARP_host host = US_Bin_list.get(0);
			vm.setDeployed_host(host);
			host.add_vm(vm);
			this.update_bin_type(host);
		} else {
			// open a new bin to accommodate this S-item
			ARP_host new_host = new ARP_host();
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
	private void move_group(ARP_host src_host, ARP_host des_host) {
		ArrayList<String> candidate_group = src_host.get_group(vm_table).get(0);
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
	private void fill(ARP_host host) {
		while ((host.getCpu_utilization() <= 2.0 / 3.0)
				&& ((!this.TO_Bin_list.isEmpty()) || (!this.ST_Bin_list.isEmpty()))) {
			if (!this.UT_Bin_list.isEmpty()) {
				ARP_host current_bin = this.UT_Bin_list.get(0);
				this.move_group(current_bin, host);
				this.update_bin_type(current_bin);
				this.migration_num = this.migration_num + 1;
			} else {
				if (!this.TO_Bin_list.isEmpty()) {
					ARP_host current_bin = this.TO_Bin_list.get(0);
					this.move_group(current_bin, host);
					this.update_bin_type(current_bin);
					this.migration_num = this.migration_num + 1;
				} else if (!this.ST_Bin_list.isEmpty()) {
					ARP_host current_bin = this.ST_Bin_list.get(0);
					this.move_group(current_bin, host);
					this.update_bin_type(current_bin);
					this.migration_num = this.migration_num + 1;
				}
			}
		}
	}

	/**
	 * put a T-group to a suitable bin
	 * 
	 * @param T_group
	 */
	private void fillwith(ArrayList<String> T_group) {
		if (!this.UL_Bin_list.isEmpty()) { // if we have a unfill L-bin
			// put the T-group to the existing unfill L-bin
			ARP_host des_host = this.UL_Bin_list.get(0);
			for (String vm_id : T_group) {
				VM current_vm = vm_table.get(vm_id);
				current_vm.setDeployed_host(des_host);
				des_host.add_vm(current_vm);
			}
			this.update_bin_type(des_host);
		} else if (!this.UT_Bin_list.isEmpty()) { // if we have a unfill T-bin
			// put this T-item into the existing UT-bin
			ARP_host des_host = UT_Bin_list.get(0);
			for (String vm_id : T_group) {
				VM current_vm = vm_table.get(vm_id);
				current_vm.setDeployed_host(des_host);
				des_host.add_vm(current_vm);
			}
			this.update_bin_type(des_host);
		} else { // open a new bin to accommodate this T-group
			ARP_host des_host = new ARP_host();
			for (String vm_id : T_group) {
				VM current_vm = vm_table.get(vm_id);
				current_vm.setDeployed_host(des_host);
				des_host.add_vm(current_vm);
			}
			this.host_list.add(des_host);
			this.update_bin_type(des_host);
		}
	}

	/**
	 * move a T-group from source host to other bin
	 * 
	 * @param T_group
	 */
	private void fillwith(ARP_host src_host, ArrayList<String> T_group) {
		if (!this.UL_Bin_list.isEmpty()) { // if we have a unfill L-bin
			// put the T-group to the existing unfill L-bin
			ARP_host des_host = this.UL_Bin_list.get(0);
			for (String vm_id : T_group) {
				VM current_vm = vm_table.get(vm_id);
				src_host.delete_vm(current_vm);
				current_vm.setDeployed_host(des_host);
				des_host.add_vm(current_vm);
			}
			this.update_bin_type(src_host);
			this.update_bin_type(des_host);
			if (src_host.getVm_list().isEmpty()) {
				this.remove_host(src_host);
			}
		} else if (!this.UT_Bin_list.isEmpty()) { // if we have a unfill T-bin
			// put this T-item into the existing UT-bin
			ARP_host des_host = UT_Bin_list.get(0);
			for (String vm_id : T_group) {
				VM current_vm = vm_table.get(vm_id);
				src_host.delete_vm(current_vm);
				current_vm.setDeployed_host(des_host);
				des_host.add_vm(current_vm);
			}
			this.update_bin_type(src_host);
			this.update_bin_type(des_host);
			if (src_host.getVm_list().isEmpty()) {
				this.remove_host(src_host);
			}
		} else { // open a new bin to accommodate this T-group
			ARP_host des_host = new ARP_host();
			for (String vm_id : T_group) {
				VM current_vm = vm_table.get(vm_id);
				src_host.delete_vm(current_vm);
				current_vm.setDeployed_host(des_host);
				des_host.add_vm(current_vm);
			}
			this.host_list.add(des_host);
			this.update_bin_type(src_host);
			this.update_bin_type(des_host);
			if (src_host.getVm_list().isEmpty()) {
				this.remove_host(src_host);
			}
		}
		this.migration_num = this.migration_num + 1;
	}

	/**
	 * move out the T-items of one host
	 * 
	 * @param host
	 */
	private void release(ARP_host host) {
		ArrayList<ArrayList<String>> groups = host.get_group(this.vm_table);
		for (ArrayList<String> group : groups) {
			this.clear_bin_type(host);
			this.fillwith(host, group);
		}
	}

	/**
	 * adjust a L-bin
	 * 
	 * @param host
	 */
	private void adjust(ARP_host host) {
		if (host.getL_num() > 0) {
			if (host.getCpu_utilization() > this.overload_threshold) {
				ArrayList<ArrayList<String>> groups = host.get_group(this.vm_table);
				for (ArrayList<String> group : groups) {
					this.clear_bin_type(host);
					this.fillwith(host, group);
					if (host.getCpu_utilization() <= this.overload_threshold) {
						break;
					}
				}
			}
			if (host.getCpu_utilization() <= this.underload_threshold) {
				this.clear_bin_type(host);
				this.fill(host);
			}
			this.update_bin_type(host);
		}
	}

	/**
	 * this method is used to merge two UT-bin
	 * 
	 * @param host1
	 * @param host2
	 */
	private void merge(ARP_host host1, ARP_host host2) {
		ARP_host max_host;
		ARP_host min_host;
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
				ARP_host new_host = new ARP_host();
				new_vm.setDeployed_host(new_host);
				new_host.add_vm(new_vm);
				host_list.add(new_host);
			} else if (new_vm.getCpu_demand() <= (2.0 / 3.0) && new_vm.getCpu_demand() > 1.0 / 2.0) { // L-item
				// open a new bin to accommodate this L-item and fill this L-bin
				ARP_host new_host = new ARP_host();
				new_vm.setDeployed_host(new_host);
				new_host.add_vm(new_vm);
				host_list.add(new_host);
				this.fill(new_host);
				this.update_bin_type(new_host);
			} else if (new_vm.getCpu_demand() <= 1.0 / 2.0 && new_vm.getCpu_demand() > 1.0 / 3.0) { // S-item
				this.insert(new_vm);
			} else {
				// using the fillwith operation to put this T-item to a suitable
				// bin
				ArrayList<String> T_group = new ArrayList<>();
				T_group.add(new_vm.getVm_id());
				this.fillwith(T_group);
			}

			this.total_cpu_workload_size = this.total_cpu_workload_size + cpu_demand;
			this.total_mem_workload_size = this.total_mem_workload_size + mem_demand;
		}

	}

	@Override
	protected void process_update_event(String[] event) {
		this.migration_num = 0;
		String vm_id = event[1] + "-" + event[2];
		Double new_cpu_demand = Double.valueOf(event[6]);
		Double new_mem_demand = Double.valueOf(event[7]);
		if (vm_table.containsKey(vm_id)) {
			Double original_cpu_demand = vm_table.get(vm_id).getCpu_demand();
			Double original_mem_demand = vm_table.get(vm_id).getMem_demand();
			VM current_vm = vm_table.get(vm_id);
			current_vm.update(new_cpu_demand, new_mem_demand);
			ARP_host deployed_host = (ARP_host) current_vm.getDeployed_host();

			if (original_cpu_demand <= 1.0 && original_cpu_demand > 2.0 / 3.0 && new_cpu_demand <= 2.0 / 3.0
					&& new_cpu_demand > 1.0 / 2.0) { // B-->L
				this.clear_bin_type(deployed_host);
				this.fill(deployed_host);
				this.update_bin_type(deployed_host);
			}

			if (original_cpu_demand <= 1.0 && original_cpu_demand > 2.0 / 3.0 && new_cpu_demand <= 1.0 / 2.0
					&& new_cpu_demand > 1.0 / 3.0) { // B-->S
				this.clear_bin_type(deployed_host);
				if (!this.US_Bin_list.isEmpty()) {
					deployed_host.delete_vm(current_vm);
					ARP_host des_host = US_Bin_list.get(0);
					des_host.add_vm(current_vm);
					this.update_bin_type(des_host);
					this.migration_num = this.migration_num + 1;
				}
			}

			if (original_cpu_demand <= 1.0 && original_cpu_demand > 2.0 / 3.0 && new_cpu_demand <= 1.0 / 3.0
					&& new_cpu_demand > 0.0) { // B-->T
				this.clear_bin_type(deployed_host);
				ArrayList<ArrayList<String>> groups = deployed_host.get_group(this.vm_table);
				for (ArrayList<String> group : groups) {
					if (!this.UL_Bin_list.isEmpty()) {
						ARP_host des_host = this.UL_Bin_list.get(0);
						for (String id : group) {
							VM t_vm = vm_table.get(id);
							deployed_host.delete_vm(t_vm);
							t_vm.setDeployed_host(des_host);
							des_host.add_vm(t_vm);
						}
						this.update_bin_type(des_host);
						this.migration_num = this.migration_num + 1;
					} else if (!this.UT_Bin_list.isEmpty()) {
						ARP_host des_host = UT_Bin_list.get(0);
						for (String id : group) {
							VM t_vm = vm_table.get(id);
							deployed_host.delete_vm(t_vm);
							t_vm.setDeployed_host(des_host);
							des_host.add_vm(t_vm);
						}
						this.update_bin_type(des_host);
						this.migration_num = this.migration_num + 1;
					} else {
						break;
					}
				}
			}

			if (original_cpu_demand <= 2.0 / 3.0 && original_cpu_demand > 1.0 / 2.0 && new_cpu_demand <= 1.0
					&& new_cpu_demand > 2.0 / 3.0) { // L-->B
				this.clear_bin_type(deployed_host);
				this.release(deployed_host);
			}

			if (original_cpu_demand <= 2.0 / 3.0 && original_cpu_demand > 1.0 / 2.0 && new_cpu_demand <= 2.0 / 3.0
					&& new_cpu_demand > 1.0 / 2.0) { // L-->L
				this.clear_bin_type(deployed_host);
				this.adjust(deployed_host);
			}

			if (original_cpu_demand <= 2.0 / 3.0 && original_cpu_demand > 1.0 / 2.0 && new_cpu_demand <= 1.0 / 2.0
					&& new_cpu_demand > 1.0 / 3.0) { // L-->S
				this.clear_bin_type(deployed_host);
				this.release(deployed_host);
				if (!this.US_Bin_list.isEmpty()) {
					ARP_host des_host = this.US_Bin_list.get(0);
					deployed_host.delete_vm(current_vm);
					des_host.add_vm(current_vm);
					this.update_bin_type(des_host);
					this.migration_num = this.migration_num + 1;
				}
			}

			if (original_cpu_demand <= 2.0 / 3.0 && original_cpu_demand > 1.0 / 2.0 && new_cpu_demand <= 1.0 / 3.0
					&& new_cpu_demand > 0.0) { // L-->T
				this.clear_bin_type(deployed_host);
				ArrayList<ArrayList<String>> groups = deployed_host.get_group(this.vm_table);
				for (ArrayList<String> group : groups) {
					if (!this.UL_Bin_list.isEmpty()) {
						ARP_host des_host = this.UL_Bin_list.get(0);
						for (String id : group) {
							VM t_vm = vm_table.get(id);
							deployed_host.delete_vm(t_vm);
							t_vm.setDeployed_host(des_host);
							des_host.add_vm(t_vm);
						}
						this.update_bin_type(des_host);
						this.migration_num = this.migration_num + 1;
					} else if (!this.UT_Bin_list.isEmpty()) {
						ARP_host des_host = UT_Bin_list.get(0);
						for (String id : group) {
							VM t_vm = vm_table.get(id);
							deployed_host.delete_vm(t_vm);
							t_vm.setDeployed_host(des_host);
							des_host.add_vm(t_vm);
						}
						this.update_bin_type(des_host);
						this.migration_num = this.migration_num + 1;
					} else {
						break;
					}
				}
			}

			if (original_cpu_demand <= 1.0 / 2.0 && original_cpu_demand > 1.0 / 3.0 && new_cpu_demand <= 1.0
					&& new_cpu_demand > 2.0 / 3.0) { // S-->B
				this.clear_bin_type(deployed_host);
				if (deployed_host.getS_num() > 0) {
					VM S_item = null;
					for (String id : deployed_host.getVm_list()) {
						VM vm = vm_table.get(id);
						if (vm.getCpu_demand() <= 1.0 / 2.0 && vm.getCpu_demand() > 1.0 / 3.0) { // S-item
							S_item = vm;
						}
					}
					deployed_host.delete_vm(S_item);
					this.insert(S_item);
					this.migration_num = this.migration_num + 1;
				}
			}

			if (original_cpu_demand <= 1.0 / 2.0 && original_cpu_demand > 1.0 / 3.0 && new_cpu_demand <= 2.0 / 3.0
					&& new_cpu_demand > 1.0 / 2.0) { // S-->L
				this.clear_bin_type(deployed_host);
				if (deployed_host.getS_num() > 0) {
					VM S_item = null;
					for (String id : deployed_host.getVm_list()) {
						VM vm = vm_table.get(id);
						if (vm.getCpu_demand() <= 1.0 / 2.0 && vm.getCpu_demand() > 1.0 / 3.0) { // S-item
							S_item = vm;
						}
					}
					deployed_host.delete_vm(S_item);
					this.insert(S_item);
					this.migration_num = this.migration_num + 1;
				}
				this.fill(deployed_host);
			}

			if (original_cpu_demand <= 1.0 / 2.0 && original_cpu_demand > 1.0 / 3.0 && new_cpu_demand <= 1.0 / 3.0
					&& new_cpu_demand > 0.0) { // S-->T
				this.clear_bin_type(deployed_host);
				if (deployed_host.getS_num() > 0 && !this.US_Bin_list.isEmpty()) {
					VM S_item = null;
					for (String id : deployed_host.getVm_list()) {
						VM vm = vm_table.get(id);
						if (vm.getCpu_demand() <= 1.0 / 2.0 && vm.getCpu_demand() > 1.0 / 3.0) { // S-item
							S_item = vm;
						}
					}
					deployed_host.delete_vm(S_item);
					ARP_host des_host = US_Bin_list.get(0);
					des_host.add_vm(S_item);
					this.update_bin_type(des_host);
					this.migration_num = this.migration_num + 1;
				}

				ArrayList<ArrayList<String>> groups = deployed_host.get_group(this.vm_table);
				for (ArrayList<String> group : groups) {
					if (!this.UL_Bin_list.isEmpty()) {
						ARP_host des_host = this.UL_Bin_list.get(0);
						for (String id : group) {
							VM t_vm = vm_table.get(id);
							deployed_host.delete_vm(t_vm);
							t_vm.setDeployed_host(des_host);
							des_host.add_vm(t_vm);
						}
						this.update_bin_type(des_host);
						this.migration_num = this.migration_num + 1;
					} else if (!this.UT_Bin_list.isEmpty()) {
						ARP_host des_host = UT_Bin_list.get(0);
						for (String id : group) {
							VM t_vm = vm_table.get(id);
							deployed_host.delete_vm(t_vm);
							t_vm.setDeployed_host(des_host);
							des_host.add_vm(t_vm);
						}
						this.update_bin_type(des_host);
						this.migration_num = this.migration_num + 1;
					} else if (deployed_host.getS_num() > 0) {
						ARP_host des_host = new ARP_host();
						for (String id : group) {
							VM t_vm = vm_table.get(id);
							deployed_host.delete_vm(t_vm);
							t_vm.setDeployed_host(des_host);
							des_host.add_vm(current_vm);
						}
						this.host_list.add(des_host);
						this.update_bin_type(des_host);
						this.migration_num = this.migration_num + 1;
					} else {
						break;
					}
				}
			}

			if (original_cpu_demand <= 1.0 / 3.0 && original_cpu_demand > 0.0 && new_cpu_demand <= 1.0
					&& new_cpu_demand > 2.0 / 3.0) { // T-->B
				this.clear_bin_type(deployed_host);
				if (deployed_host.getL_num() > 0) {
					VM L_item = null;
					for (String id : deployed_host.getVm_list()) {
						VM vm = vm_table.get(id);
						if (vm.getCpu_demand() <= 2.0 / 3.0 && vm.getCpu_demand() > 1.0 / 2.0) { // L-item
							L_item = vm;
						}
					}
					// open a new bin to accommodate this L-item and fill this
					// L-bin
					deployed_host.delete_vm(L_item);
					ARP_host new_host = new ARP_host();
					L_item.setDeployed_host(new_host);
					new_host.add_vm(L_item);
					host_list.add(new_host);
					this.migration_num = this.migration_num + 1;
					this.fill(new_host);
					this.update_bin_type(new_host);
				}
				this.release(deployed_host);
			}

			if (original_cpu_demand <= 1.0 / 3.0 && original_cpu_demand > 0.0 && new_cpu_demand <= 2.0 / 3.0
					&& new_cpu_demand > 1.0 / 2.0) { // T-->L
				this.clear_bin_type(deployed_host);
				if (deployed_host.getL_num() > 1) {
					VM L_item = null;
					for (String id : deployed_host.getVm_list()) {
						VM vm = vm_table.get(id);
						if (vm.getCpu_demand() <= 2.0 / 3.0 && vm.getCpu_demand() > 1.0 / 2.0) { // L-item
							L_item = vm;
						}
					}
					// open a new bin to accommodate this L-item and fill this
					// L-bin
					deployed_host.delete_vm(L_item);
					ARP_host new_host = new ARP_host();
					L_item.setDeployed_host(new_host);
					new_host.add_vm(L_item);
					host_list.add(new_host);
					this.migration_num = this.migration_num + 1;
					this.fill(new_host);
					this.update_bin_type(new_host);
				}
				this.adjust(deployed_host);
			}

			if (original_cpu_demand <= 1.0 / 3.0 && original_cpu_demand > 0.0 && new_cpu_demand <= 1.0 / 2.0
					&& new_cpu_demand > 1.0 / 3.0) { // T-->S
				this.clear_bin_type(deployed_host);
				if (deployed_host.getL_num() > 0) {
					deployed_host.delete_vm(current_vm);
					this.insert(current_vm);
					this.migration_num = this.migration_num + 1;
					this.fill(deployed_host);
				} else if (!this.US_Bin_list.isEmpty()) {
					deployed_host.delete_vm(current_vm);
					ARP_host us_host = US_Bin_list.get(0);
					us_host.add_vm(current_vm);
					this.update_bin_type(us_host);
					this.migration_num = this.migration_num + 1;

					ArrayList<ArrayList<String>> groups = deployed_host.get_group(this.vm_table);
					for (ArrayList<String> group : groups) {
						if (!this.UL_Bin_list.isEmpty()) {
							ARP_host ul_host = this.UL_Bin_list.get(0);
							for (String id : group) {
								VM t_vm = vm_table.get(id);
								deployed_host.delete_vm(t_vm);
								t_vm.setDeployed_host(ul_host);
								ul_host.add_vm(t_vm);
							}
							this.update_bin_type(ul_host);
							this.migration_num = this.migration_num + 1;
						} else if (!this.UT_Bin_list.isEmpty()) {
							ARP_host ut_host = UT_Bin_list.get(0);
							for (String id : group) {
								VM t_vm = vm_table.get(id);
								deployed_host.delete_vm(t_vm);
								t_vm.setDeployed_host(ut_host);
								ut_host.add_vm(t_vm);
							}
							this.update_bin_type(ut_host);
							this.migration_num = this.migration_num + 1;
						} else {
							break;
						}
					}
				} else {
					this.release(deployed_host);
				}
			}

			if (original_cpu_demand <= 1.0 / 3.0 && original_cpu_demand > 0.0 && new_cpu_demand <= 1.0 / 3.0
					&& new_cpu_demand > 0.0) { // T-->T
				this.clear_bin_type(deployed_host);
				if (deployed_host.getL_num() > 0) {
					this.adjust(deployed_host);
				} else if (deployed_host.getCpu_utilization() > this.overload_threshold) {
					ArrayList<String> group = new ArrayList<>();
					group.add(vm_id);
					this.fillwith(deployed_host, group);
				} else {
					if (deployed_host.getCpu_utilization() <= 2.0 / 3.0 && !this.UT_Bin_list.isEmpty()) {
						if (!UT_Bin_list.contains(deployed_host)) {
							ARP_host UT_host = UT_Bin_list.getFirst();
							this.merge(deployed_host, UT_host);
							this.update_bin_type(UT_host);
							if (UT_host.getVm_list().isEmpty()) {
								this.remove_host(UT_host);
							}
						}
					}
				}
			}

			this.update_bin_type(deployed_host);
			if (deployed_host.getVm_list().isEmpty()) {
				this.remove_host(deployed_host);
			}
			this.total_cpu_workload_size = this.total_cpu_workload_size - original_cpu_demand + new_cpu_demand;
			this.total_mem_workload_size = this.total_mem_workload_size - original_mem_demand + new_mem_demand;
		}

	}

	@Override
	protected void process_finish_event(String[] event) {
		this.migration_num = 0;
		String vm_id = event[1] + "-" + event[2];
		if (vm_table.containsKey(vm_id)) {
			VM finish_vm = vm_table.get(vm_id);
			ARP_host deployed_host = (ARP_host) finish_vm.getDeployed_host();
			deployed_host.delete_vm(finish_vm);

			if (finish_vm.getCpu_demand() > (2.0 / 3.0)) { // B-item
				this.update_bin_type(deployed_host);
				if (deployed_host.getVm_list().isEmpty()) {
					this.remove_host(deployed_host);
				}
			} else if (finish_vm.getCpu_demand() <= (2.0 / 3.0) && finish_vm.getCpu_demand() > 1.0 / 2.0) { // L-item
				this.clear_bin_type(deployed_host);
				ArrayList<ArrayList<String>> groups = deployed_host.get_group(this.vm_table);
				for (ArrayList<String> group : groups) {
					if (!this.UL_Bin_list.isEmpty()) { // if we have a unfill
														// L-bin put the T-group
														// to the existing
														// unfill L-bin
						ARP_host des_host = this.UL_Bin_list.get(0);
						for (String id : group) {
							VM current_vm = vm_table.get(id);
							deployed_host.delete_vm(current_vm);
							current_vm.setDeployed_host(des_host);
							des_host.add_vm(current_vm);
						}
						this.update_bin_type(des_host);
						this.migration_num = this.migration_num + 1;
					} else if (!this.UT_Bin_list.isEmpty()) { // if we have a
																// unfill T-bin
																// put this
																// T-item into
																// the
																// existing
																// UT-bin
						ARP_host des_host = UT_Bin_list.get(0);
						for (String id : group) {
							VM current_vm = vm_table.get(id);
							deployed_host.delete_vm(current_vm);
							current_vm.setDeployed_host(des_host);
							des_host.add_vm(current_vm);
						}
						this.update_bin_type(des_host);
						this.migration_num = this.migration_num + 1;
					} else {
						break;
					}
				}
				this.update_bin_type(deployed_host);
				if (deployed_host.getVm_list().isEmpty()) {
					this.remove_host(deployed_host);
				}

			} else if (finish_vm.getCpu_demand() <= 1.0 / 2.0 && finish_vm.getCpu_demand() > 1.0 / 3.0) { // S-item
				this.clear_bin_type(deployed_host);
				if (deployed_host.getS_num() > 0 && !this.US_Bin_list.isEmpty()) {
					VM S_item = null;
					for (String id : deployed_host.getVm_list()) {
						VM current_vm = vm_table.get(id);
						if (current_vm.getCpu_demand() <= 1.0 / 2.0 && current_vm.getCpu_demand() > 1.0 / 3.0) { // S-item
							S_item = current_vm;
						}
					}
					deployed_host.delete_vm(S_item);
					ARP_host des_host = US_Bin_list.get(0);
					des_host.add_vm(S_item);
					this.update_bin_type(des_host);
					this.migration_num = this.migration_num + 1;
				}
				this.update_bin_type(deployed_host);
				if (deployed_host.getVm_list().isEmpty()) {
					this.remove_host(deployed_host);
				}
			} else { // T-item
				this.clear_bin_type(deployed_host);
				if (deployed_host.getL_num() > 0) {
					this.adjust(deployed_host);
				} else {
					if (deployed_host.getCpu_utilization() <= 2.0 / 3.0 && !this.UT_Bin_list.isEmpty()) {
						if (!UT_Bin_list.contains(deployed_host)) {
							ARP_host UT_host = UT_Bin_list.getFirst();
							this.merge(deployed_host, UT_host);
							this.update_bin_type(UT_host);
							if (UT_host.getVm_list().isEmpty()) {
								this.remove_host(UT_host);
							}
						}
					}
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
		long eventtime = Long.parseLong(event_time);
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

		// record the host information
		try {
			if (eventtime - pre_time >= 60000000) {
				if (host_list.size() > 0) {
					this.host_log_writer.write(eventtime + ":" + host_list.get(0).getCpu_utilization());

					for (int i = 1; i < host_list.size(); i++) {
						this.host_log_writer.write("," + host_list.get(i).getCpu_utilization());
					}
					this.host_log_writer.newLine();
					this.pre_time = eventtime;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
