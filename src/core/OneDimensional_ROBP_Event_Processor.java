/**
 * This class is used to process every event by using method Relaxed Online Bin-Packing Model related algorithm for one-dimensional situation. My
 * paper: Energy-aware Resource Allocation for Cloud based on Relaxed Online Bin-Packing Model
 * 
 * Compared to V1.0, we reduce the operation in overload handing procedures, please see the detail information in ROBP_v3.pdf.
 * 
 * @author jiyuanshi(shijiyuan.seu@gmail.com)
 * @since 2014-04-24
 * @version V2.0
 */

package core;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

public class OneDimensional_ROBP_Event_Processor extends Abstract_Event_Processor {

	/** the hash table used to store the vms */
	public Hashtable<String, VM> vm_table;
	/** the list used to store the host */
	public ArrayList<ROBP_Host> host_list;
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
	/** the Bins contain L-item */
	private LinkedList<ROBP_Host> L_Bin_list;
	/** the Bins contain S-item and some T-items or M-items */
	private LinkedList<ROBP_Host> S_Bin_list;
	/** the Bins contain only one S-item */
	private LinkedList<ROBP_Host> US_Bin_list;
	/** the Bins contain only T-items */
	private LinkedList<ROBP_Host> TO_Bin_list;
	/** the unfill T-bins */
	private LinkedList<ROBP_Host> UT_Bin_list;
	/** the Bins contain only M-items */
	private LinkedList<ROBP_Host> MO_Bin_list;
	/** the unfill M-bins */
	private LinkedList<ROBP_Host> UM_Bin_list;
	/** the total cpu workload size */
	private Double total_cpu_workload_size;
	/** the total mem workload size */
	private Double total_mem_workload_size;
	/** the time to record the pre time */
	private long pre_time;

	public OneDimensional_ROBP_Event_Processor(BufferedWriter writer, BufferedWriter hostwriter) {
		this.overload_threshold = 1.0;
		this.underload_threshold = 3.0 / 4.0;
		this.vm_table = new Hashtable<>();
		this.host_list = new ArrayList<ROBP_Host>();
		this.log_writer = writer;
		this.host_log_writer = hostwriter;
		this.migration_num = 0;
		this.total_vm_num = (long) 0;
		this.L_Bin_list = new LinkedList<>();
		this.S_Bin_list = new LinkedList<>();
		this.US_Bin_list = new LinkedList<>();
		this.TO_Bin_list = new LinkedList<>();
		this.UT_Bin_list = new LinkedList<>();
		this.MO_Bin_list = new LinkedList<>();
		this.UM_Bin_list = new LinkedList<>();
		this.total_cpu_workload_size = 0.0;
		this.total_mem_workload_size = 0.0;
		this.pre_time = 0;
	}

	/**
	 * remove the host from the related list
	 * 
	 * @param host
	 */
	private void remove_host(ROBP_Host host) {
		this.host_list.remove(host);
		this.L_Bin_list.remove(host);
		this.S_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.MO_Bin_list.remove(host);
		this.UM_Bin_list.remove(host);

	}

	/**
	 * clear the bin type from the related list
	 * 
	 * @param host
	 */
	private void clear_bin_type(ROBP_Host host) {
		this.L_Bin_list.remove(host);
		this.S_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.MO_Bin_list.remove(host);
		this.UM_Bin_list.remove(host);
	}

	/**
	 * update the bin type
	 * 
	 * @param host
	 */
	private void update_bin_type(ROBP_Host host) {
		Integer H_num = host.getH_num();
		Integer B_num = host.getB_num();
		Integer L_num = host.getL_num();
		Integer S_num = host.getS_num();
		Integer T_num = host.getT_num();
		Integer M_num = host.getM_num();

		// first clear the host type
		this.L_Bin_list.remove(host);
		this.S_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.MO_Bin_list.remove(host);
		this.UM_Bin_list.remove(host);

		if (L_num > 0 && H_num == 0 && B_num == 0) {
			this.L_Bin_list.add(host);
		}

		if (S_num > 0 && H_num == 0 && B_num == 0 && L_num == 0) {
			this.S_Bin_list.add(host);
		}

		if (S_num == 1 && H_num == 0 && B_num == 0 && L_num == 0 && T_num == 0 && M_num == 0) {
			this.US_Bin_list.add(host);
		}

		if (T_num > 0 && H_num == 0 && B_num == 0 && L_num == 0 && S_num == 0 && M_num == 0) {
			this.TO_Bin_list.add(host);
			if (T_num <= 2) {
				this.UT_Bin_list.add(host);
			}
		}

		if (M_num > 0 && H_num == 0 && B_num == 0 && L_num == 0 && S_num == 0 && T_num == 0) {
			this.MO_Bin_list.add(host);
			if (host.getCpu_utilization() <= 3.0 / 4.0) {
				this.UM_Bin_list.add(host);
			}
		}

		if (host.getVm_list().isEmpty()) {
			this.remove_host(host);
		}
	}

