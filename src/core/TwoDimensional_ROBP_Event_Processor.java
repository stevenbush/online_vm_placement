/**
 * This class is used to process every event by using method Relaxed Online Bin-Packing Model related algorithm for two-dimensional situation. My
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

public class TwoDimensional_ROBP_Event_Processor extends Abstract_Event_Processor {
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
	/** the Bins contain only one B-item */
	private LinkedList<ROBP_Host> BOne_Bin_list;
	/** the Bins contain L-item */
	private LinkedList<ROBP_Host> L_Bin_list;
	/** the Bins contain only one L-item */
	private LinkedList<ROBP_Host> LOne_Bin_list;
	/** the Bins contain only one L-item and without S_item */
	private LinkedList<ROBP_Host> LOne_withoutS_Bin_list;
	/** the Bins contain S-item and some T-items or M-items */
	private LinkedList<ROBP_Host> STM_Bin_list;
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
	/** these bins can be filled with T-item */
	private LinkedList<ROBP_Host> Bin_canbe_filled_T_list;
	/** these bins can be filled with M-item */
	private LinkedList<ROBP_Host> Bin_canbe_filled_M_list;
	/** the total cpu workload size */
	private Double total_cpu_workload_size;
	/** the total mem workload size */
	private Double total_mem_workload_size;
	/** the time to record the pre time */
	private long pre_time;

	public TwoDimensional_ROBP_Event_Processor(BufferedWriter writer, BufferedWriter hostwriter) {
		this.overload_threshold = 1.0;
		this.underload_threshold = 3.0 / 4.0;
		this.vm_table = new Hashtable<>();
		this.host_list = new ArrayList<ROBP_Host>();
		this.log_writer = writer;
		this.host_log_writer = hostwriter;
		this.migration_num = 0;
		this.total_vm_num = (long) 0;
		this.BOne_Bin_list = new LinkedList<>();
		this.L_Bin_list = new LinkedList<>();
		this.LOne_Bin_list = new LinkedList<>();
		this.LOne_withoutS_Bin_list = new LinkedList<>();
		this.STM_Bin_list = new LinkedList<>();
		this.US_Bin_list = new LinkedList<>();
		this.TO_Bin_list = new LinkedList<>();
		this.UT_Bin_list = new LinkedList<>();
		this.MO_Bin_list = new LinkedList<>();
		this.UM_Bin_list = new LinkedList<>();
		this.Bin_canbe_filled_T_list = new LinkedList<>();
		this.Bin_canbe_filled_M_list = new LinkedList<>();
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
		this.BOne_Bin_list.remove(host);
		this.L_Bin_list.remove(host);
		this.LOne_Bin_list.remove(host);
		this.LOne_withoutS_Bin_list.remove(host);
		this.STM_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.MO_Bin_list.remove(host);
		this.UM_Bin_list.remove(host);
		this.Bin_canbe_filled_T_list.remove(host);
		this.Bin_canbe_filled_M_list.remove(host);
	}

	/**
	 * clear the bin type from the related list
	 * 
	 * @param host
	 */
	private void clear_bin_type(ROBP_Host host) {
		this.BOne_Bin_list.remove(host);
		this.L_Bin_list.remove(host);
		this.LOne_Bin_list.remove(host);
		this.LOne_withoutS_Bin_list.remove(host);
		this.STM_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.MO_Bin_list.remove(host);
		this.UM_Bin_list.remove(host);
		this.Bin_canbe_filled_T_list.remove(host);
		this.Bin_canbe_filled_M_list.remove(host);
	}

	/**
	 * update the bin type
	 * 
	 * @param host
	 */
	private void update_bin_type(ROBP_Host host) {
		Integer H_num = host.getH_num_intwo();
		Integer B_num = host.getB_num_intwo();
		Integer L_num = host.getL_num_intwo();
		Integer S_num = host.getS_num_intwo();
		Integer T_num = host.getT_num_intwo();
		Integer M_num = host.getM_num_intwo();

		// first clear the host type
		this.BOne_Bin_list.remove(host);
		this.L_Bin_list.remove(host);
		this.LOne_Bin_list.remove(host);
		this.LOne_withoutS_Bin_list.remove(host);
		this.STM_Bin_list.remove(host);
		this.US_Bin_list.remove(host);
		this.TO_Bin_list.remove(host);
		this.UT_Bin_list.remove(host);
		this.MO_Bin_list.remove(host);
		this.UM_Bin_list.remove(host);
		this.Bin_canbe_filled_M_list.remove(host);
		this.Bin_canbe_filled_T_list.remove(host);

		if (B_num == 1 && L_num == 0 && H_num == 0 && S_num == 0) {
			this.BOne_Bin_list.add(host);
			this.Bin_canbe_filled_T_list.add(host);
			this.Bin_canbe_filled_M_list.add(host);
		}

		if (L_num > 0 && H_num == 0 && B_num == 0) {
			this.L_Bin_list.add(host);
			if (L_num == 1) {
				this.LOne_Bin_list.add(host);
				if (S_num == 0) {
					this.LOne_withoutS_Bin_list.add(host);
					this.Bin_canbe_filled_T_list.add(host);
					this.Bin_canbe_filled_M_list.add(host);
				}
			}
		}

		if (S_num > 0 && H_num == 0 && B_num == 0 && L_num == 0) {
			this.STM_Bin_list.add(host);
			if (S_num == 2) {
				this.Bin_canbe_filled_T_list.add(host);
				this.Bin_canbe_filled_M_list.add(host);
			}
		}

		if (S_num == 1 && H_num == 0 && B_num == 0 && L_num == 0 && T_num == 0 && M_num == 0) {
			this.US_Bin_list.add(host);
		}

		if (T_num > 0 && H_num == 0 && B_num == 0 && L_num == 0 && S_num == 0 && M_num == 0) {
			this.TO_Bin_list.add(host);
			if (T_num <= 2) {
				this.UT_Bin_list.add(host);
				this.Bin_canbe_filled_T_list.add(host);
			}
			if (T_num == 3) {
				this.Bin_canbe_filled_M_list.add(host);
			}

		}

		if (M_num > 0 && H_num == 0 && B_num == 0 && L_num == 0 && S_num == 0 && T_num == 0) {
			this.MO_Bin_list.add(host);
			if (host.getCpu_utilization() <= 3.0 / 4.0) {
				this.UM_Bin_list.add(host);
				this.Bin_canbe_filled_M_list.add(host);
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
		// System.out.println("move_group");
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
	 * this method is used to fill a L-bin or B-bin, before using this method, you should use clear_bin_type method,
	 * after using this method you should use update_bin_type method
	 * 
	 * @param host
	 * @throws Exception
	 */
	private void fill(ROBP_Host host) throws Exception {
		// System.out.println("fill");
		while ((host.getMax_uitilization() <= 3.0 / 4.0) && ((!TO_Bin_list.isEmpty()) || (!MO_Bin_list.isEmpty()))) {
			Boolean find_T_item = false;
			VM T_item = null;
			ROBP_Host src_host = null;
			for (ROBP_Host T_host : TO_Bin_list) {
				for (String vm_id : T_host.getVm_list()) {
					VM current_vm = vm_table.get(vm_id);
					if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= (1 - host
							.getMax_uitilization())) {
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
						if (src_host.getT_num_intwo() == 3) {
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
		// System.out.println("merge");
		// if B1 and B2 are UM-bin
		if (host1.getM_num_intwo() > 0 && host1.getH_num_intwo() == 0 && host1.getB_num_intwo() == 0
				&& host1.getL_num_intwo() == 0 && host1.getS_num_intwo() == 0 && host1.getT_num_intwo() == 0
				&& host1.getMax_uitilization() <= 3.0 / 4.0 && host2.getM_num_intwo() > 0
				&& host2.getH_num_intwo() == 0 && host2.getB_num_intwo() == 0 && host2.getL_num_intwo() == 0
				&& host2.getS_num_intwo() == 0 && host2.getT_num_intwo() == 0
				&& host2.getMax_uitilization() <= 3.0 / 4.0) {
			ROBP_Host max_host;
			ROBP_Host min_host;
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

		// if B1 and B2 are US-bin then
		if (host1.getS_num_intwo() > 0 && host1.getMax_uitilization() <= 3.0 / 4.0 && host2.getS_num_intwo() > 0
				&& host2.getMax_uitilization() <= 3.0 / 4.0) {
			ROBP_Host SO_Bin = null;
			ROBP_Host S_Bin = null;
			if (host1.getS_num_intwo() > 0 && host1.getH_num_intwo() == 0 && host1.getB_num_intwo() == 0
					&& host1.getL_num_intwo() == 0 && host1.getT_num_intwo() == 0 && host1.getM_num_intwo() == 0
					&& host2.getS_num_intwo() > 0 && host2.getH_num_intwo() == 0 && host2.getB_num_intwo() == 0
					&& host2.getL_num_intwo() == 0 && host2.getT_num_intwo() == 0 && host2.getM_num_intwo() == 0) {
				// if both host1 and host2 only have on S-item then
				VM S_item = vm_table.get(host1.getVm_list().get(0));
				host1.delete_vm(S_item);
				host2.add_vm(S_item);
				this.migration_num = this.migration_num + 1;
				this.fill(host2);
			} else {
				if (host1.getS_num_intwo() > 0 && host1.getH_num_intwo() == 0 && host1.getB_num_intwo() == 0
						&& host1.getL_num_intwo() == 0 && host1.getT_num_intwo() == 0 && host1.getM_num_intwo() == 0) {
					// if host1 only have on S-item then
					SO_Bin = host1;
					S_Bin = host2;
				} else {
					// if host2 only have on S-item then
					SO_Bin = host2;
					S_Bin = host1;
				}

				if (S_Bin.getT_num_intwo() > 0) { // if the host containing
													// T-item we have to move
													// out this T-item
					VM T_item = null;
					for (String iterable_vm_id : S_Bin.getVm_list()) {
						VM current_item = vm_table.get(iterable_vm_id);
						if (Math.max(current_item.getCpu_demand(), current_item.getMem_demand()) <= 1.0 / 3.0
								&& Math.max(current_item.getCpu_demand(), current_item.getMem_demand()) > 1.0 / 4.0) {
							T_item = current_item;
							break;
						}
					}
					Boolean find_bin = false;
					ROBP_Host candidate_host = null;
					for (ROBP_Host iterable_host : this.host_list) {
						if ((Math.max(T_item.getCpu_demand(), T_item.getMem_demand()) <= 1 - iterable_host
								.getMax_uitilization())
								&& !this.MO_Bin_list.contains(iterable_host)
								&& iterable_host != S_Bin && iterable_host != SO_Bin) {
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
					while (S_Bin.getMax_uitilization() + SO_Bin.getMax_uitilization() > 1.0) {
						Boolean find_host = false;
						ROBP_Host candidate_host = null;
						for (ROBP_Host iterable_host : this.host_list) {
							if ((iterable_host.getMax_uitilization() < 3.0 / 4.0)
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
		// System.out.println("movethegap");
		ArrayList<String> vm_id_list = (ArrayList<String>) host.getVm_list().clone();

		// move out the M-groups
		ArrayList<ArrayList<String>> groups = host.get_group_intwo(vm_table);
		if (groups.size() > 0) {
			for (ArrayList<String> group : groups) {
				Double group_size = 0.0;
				for (String vm_id : group) {
					VM current_vm = vm_table.get(vm_id);
					group_size = group_size + Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand());
				}
				Boolean find_host = false;
				for (ROBP_Host iterable_host : this.Bin_canbe_filled_M_list) {
					if (((1.0 - iterable_host.getMax_uitilization()) >= group_size) && iterable_host != host) {
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
			if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 1.0 / 3.0
					&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 4.0) { // T-item
				Boolean find_host = false;
				for (ROBP_Host iterable_host : this.Bin_canbe_filled_T_list) {
					if (((1.0 - iterable_host.getMax_uitilization()) >= Math.max(current_vm.getCpu_demand(),
							current_vm.getMem_demand()))
							&& iterable_host != host) {
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
	 * This operation fills a B-bin A with complementary L-item. If we find one of this kind of L-item then return true,
	 * otherwise return fales. Before using this method you should clear the host's type
	 * 
	 * @param host
	 * @return
	 * @throws Exception
	 */
	private Boolean fillwithcomp(ROBP_Host host) throws Exception {
		// System.out.println("fillwithcomp");
		Boolean find_L_item = false;
		if (host.getB_num_intwo() == 1 && host.getH_num_intwo() == 0 && host.getL_num_intwo() == 0
				&& host.getS_num_intwo() == 0 && host.getT_num_intwo() == 0 && host.getM_num_intwo() == 0) {
			VM current_B_item = vm_table.get(host.getVm_list().get(0));
			VM candidate_L_item = null;
			if (!LOne_Bin_list.isEmpty()) { // if there exists a L-bin
											// containing at most one L-item
				for (ROBP_Host iterable_host : LOne_Bin_list) {
					for (String iterable_id : iterable_host.getVm_list()) {
						VM current_vm = vm_table.get(iterable_id);
						// this is a L-item
						if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 2.0 / 3.0
								&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 2.0) {
							// if this L-item has a complementary relationship
							// with this B-item
							if (current_B_item.getCpu_demand() + current_vm.getCpu_demand() <= 1.0
									&& current_B_item.getMem_demand() + current_vm.getMem_demand() <= 1.0) {
								candidate_L_item = current_vm;
								find_L_item = true;
								break;
							}
						}
					}
					if (find_L_item) {
						break;
					}
				}

				if (find_L_item) {
					ROBP_Host candidate_L_host = (ROBP_Host) candidate_L_item.getDeployed_host();
					// if this L_bin contains S-item
					if (candidate_L_host.getS_num_intwo() > 0) {
						// this.movethegap(candidate_L_host);
						VM curren_S_item = null;
						// find out this S-item
						for (String iterable_id : candidate_L_host.getVm_list()) {
							VM current_vm = vm_table.get(iterable_id);
							if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 1.0 / 2.0
									&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 3.0) {
								curren_S_item = current_vm;
								break;
							}
						}

						Boolean find_L_bin = false;
						// try to find a L_bin contains at most one L-item and
						// without S-item to accommodate this S-item
						if (!LOne_withoutS_Bin_list.isEmpty()) {
							for (ROBP_Host iterable_host : LOne_withoutS_Bin_list) {
								if (iterable_host != candidate_L_host) {
									for (String iterable_id : iterable_host.getVm_list()) {
										VM current_vm = vm_table.get(iterable_id);
										// this is a L-item
										if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 2.0 / 3.0
												&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 2.0) {
											// if this S-item a complementary
											// relationship with this L-item
											if (Math.max(curren_S_item.getCpu_demand(), curren_S_item.getMem_demand())
													+ current_vm.getCpu_demand() <= 1.0
													&& Math.max(curren_S_item.getCpu_demand(),
															curren_S_item.getMem_demand())
															+ current_vm.getMem_demand() <= 1.0) {
												find_L_bin = true;
												break;
											}
										}
									}

									if (find_L_bin) {
										if (iterable_host.getMax_uitilization()
												+ Math.max(curren_S_item.getCpu_demand(), curren_S_item.getMem_demand()) > 1.0) {
											movethegap(iterable_host);
										}

										candidate_L_host.delete_vm(curren_S_item);
										iterable_host.add_vm(curren_S_item);
										this.migration_num = this.migration_num + 1;
										this.update_bin_type(iterable_host);
										break;
									}
								}
							}
						}

						if (!find_L_bin) {
							// if we didn't find a L_bin to accommodate this
							// S-item and there exists a S-bin with only
							// one S-item, than move this current_S_item to this
							// S-bin
							if (!US_Bin_list.isEmpty() && !US_Bin_list.contains(candidate_L_host)) {
								ROBP_Host US_Bin = US_Bin_list.getFirst();
								candidate_L_host.delete_vm(curren_S_item);
								US_Bin.add_vm(curren_S_item);
								this.migration_num = this.migration_num + 1;
								this.fill(US_Bin);
								this.update_bin_type(US_Bin);
							}
						}
					} else {
						movethegap(candidate_L_host);
					}
					// move this L-item to this B-bin
					candidate_L_host.delete_vm(candidate_L_item);
					// this.movethegap(host);
					host.add_vm(candidate_L_item);
					this.migration_num = this.migration_num + 1;
					this.update_bin_type(candidate_L_host);
					this.update_bin_type(host);
				}
			}
		}
		return find_L_item;
	}

	/**
	 * this method put a L-item into a B-bin or L-bin which can accommodate this L-item, if this operation succeed
	 * return true, otherwise return false.
	 * 
	 * @param L_item
	 * @return
	 * @throws Exception
	 */
	private Boolean putwithcomp(VM L_item) throws Exception {
		// System.out.println("putwithcomp");
		Boolean find_candidate_bin = false;
		// find if there are some B-bin can accommodate this L-item
		if (!BOne_Bin_list.isEmpty()) { // if there exists a B-bin containing at
										// most one B-item
			ROBP_Host candidate_B_bin = null;
			// find out the B-bin that can accommodate this L-item
			for (ROBP_Host iterable_host : BOne_Bin_list) {
				for (String iterable_id : iterable_host.getVm_list()) {
					VM current_B_item = vm_table.get(iterable_id);
					// this is a B-item
					if (Math.max(current_B_item.getCpu_demand(), current_B_item.getMem_demand()) <= 3.0 / 4.0
							&& Math.max(current_B_item.getCpu_demand(), current_B_item.getMem_demand()) > 2.0 / 3.0) {
						// if this B-item has a complementary relationship with
						// this L-item
						if (current_B_item.getCpu_demand() + L_item.getCpu_demand() <= 1.0
								&& current_B_item.getMem_demand() + L_item.getMem_demand() <= 1.0) {
							candidate_B_bin = iterable_host;
							find_candidate_bin = true;
							break;
						}
					}
				}
				if (find_candidate_bin) {
					break;
				}
			}

			if (find_candidate_bin) {
				if (Math.max(L_item.getCpu_demand() + candidate_B_bin.getCpu_utilization_intwo(),
						L_item.getMem_demand() + candidate_B_bin.getMem_utilization_intwo()) > 1.0) {
					this.movethegap(candidate_B_bin);
				}
				candidate_B_bin.add_vm(L_item);
				this.update_bin_type(candidate_B_bin);
			}
		}

		if (!LOne_Bin_list.isEmpty() && !find_candidate_bin) { // if there
																// exists a
																// L-bin
																// containing at
																// most one
																// L-item
			ROBP_Host candidate_L_bin = null;
			for (ROBP_Host iterable_host : LOne_Bin_list) {
				for (String iterable_id : iterable_host.getVm_list()) {
					VM current_L_item = vm_table.get(iterable_id);
					// this is a L-item
					if (Math.max(current_L_item.getCpu_demand(), current_L_item.getMem_demand()) <= 2.0 / 3.0
							&& Math.max(current_L_item.getCpu_demand(), current_L_item.getMem_demand()) > 1.0 / 2.0) {
						// if this L-item has a complementary relationship with
						// this B-item
						if (current_L_item.getCpu_demand() + L_item.getCpu_demand() <= 1.0
								&& current_L_item.getMem_demand() + L_item.getMem_demand() <= 1.0) {
							candidate_L_bin = iterable_host;
							find_candidate_bin = true;
							break;
						}
					}
				}
				if (find_candidate_bin) {
					break;
				}
			}

			if (find_candidate_bin) {
				// if the gap of this candidate L-bin is large than the input
				// L-item's size, we have to move out some
				// items from this candidate L-bin then add this L-item into
				// this L-bin
				if (Math.max(L_item.getCpu_demand() + candidate_L_bin.getCpu_utilization_intwo(),
						L_item.getMem_demand() + candidate_L_bin.getMem_utilization_intwo()) > 1.0) {
					if (candidate_L_bin.getS_num_intwo() > 0) {
						this.movethegap(candidate_L_bin);
						VM curren_S_item = null;
						// find out this S-item
						for (String iterable_id : candidate_L_bin.getVm_list()) {
							VM current_vm = vm_table.get(iterable_id);
							if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 1.0 / 2.0
									&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 3.0) {
								curren_S_item = current_vm;
								break;
							}
						}

						Boolean find_L_bin = false;
						// try to find a L_bin contains at most one L-item and
						// without S-item to accommodate this S-item
						if (!LOne_withoutS_Bin_list.isEmpty()) {
							for (ROBP_Host iterable_host : LOne_withoutS_Bin_list) {
								if (iterable_host != candidate_L_bin) {
									for (String iterable_id : iterable_host.getVm_list()) {
										VM current_vm = vm_table.get(iterable_id);
										// this is a L-item
										if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 2.0 / 3.0
												&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 2.0) {
											// if this S-item a complementary
											// relationship with this L-item
											if (Math.max(curren_S_item.getCpu_demand(), curren_S_item.getMem_demand())
													+ current_vm.getCpu_demand() <= 1.0
													&& Math.max(curren_S_item.getCpu_demand(),
															curren_S_item.getMem_demand())
															+ current_vm.getMem_demand() <= 1.0) {
												find_L_bin = true;
												break;
											}
										}
									}

									if (find_L_bin) {
										if (iterable_host.getMax_uitilization()
												+ Math.max(curren_S_item.getCpu_demand(), curren_S_item.getMem_demand()) > 1.0) {
											movethegap(iterable_host);
										}
										candidate_L_bin.delete_vm(curren_S_item);
										iterable_host.add_vm(curren_S_item);
										this.migration_num = this.migration_num + 1;
										this.update_bin_type(iterable_host);
										break;
									}
								}
							}
						}

						if (!find_L_bin) {
							if (!US_Bin_list.isEmpty()) {
								ROBP_Host US_Bin = US_Bin_list.getFirst();
								candidate_L_bin.delete_vm(curren_S_item);
								US_Bin.add_vm(curren_S_item);
								this.migration_num = this.migration_num + 1;
								this.fill(US_Bin);
								this.update_bin_type(US_Bin);
							} else {
								ROBP_Host new_bin = new ROBP_Host();
								host_list.add(new_bin);
								candidate_L_bin.delete_vm(curren_S_item);
								new_bin.add_vm(curren_S_item);
								this.migration_num = this.migration_num + 1;
								this.update_bin_type(new_bin);
							}
						}

					} else {
						this.movethegap(candidate_L_bin);
					}
				}
				candidate_L_bin.add_vm(L_item);
				this.update_bin_type(candidate_L_bin);
			}

		}
		return find_candidate_bin;
	}

	/**
	 * This operation insert an item into a suitable bin. This operation corresponds insert a VM into a existing PM.
	 * 
	 * @param vm
	 * @throws Exception
	 */
	private void insert(VM vm) throws Exception {
		// System.out.println("insert");
		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 1.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 3.0 / 4.0) { // H-item
			ROBP_Host new_host = new ROBP_Host();
			this.host_list.add(new_host);
			new_host.add_vm(vm);
			this.update_bin_type(new_host);
		}

		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 3.0 / 4.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 2.0 / 3.0) { // B-item
			ROBP_Host new_host = new ROBP_Host();
			this.host_list.add(new_host);
			new_host.add_vm(vm);
			Boolean flag = fillwithcomp(new_host);
			if (!flag) {
				fill(new_host);
			}
			this.update_bin_type(new_host);
		}

		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 2.0 / 3.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 1.0 / 2.0) { // L-item
			Boolean flag = putwithcomp(vm);
			if (!flag) {
				ROBP_Host new_host = new ROBP_Host();
				this.host_list.add(new_host);
				new_host.add_vm(vm);
				Boolean find_S_item = false;
				if (!STM_Bin_list.isEmpty()) {
					for (ROBP_Host iterable_host : STM_Bin_list) {
						for (String vm_id : iterable_host.getVm_list()) {
							VM current_vm = vm_table.get(vm_id);
							if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= (1 - new_host
									.getMax_uitilization())
									&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 1.0 / 2.0
									&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 3.0) {
								iterable_host.delete_vm(current_vm);
								new_host.add_vm(current_vm);
								this.migration_num = this.migration_num + 1;
								if (iterable_host.getS_num_intwo() > 0 && !US_Bin_list.isEmpty()
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
		}

		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 1.0 / 2.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 1.0 / 3.0) { // S-item
			Boolean find_L_item = false;
			// try to find a L_bin contains at most one L-item and without
			// S-item to accommodate this S-item
			if (!LOne_withoutS_Bin_list.isEmpty()) {
				for (ROBP_Host iterable_host : LOne_withoutS_Bin_list) {
					if (iterable_host.getS_num_intwo() == 0) {
						for (String vm_id : iterable_host.getVm_list()) {
							VM current_vm = vm_table.get(vm_id);
							if (current_vm.getCpu_demand() <= (1 - Math.max(vm.getCpu_demand(), vm.getMem_demand()))
									&& current_vm.getMem_demand() <= (1 - Math.max(vm.getCpu_demand(),
											vm.getMem_demand()))
									&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 2.0 / 3.0
									&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 2.0) {
								if (iterable_host.getMax_uitilization()
										+ Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 1) {
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

		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 1.0 / 3.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 1.0 / 4.0) { // T-item
			Boolean find_host = false;
			for (ROBP_Host iterable_host : this.host_list) {
				if (((1.0 - iterable_host.getMax_uitilization()) >= Math.max(vm.getCpu_demand(), vm.getMem_demand()))
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

		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 1.0 / 4.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 0.0) { // M-item
			Boolean find_host = false;
			for (ROBP_Host iterable_host : this.host_list) {
				if (((1.0 - iterable_host.getMax_uitilization()) >= Math.max(vm.getCpu_demand(), vm.getMem_demand()))
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
	 * find B-item and L-item with complementary relationship from these two list, if can not find, just return empty
	 * list.
	 * 
	 * @param B_list
	 * @param L_list
	 * @return
	 */
	private ArrayList<VM> find_comp_BandL(ArrayList<VM> B_list, ArrayList<VM> L_list) {
		ArrayList<VM> comp_vm_list = new ArrayList<>();
		Boolean find_flag = false;
		for (VM vm_B : B_list) {
			for (VM vm_L : L_list) {
				if (vm_B.getCpu_demand() + vm_L.getCpu_demand() <= 1.0
						&& vm_B.getMem_demand() + vm_L.getMem_demand() <= 1.0) {
					comp_vm_list.add(vm_L);
					comp_vm_list.add(vm_B);
					find_flag = true;
					break;
				}
			}
			if (find_flag) {
				break;
			}
		}
		return comp_vm_list;
	}

	/**
	 * find two L-item with complementary relationship from these list, if can not find, just return empty list.
	 * 
	 * @param L_list
	 * @return
	 */
	private ArrayList<VM> find_comp_L_items(ArrayList<VM> L_list) {
		ArrayList<VM> comp_vm_list = new ArrayList<>();
		Boolean find_flag = false;
		for (VM vm1 : comp_vm_list) {
			for (VM vm2 : comp_vm_list) {
				if (vm1 != vm2) {
					if (vm1.getCpu_demand() + vm2.getCpu_demand() <= 1.0
							&& vm1.getMem_demand() + vm2.getMem_demand() <= 1.0) {
						comp_vm_list.add(vm1);
						comp_vm_list.add(vm2);
						find_flag = true;
						break;
					}
				}
			}
			if (find_flag) {
				break;
			}
		}
		return comp_vm_list;
	}

	/**
	 * this method is used to handle a overload host
	 * 
	 * @param host
	 * @throws Exception
	 */
	private void overload_handler(ROBP_Host host) throws Exception {
		// System.out.println("overload_handler");
		VM vm_canbe_moved = null;
		for (String id : host.getVm_list()) {
			VM current_vm = vm_table.get(id);

			if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 1.0 / 2.0) {
				if (host.getMax_uitilization() - Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 1.0) {
					vm_canbe_moved = current_vm;
				}
			} else {
				if (host.getCpu_utilization_intwo() - current_vm.getCpu_demand() <= 1.0
						&& host.getMem_utilization_intwo() - current_vm.getMem_demand() <= 1.0) {
					vm_canbe_moved = current_vm;
				}
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
		// System.out.println("underload_handler");
		ArrayList<VM> B_list = new ArrayList<>();
		ArrayList<VM> L_list = new ArrayList<>();
		ArrayList<VM> S_list = new ArrayList<>();
		ArrayList<VM> T_list = new ArrayList<>();

		for (String id : host.getVm_list()) {
			VM current_vm = vm_table.get(id);

			if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 3.0 / 4.0
					&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 2.0 / 3.0) { // B-item
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
					&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 4.0) {// T-item
				T_list.add(current_vm);
			}
		}

		if (B_list.size() > 0) {
			VM current_B_item = B_list.get(0);
			Boolean find_L_item = false;
			if (!LOne_Bin_list.isEmpty()) { // if there exists a L-bin
											// containing at most one L-item
				for (ROBP_Host iterable_host : LOne_Bin_list) {
					for (String iterable_id : iterable_host.getVm_list()) {
						VM current_vm = vm_table.get(iterable_id);
						// this is a L-item
						if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 2.0 / 3.0
								&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 2.0) {
							// if this L-item has a complementary relationship
							// with this B-item
							if (current_B_item.getCpu_demand() + current_vm.getCpu_demand() <= 1.0
									&& current_B_item.getMem_demand() + current_vm.getMem_demand() <= 1.0) {
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

			if (find_L_item) {
				this.movethegap(host);
				this.fillwithcomp(host);
			} else {
				this.fill(host);
			}
			this.update_bin_type(host);
		} else if (L_list.size() > 0) {
			Boolean find_S_item = false;
			if (!STM_Bin_list.isEmpty()) { // if ∃i ∈ S-item in some S-bin S,
											// and size(i) + size(cb) ≤ 1
				for (ROBP_Host iterable_host : STM_Bin_list) {
					for (String vm_id : iterable_host.getVm_list()) {
						VM current_vm = vm_table.get(vm_id);
						if (Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= (1 - host
								.getMax_uitilization())
								&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= 1.0 / 2.0
								&& Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) > 1.0 / 3.0) {
							this.movethegap(host);
							iterable_host.delete_vm(current_vm);
							host.add_vm(current_vm);
							this.migration_num = this.migration_num + 1;
							if (iterable_host.getS_num_intwo() > 0 && !US_Bin_list.isEmpty()
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
				Boolean find_candidate_bin = false;
				VM L_item = L_list.get(0);
				// find if there are some B-bin can accommodate this L-item
				if (!BOne_Bin_list.isEmpty()) { // if there exists a B-bin
												// containing at most one B-item
					// find out the B-bin that can accommodate this L-item
					for (ROBP_Host iterable_host : BOne_Bin_list) {
						for (String iterable_id : iterable_host.getVm_list()) {
							VM current_B_item = vm_table.get(iterable_id);
							// this is a B-item
							if (Math.max(current_B_item.getCpu_demand(), current_B_item.getMem_demand()) <= 3.0 / 4.0
									&& Math.max(current_B_item.getCpu_demand(), current_B_item.getMem_demand()) > 2.0 / 3.0) {
								// if this B-item has a complementary
								// relationship with this L-item
								if (current_B_item.getCpu_demand() + L_item.getCpu_demand() <= 1.0
										&& current_B_item.getMem_demand() + L_item.getMem_demand() <= 1.0) {
									find_candidate_bin = true;
									break;
								}
							}
						}
						if (find_candidate_bin) {
							break;
						}
					}
				}

				if (!LOne_Bin_list.isEmpty() && !find_candidate_bin) { // if
																		// there
																		// exists
																		// a
																		// L-bin
																		// containing
																		// at
																		// most
																		// one
																		// L-item
					for (ROBP_Host iterable_host : LOne_Bin_list) {
						for (String iterable_id : iterable_host.getVm_list()) {
							VM current_L_item = vm_table.get(iterable_id);
							// this is a L-item
							if (Math.max(current_L_item.getCpu_demand(), current_L_item.getMem_demand()) <= 2.0 / 3.0
									&& Math.max(current_L_item.getCpu_demand(), current_L_item.getMem_demand()) > 1.0 / 2.0) {
								// if this L-item has a complementary
								// relationship with this B-item
								if (current_L_item.getCpu_demand() + L_item.getCpu_demand() <= 1.0
										&& current_L_item.getMem_demand() + L_item.getMem_demand() <= 1.0) {
									find_candidate_bin = true;
									break;
								}
							}
						}
						if (find_candidate_bin) {
							break;
						}
					}
				}

				if (find_candidate_bin) {
					this.movethegap(host);
					host.delete_vm(L_item);
					insert(L_item);
					this.migration_num = this.migration_num + 1;
				} else {
					this.fill(host);
				}
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
			ROBP_Host deployed_host = (ROBP_Host) current_vm.getDeployed_host();
			current_vm.update(cpu_demand, mem_demand);
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
		long eventtime = Long.parseLong(event_time);
		if (event_type.equals("1")) {
			try {
				process_submit_event(event);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
		}

		if (event_type.equals("8")) {
			try {
				process_update_event(event);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}
		}

		if (event_type.equals("4")) {
			try {
				process_finish_event(event);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
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
					this.host_log_writer.write(eventtime + ":" + host_list.get(0).getCpu_utilization() + "~"
							+ host_list.get(0).getMem_utilization());

					for (int i = 1; i < host_list.size(); i++) {
						this.host_log_writer.write("," + host_list.get(i).getCpu_utilization() + "~"
								+ host_list.get(i).getMem_utilization());
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