	/**
	 * move a vm_group from source host to the destination host
	 * 
	 * @param src_host
	 * @param des_host
	 * @throws Exception
	 */
	private void move_group(ROBP_Host src_host, ROBP_Host des_host) throws Exception {
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
	 * this method is used to fill a L-bin or B-bin, before using this method, you should use clear_bin_type method,
	 * after using this method you should use update_bin_type method
	 * 
	 * @param host
	 * @throws Exception
	 */
	private void fill(ROBP_Host host) throws Exception {
		while ((host.getCpu_utilization() <= 3.0 / 4.0) && ((!TO_Bin_list.isEmpty()) || (!MO_Bin_list.isEmpty()))) {
			Boolean find_T_item = false;
			VM T_item = null;
			ROBP_Host src_host = null;
			for (ROBP_Host T_host : TO_Bin_list) {
				for (String vm_id : T_host.getVm_list()) {
					VM current_vm = vm_table.get(vm_id);
					if (current_vm.getCpu_demand() <= (1 - host.getCpu_utilization())) {
						find_T_item = true;
						T_item = current_vm;
						src_host = T_host;
						break;
					}
				}
				if (find_T_item) {
					break;
				}
			}

			if (find_T_item) {
				src_host.delete_vm(T_item);
				host.add_vm(T_item);
				if (!src_host.getVm_list().isEmpty() && !UT_Bin_list.isEmpty() && !UT_Bin_list.contains(src_host)) {
					ROBP_Host UT_Bin = UT_Bin_list.getFirst();
					for (String vm_id : UT_Bin.getVm_list()) {
						VM current_vm = vm_table.get(vm_id);
						UT_Bin.delete_vm(current_vm);
						src_host.add_vm(current_vm);
						this.migration_num = this.migration_num + 1;
						if (src_host.getT_num() == 3) {
							break;
						}
					}
					this.update_bin_type(UT_Bin);
					if (UT_Bin.getVm_list().isEmpty()) {
						this.remove_host(UT_Bin);
					}
				}
				this.update_bin_type(src_host);
				this.migration_num = this.migration_num + 1;
			} else {
				if (!UM_Bin_list.isEmpty()) {
					ROBP_Host curren_bin = UM_Bin_list.getFirst();
					this.move_group(curren_bin, host);
					this.update_bin_type(curren_bin);
					this.migration_num = this.migration_num + 1;
				} else if (!MO_Bin_list.isEmpty()) {
					ROBP_Host current_bin = MO_Bin_list.getFirst();
					this.move_group(current_bin, host);
					this.update_bin_type(current_bin);
					this.migration_num = this.migration_num + 1;
				}
			}

			if (!find_T_item && MO_Bin_list.isEmpty()) {
				break;
			}
		}
	}

	/**
	 * This operation is used to merge two UM-bins or merge a S*-bin and a S’-bin, ensuring that at most one of them
	 * remains unfilled.
	 * 
	 * @param host1
	 * @param host2
	 * @throws Exception
	 */
	private void merge(ROBP_Host host1, ROBP_Host host2) throws Exception {
		// if B1 and B2 are UM-bin
		if (host1.getM_num() > 0 && host1.getH_num() == 0 && host1.getB_num() == 0 && host1.getL_num() == 0
				&& host1.getS_num() == 0 && host1.getT_num() == 0 && host1.getCpu_utilization() <= 3.0 / 4.0
				&& host2.getM_num() > 0 && host2.getH_num() == 0 && host2.getB_num() == 0 && host2.getL_num() == 0
				&& host2.getS_num() == 0 && host2.getT_num() == 0 && host2.getCpu_utilization() <= 3.0 / 4.0) {
			ROBP_Host max_host;
			ROBP_Host min_host;
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

		// if B1 and B2 are US-bin then
		if (host1.getS_num() > 0 && host1.getCpu_utilization() <= 3.0 / 4.0 && host2.getS_num() > 0
				&& host2.getCpu_utilization() <= 3.0 / 4.0) {
			ROBP_Host SO_Bin = null;
			ROBP_Host S_Bin = null;
			if (host1.getS_num() > 0 && host1.getH_num() == 0 && host1.getB_num() == 0 && host1.getL_num() == 0
					&& host1.getT_num() == 0 && host1.getM_num() == 0 && host2.getS_num() > 0 && host2.getH_num() == 0
					&& host2.getB_num() == 0 && host2.getL_num() == 0 && host2.getT_num() == 0 && host2.getM_num() == 0) {
				// if both host1 and host2 only have on S-item then
				VM S_item = vm_table.get(host1.getVm_list().get(0));
				host1.delete_vm(S_item);
				host2.add_vm(S_item);
				this.migration_num = this.migration_num + 1;
				this.fill(host2);
			} else {
				if (host1.getS_num() > 0 && host1.getH_num() == 0 && host1.getB_num() == 0 && host1.getL_num() == 0
						&& host1.getT_num() == 0 && host1.getM_num() == 0) {
					// if host1 only have on S-item then
					SO_Bin = host1;
					S_Bin = host2;
				} else {
					// if host2 only have on S-item then
					SO_Bin = host2;
					S_Bin = host1;
				}

				if (S_Bin.getT_num() > 0) { // if the host containing T-item we
											// have to move out this T-item
					VM T_item = null;
					for (String iterable_vm_id : S_Bin.getVm_list()) {
						VM current_item = vm_table.get(iterable_vm_id);
						if (current_item.getCpu_demand() <= 1.0 / 3.0 && current_item.getCpu_demand() > 1.0 / 4.0) {
							T_item = current_item;
							break;
						}
					}
					Boolean find_bin = false;
					ROBP_Host candidate_host = null;
					for (ROBP_Host iterable_host : this.host_list) {
						if ((T_item.getCpu_demand() <= 1 - iterable_host.getCpu_utilization())
								&& !this.MO_Bin_list.contains(iterable_host) && iterable_host != S_Bin
								&& iterable_host != SO_Bin) {
							candidate_host = iterable_host;
							find_bin = true;
							break;
						}
					}

					if (find_bin) {
						S_Bin.delete_vm(T_item);
						candidate_host.add_vm(T_item);
						this.update_bin_type(candidate_host);
						this.migration_num = this.migration_num + 1;
					} else {
						ROBP_Host new_host = new ROBP_Host();
						this.host_list.add(new_host);
						S_Bin.delete_vm(T_item);
						new_host.add_vm(T_item);
						this.update_bin_type(new_host);
						this.migration_num = this.migration_num + 1;
					}
					VM S_item = vm_table.get(SO_Bin.getVm_list().get(0));
					SO_Bin.delete_vm(S_item);
					S_Bin.add_vm(S_item);
					this.migration_num = this.migration_num + 1;
					this.fill(S_Bin);

				} else {
					while (S_Bin.getCpu_utilization() + SO_Bin.getCpu_utilization() > 1.0) {
						Boolean find_host = false;
						ROBP_Host candidate_host = null;
						for (ROBP_Host iterable_host : this.host_list) {
							if ((iterable_host.getCpu_utilization() < 3.0 / 4.0)
									&& !this.TO_Bin_list.contains(iterable_host) && iterable_host != S_Bin
									&& iterable_host != SO_Bin) {
								candidate_host = iterable_host;
								find_host = true;
								break;
							}
						}
						if (find_host) {
							this.move_group(S_Bin, candidate_host);
							this.update_bin_type(candidate_host);
							this.migration_num = this.migration_num + 1;
						} else {
							ROBP_Host new_host = new ROBP_Host();
							this.host_list.add(new_host);
							this.move_group(S_Bin, new_host);
							this.update_bin_type(new_host);
							this.migration_num = this.migration_num + 1;
						}
					}
					VM S_item = vm_table.get(SO_Bin.getVm_list().get(0));
					SO_Bin.delete_vm(S_item);
					S_Bin.add_vm(S_item);
					this.migration_num = this.migration_num + 1;
				}
			}

		}
	}

	/**
	 * This operation moves all the T-items and M-groups from the gap of a current host and distributes them among all
	 * the other hosts.
	 * 
	 * @param host
	 * @throws Exception
	 */
	private void movethegap(ROBP_Host host) throws Exception {
		ArrayList<String> vm_id_list = (ArrayList<String>) host.getVm_list().clone();

		// move out the M-groups
		ArrayList<ArrayList<String>> groups = host.get_group(vm_table);
		if (groups.size() > 0) {
			for (ArrayList<String> group : groups) {
				Double group_size = 0.0;
				for (String vm_id : group) {
					group_size = group_size + vm_table.get(vm_id).getCpu_demand();
				}
				Boolean find_host = false;
				for (ROBP_Host iterable_host : this.host_list) {
					if (((1.0 - iterable_host.getCpu_utilization()) >= group_size)
							&& !this.TO_Bin_list.contains(iterable_host) && iterable_host != host) {
						move_group(host, iterable_host);
						this.update_bin_type(iterable_host);
						this.migration_num = this.migration_num + 1;
						find_host = true;
						break;
					}
				}
				if (!find_host) {
					ROBP_Host new_host = new ROBP_Host();
					this.host_list.add(new_host);
					move_group(host, new_host);
					this.update_bin_type(new_host);
					this.migration_num = this.migration_num + 1;
				}
			}
		}

		// move out the T-items
		for (String vm_id : vm_id_list) {
			VM current_vm = vm_table.get(vm_id);
			if (current_vm.getCpu_demand() <= 1.0 / 3.0 && current_vm.getCpu_demand() > 1.0 / 4.0) { // T-item
				Boolean find_host = false;
				for (ROBP_Host iterable_host : this.host_list) {
					if (((1.0 - iterable_host.getCpu_utilization()) >= current_vm.getCpu_demand())
							&& !this.MO_Bin_list.contains(iterable_host) && iterable_host != host) {
						host.delete_vm(current_vm);
						iterable_host.add_vm(current_vm);
						this.update_bin_type(iterable_host);
						this.migration_num = this.migration_num + 1;
						find_host = true;
						break;
					}
				}
				if (!find_host) {
					ROBP_Host new_host = new ROBP_Host();
					this.host_list.add(new_host);
					host.delete_vm(current_vm);
					new_host.add_vm(current_vm);
					this.update_bin_type(new_host);
					this.migration_num = this.migration_num + 1;
				}
			}
		}
	}

	/**
	 * This operation insert an item into a suitable bin. This operation corresponds insert a VM into a existing PM.
	 * 
	 * @param vm
	 * @throws Exception
	 */
	private void insert(VM vm) throws Exception {
		if (vm.getCpu_demand() <= 1.0 && vm.getCpu_demand() > 3.0 / 4.0) { // H-item
			ROBP_Host new_host = new ROBP_Host();
			this.host_list.add(new_host);
			new_host.add_vm(vm);
			this.update_bin_type(new_host);
		}

		if (vm.getCpu_demand() <= 3.0 / 4.0 && vm.getCpu_demand() > 2.0 / 3.0) { // B-item
			ROBP_Host new_host = new ROBP_Host();
			this.host_list.add(new_host);
			new_host.add_vm(vm);
			this.fill(new_host);
			this.update_bin_type(new_host);
		}

		if (vm.getCpu_demand() <= 2.0 / 3.0 && vm.getCpu_demand() > 1.0 / 2.0) { // L-item
			ROBP_Host new_host = new ROBP_Host();
			this.host_list.add(new_host);
			new_host.add_vm(vm);
			Boolean find_S_item = false;
			if (!S_Bin_list.isEmpty()) {
				for (ROBP_Host iterable_host : S_Bin_list) {
					for (String vm_id : iterable_host.getVm_list()) {
						VM current_vm = vm_table.get(vm_id);
						if (current_vm.getCpu_demand() <= (1 - new_host.getCpu_utilization())
								&& current_vm.getCpu_demand() <= 1.0 / 2.0 && current_vm.getCpu_demand() > 1.0 / 3.0) {
							iterable_host.delete_vm(current_vm);
							new_host.add_vm(current_vm);
							this.migration_num = this.migration_num + 1;
							if (iterable_host.getS_num() > 0 && !US_Bin_list.isEmpty()
									&& !US_Bin_list.contains(iterable_host)) {
								ROBP_Host US_Bin = US_Bin_list.getFirst();
								this.merge(iterable_host, US_Bin);
								this.update_bin_type(iterable_host);
								this.update_bin_type(US_Bin);
								if (iterable_host.getVm_list().isEmpty()) {
									this.remove_host(iterable_host);
								}
								if (US_Bin.getVm_list().isEmpty()) {
									this.remove_host(US_Bin);
								}
							} else {
								this.movethegap(iterable_host);
								this.update_bin_type(iterable_host);
								if (iterable_host.getVm_list().isEmpty()) {
									this.remove_host(iterable_host);
								}
							}
							find_S_item = true;
							break;
						}
					}

					if (find_S_item) {
						break;
					}
				}
			}

			if (!find_S_item) {
				this.fill(new_host);
			}
			this.update_bin_type(new_host);
		}

		if (vm.getCpu_demand() <= 1.0 / 2.0 && vm.getCpu_demand() > 1.0 / 3.0) { // S-item
			Boolean find_L_item = false;
			if (!L_Bin_list.isEmpty()) {
				for (ROBP_Host iterable_host : L_Bin_list) {
					if (iterable_host.getS_num() == 0) {
						for (String vm_id : iterable_host.getVm_list()) {
							VM current_vm = vm_table.get(vm_id);
							if (current_vm.getCpu_demand() <= (1 - vm.getCpu_demand())
									&& current_vm.getCpu_demand() <= 2.0 / 3.0
									&& current_vm.getCpu_demand() > 1.0 / 2.0) {
								if (iterable_host.getCpu_utilization() + vm.getCpu_demand() > 1) {
									this.movethegap(iterable_host);
								}
								iterable_host.add_vm(vm);
								this.update_bin_type(iterable_host);
								find_L_item = true;
								break;
							}
						}
					}

					if (find_L_item) {
						break;
					}
				}
			}

			if (!find_L_item) {
				if (!US_Bin_list.isEmpty()) {
					ROBP_Host US_Bin = US_Bin_list.getFirst();
					US_Bin.add_vm(vm);
					this.fill(US_Bin);
					this.update_bin_type(US_Bin);
				} else {
					ROBP_Host new_host = new ROBP_Host();
					this.host_list.add(new_host);
					new_host.add_vm(vm);
					this.update_bin_type(new_host);
				}
			}

		}

		if (vm.getCpu_demand() <= 1.0 / 3.0 && vm.getCpu_demand() > 1.0 / 4.0) { // T-item
			Boolean find_host = false;
			for (ROBP_Host iterable_host : this.host_list) {
				if (((1.0 - iterable_host.getCpu_utilization()) >= vm.getCpu_demand())
						&& !this.MO_Bin_list.contains(iterable_host)) {
					iterable_host.add_vm(vm);
					this.update_bin_type(iterable_host);
					this.migration_num = this.migration_num + 1;
					find_host = true;
					break;
				}
			}
			if (!find_host) {
				ROBP_Host new_host = new ROBP_Host();
				this.host_list.add(new_host);
				new_host.add_vm(vm);
				this.update_bin_type(new_host);
				this.migration_num = this.migration_num + 1;
			}
		}

		if (vm.getCpu_demand() <= 1.0 / 4.0 && vm.getCpu_demand() > 0.0) { // M-item
			Boolean find_host = false;
			for (ROBP_Host iterable_host : this.host_list) {
				if (((1.0 - iterable_host.getCpu_utilization()) >= vm.getCpu_demand())
						&& !this.TO_Bin_list.contains(iterable_host)) {
					iterable_host.add_vm(vm);
					this.update_bin_type(iterable_host);
					this.migration_num = this.migration_num + 1;
					find_host = true;
					break;
				}
			}
			if (!find_host) {
				ROBP_Host new_host = new ROBP_Host();
				this.host_list.add(new_host);
				new_host.add_vm(vm);
				this.update_bin_type(new_host);
				this.migration_num = this.migration_num + 1;
			}
		}
	}

	/**
	 * this method is used to handle a overload host
	 * 
	 * @param host
	 * @throws Exception
	 */
	private void overload_handler(ROBP_Host host) throws Exception {
		VM vm_canbe_moved = null;
		for (String id : host.getVm_list()) {
			VM current_vm = vm_table.get(id);

			if (host.getCpu_utilization() - current_vm.getCpu_demand() <= 1.0) {
				vm_canbe_moved = current_vm;
			}
		}

		host.delete_vm(vm_canbe_moved);
		this.insert(vm_canbe_moved);
		this.update_bin_type(host);
		this.migration_num = this.migration_num + 1;
	}

	/**
	 * this method is used to handle a underload host
	 * 
	 * @param host
	 * @throws Exception
	 */
	private void underload_handler(ROBP_Host host) throws Exception {
		ArrayList<VM> B_list = new ArrayList<>();
		ArrayList<VM> L_list = new ArrayList<>();
		ArrayList<VM> S_list = new ArrayList<>();
		ArrayList<VM> T_list = new ArrayList<>();

		for (String id : host.getVm_list()) {
			VM current_vm = vm_table.get(id);

			if (current_vm.getCpu_demand() <= 3.0 / 4.0 && current_vm.getCpu_demand() > 2.0 / 3.0) { // B-item
				B_list.add(current_vm);
			}
			if (current_vm.getCpu_demand() <= 2.0 / 3.0 && current_vm.getCpu_demand() > 1.0 / 2.0) { // L-item
				L_list.add(current_vm);
			}
			if (current_vm.getCpu_demand() <= 1.0 / 2.0 && current_vm.getCpu_demand() > 1.0 / 3.0) { // S-item
				S_list.add(current_vm);
			}
			if (current_vm.getCpu_demand() <= 1.0 / 3.0 && current_vm.getCpu_demand() > 1.0 / 4.0) {// T-item
				T_list.add(current_vm);
			}
		}

		if (B_list.size() > 0) {
			this.fill(host);
			this.update_bin_type(host);
		} else if (L_list.size() > 0) {
			Boolean find_S_item = false;
			if (!S_Bin_list.isEmpty()) { // if ∃i ∈ S-item in some S-bin S, and
											// size(i) + size(cb) ≤ 1
				for (ROBP_Host iterable_host : S_Bin_list) {
					for (String vm_id : iterable_host.getVm_list()) {
						VM current_vm = vm_table.get(vm_id);
						if (current_vm.getCpu_demand() <= (1 - host.getCpu_utilization())
								&& current_vm.getCpu_demand() <= 1.0 / 2.0 && current_vm.getCpu_demand() > 1.0 / 3.0) {
							this.movethegap(host);
							iterable_host.delete_vm(current_vm);
							host.add_vm(current_vm);
							this.migration_num = this.migration_num + 1;
							if (iterable_host.getS_num() > 0 && !US_Bin_list.isEmpty()
									&& !US_Bin_list.contains(iterable_host)) {
								ROBP_Host US_Bin = US_Bin_list.getFirst();
								this.merge(iterable_host, US_Bin);
								this.update_bin_type(iterable_host);
								this.update_bin_type(US_Bin);
								if (iterable_host.getVm_list().isEmpty()) {
									this.remove_host(iterable_host);
								}
								if (US_Bin.getVm_list().isEmpty()) {
									this.remove_host(US_Bin);
								}
							} else {
								this.movethegap(iterable_host);
								this.update_bin_type(iterable_host);
								if (iterable_host.getVm_list().isEmpty()) {
									this.remove_host(iterable_host);
								}
							}
							find_S_item = true;
							break;
						}
					}

					if (find_S_item) {
						break;
					}
				}
			}

			if (!find_S_item) {
				this.fill(host);
			}
			this.update_bin_type(host);
		} else if (S_list.size() > 0 || T_list.size() > 0) {
			for (VM vm : S_list) {
				host.delete_vm(vm);
				this.insert(vm);
				this.update_bin_type(host);
				this.migration_num = this.migration_num + 1;
			}
			for (VM vm : T_list) {
				host.delete_vm(vm);
				this.insert(vm);
				this.update_bin_type(host);
				this.migration_num = this.migration_num + 1;
			}

			if (!host.getVm_list().isEmpty() && !UM_Bin_list.isEmpty() && !UM_Bin_list.contains(host)) {
				ROBP_Host UM_Bin = UM_Bin_list.getFirst();
				this.merge(host, UM_Bin);
				this.update_bin_type(host);
				this.update_bin_type(UM_Bin);
				if (UM_Bin.getVm_list().isEmpty()) {
					this.remove_host(UM_Bin);
				}
			}

		} else if (!host.getVm_list().isEmpty() && !UM_Bin_list.isEmpty() && !UM_Bin_list.contains(host)) {
			ROBP_Host UM_Bin = UM_Bin_list.getFirst();
			this.merge(host, UM_Bin);
			this.update_bin_type(host);
			this.update_bin_type(UM_Bin);
			if (UM_Bin.getVm_list().isEmpty()) {
				this.remove_host(UM_Bin);
			}
		}

		if (host.getVm_list().isEmpty()) {
			this.remove_host(host);
		}

	}

	@Override
	protected void process_submit_event(String[] event) throws Exception {
		this.migration_num = 0;
		String vm_id = event[1] + "-" + event[2];
		Double cpu_demand = Double.valueOf(event[6]);
		Double mem_demand = Double.valueOf(event[7]);
		if (!vm_table.containsKey(vm_id)) {
			this.total_vm_num = this.total_vm_num + 1;
			VM new_vm = new VM(vm_id, cpu_demand, mem_demand);
			vm_table.put(vm_id, new_vm);
			this.insert(new_vm);
			this.total_cpu_workload_size = this.total_cpu_workload_size + cpu_demand;
			this.total_mem_workload_size = this.total_mem_workload_size + mem_demand;
		}

	}

	@Override
	protected void process_update_event(String[] event) throws Exception {
		this.migration_num = 0;
		String vm_id = event[1] + "-" + event[2];
		Double cpu_demand = Double.valueOf(event[6]);
		Double mem_demand = Double.valueOf(event[7]);
		if (vm_table.containsKey(vm_id)) {
			Double original_cpu_demand = vm_table.get(vm_id).getCpu_demand();
			Double original_mem_demand = vm_table.get(vm_id).getMem_demand();
			VM current_vm = vm_table.get(vm_id);
			current_vm.update(cpu_demand, mem_demand);
			ROBP_Host deployed_host = (ROBP_Host) current_vm.getDeployed_host();
			// this.update_bin_type(deployed_host);
			if (deployed_host.getCpu_utilization() > this.overload_threshold) {
				this.clear_bin_type(deployed_host);
				this.overload_handler(deployed_host);
			}
			if (deployed_host.getCpu_utilization() < this.underload_threshold) {
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
	protected void process_finish_event(String[] event) throws Exception {
		this.migration_num = 0;
		String vm_id = event[1] + "-" + event[2];
		if (vm_table.containsKey(vm_id)) {
			VM finish_vm = vm_table.get(vm_id);
			ROBP_Host deployed_host = (ROBP_Host) finish_vm.getDeployed_host();
			deployed_host.delete_vm(finish_vm);

			// if after deletion of vm, this host become empty, just remove this
			// host from host list
			if (deployed_host.getVm_list().isEmpty()) {
				this.remove_host(deployed_host);
			} else {
				// this.update_bin_type(deployed_host);
				if (deployed_host.getCpu_utilization() <= this.underload_threshold) {
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
		long eventtime = Long.parseLong(event_time);
		if (event_type.equals("1")) {
			try {
				process_submit_event(event);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (event_type.equals("8")) {
			try {
				process_update_event(event);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (event_type.equals("4")) {
			try {
				process_finish_event(event);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

		// record host information
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
