/**
 * this class is used to represent a physic host using by ORA related algorithm. L.Eyraud-Dubois,H.Larcheveˆqueetal.,
 * "Optimizingresourceallocation while handling sla violations in cloud computing platforms," in IPDPS- 27th IEEE
 * International Parallel & Distributed Processing Symposium, 2013.
 * 
 * @author jiyuanshi(shijiyuan.seu@gmail.com)
 * @since 2013-12-13
 * @version V1.0
 */
package core;

import java.util.ArrayList;
import java.util.Hashtable;

public class ORA_Host extends Abstract_Host {

	/** the threshold of group operation */
	private Double group_threshold;
	/** these vms that can be grouped */
	private ArrayList<String> vms_canbe_grouped;
	/** these vms that can be grouped in two dimensional situation */
	private ArrayList<String> vms_canbe_grouped_intwo;
	/** the number of B_items */
	private Integer B_num;
	/** the number of L_items */
	private Integer L_num;
	/** the number of S_items */
	private Integer S_num;
	/** the number of T_items */
	private Integer T_num;
	/** the number of B_items considering two dimensional */
	private Integer B_num_intwo;
	/** the number of L_items considering two dimensional */
	private Integer L_num_intwo;
	/** the number of S_items considering two dimensional */
	private Integer S_num_intwo;
	/** the number of T_items considering two dimensional */
	private Integer T_num_intwo;

	public ORA_Host() {
		super();
		this.cpu_utilization = 0.0;
		this.mem_utilization = 0.0;
		this.max_uitilization = 0.0;
		this.cpu_gap = 1.0;
		this.mem_gap = 1.0;
		this.max_gap = 1.0;
		this.vm_list = new ArrayList<>();
		this.group_threshold = 1.0 / 3.0;
		this.vms_canbe_grouped = new ArrayList<>();
		this.vms_canbe_grouped_intwo = new ArrayList<>();
		this.B_num = 0;
		this.L_num = 0;
		this.S_num = 0;
		this.T_num = 0;
		this.B_num_intwo = 0;
		this.L_num_intwo = 0;
		this.S_num_intwo = 0;
		this.T_num_intwo = 0;
	}

	/**
	 * this method is used to add a vm to this host, and if this vm can be group then put it into the corresponding list
	 * 
	 * @param vm
	 */
	@Override
	public void add_vm(VM vm) {
		vm_list.add(vm.getVm_id());
		vm.setDeployed_host(this);
		cpu_utilization = cpu_utilization + vm.getCpu_demand();
		mem_utilization = mem_utilization + vm.getMem_demand();
		max_uitilization = max_uitilization + Math.max(vm.getCpu_demand(), vm.getMem_demand());
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;
		max_gap = 1 - max_uitilization;

		// if this vm canbe grouped, them put it into the corresponding list
		if (vm.getCpu_demand() <= this.group_threshold) {
			this.vms_canbe_grouped.add(vm.getVm_id());
			if (vm.getMem_demand() <= this.group_threshold) {
				this.vms_canbe_grouped_intwo.add(vm.getVm_id());
			}
		}

		// update the item type counter
		if (vm.getCpu_demand() <= 1.0 && vm.getCpu_demand() > 2.0 / 3.0) { // B-item
			this.B_num = this.B_num + 1;
		}
		if (vm.getCpu_demand() <= 2.0 / 3.0 && vm.getCpu_demand() > 1.0 / 2.0) { // L-item
			this.L_num = this.L_num + 1;
		}
		if (vm.getCpu_demand() <= 1.0 / 2.0 && vm.getCpu_demand() > 1.0 / 3.0) { // S-item
			this.S_num = this.S_num + 1;
		}
		if (vm.getCpu_demand() <= 1.0 / 3.0 && vm.getCpu_demand() > 0) { // T-item
			this.T_num = this.T_num + 1;
		}

		// update the item type counter in two dimensional situation
		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 1.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 2.0 / 3.0) { // B-item
			this.B_num_intwo = this.B_num_intwo + 1;
		}
		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 2.0 / 3.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 1.0 / 2.0) { // L-item
			this.L_num_intwo = this.L_num_intwo + 1;
		}
		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 1.0 / 2.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 1.0 / 3.0) { // S-item
			this.S_num_intwo = this.S_num_intwo + 1;
		}
		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 1.0 / 3.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 0) { // T-item
			this.T_num_intwo = this.T_num_intwo + 1;
		}

	}

	/**
	 * this method is used to delete a vm from this host, and if this vm can be group then delete it from the
	 * corresponding list
	 * 
	 * @param vm
	 */
	@Override
	public void delete_vm(VM vm) {
		vm_list.remove(vm.getVm_id());
		vm.emptyDeployed_host();
		cpu_utilization = cpu_utilization - vm.getCpu_demand();
		mem_utilization = mem_utilization - vm.getMem_demand();
		max_uitilization = max_uitilization - Math.max(vm.getCpu_demand(), vm.getMem_demand());
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;
		max_gap = 1 - max_uitilization;

		// if this vm can be grouped, them remove it from the corresponding list
		if (vm.getCpu_demand() <= this.group_threshold) {
			this.vms_canbe_grouped.remove(vm.getVm_id());
			if (vm.getMem_demand() <= this.group_threshold) {
				this.vms_canbe_grouped_intwo.remove(vm.getVm_id());
			}
		}

		// update the item type counter
		if (vm.getCpu_demand() <= 1.0 && vm.getCpu_demand() > 2.0 / 3.0) { // B-item
			this.B_num = this.B_num - 1;
		}
		if (vm.getCpu_demand() <= 2.0 / 3.0 && vm.getCpu_demand() > 1.0 / 2.0) { // L-item
			this.L_num = this.L_num - 1;
		}
		if (vm.getCpu_demand() <= 1.0 / 2.0 && vm.getCpu_demand() > 1.0 / 3.0) { // S-item
			this.S_num = this.S_num - 1;
		}
		if (vm.getCpu_demand() <= 1.0 / 3.0 && vm.getCpu_demand() > 0) { // T-item
			this.T_num = this.T_num - 1;
		}

		// update the item type counter in two dimensional situation
		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 1.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 2.0 / 3.0) { // B-item
			this.B_num_intwo = this.B_num_intwo - 1;
		}
		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 2.0 / 3.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 1.0 / 2.0) { // L-item
			this.L_num_intwo = this.L_num_intwo - 1;
		}
		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 1.0 / 2.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 1.0 / 3.0) { // S-item
			this.S_num_intwo = this.S_num_intwo - 1;
		}
		if (Math.max(vm.getCpu_demand(), vm.getMem_demand()) <= 1.0 / 3.0
				&& Math.max(vm.getCpu_demand(), vm.getMem_demand()) > 0) { // T-item
			this.T_num_intwo = this.T_num_intwo - 1;
		}

		if (this.vm_list.isEmpty()) {
			cpu_utilization = 0.0;
			mem_utilization = 0.0;
			max_uitilization = 0.0;
			cpu_gap = 1.0;
			mem_gap = 1.0;
			max_gap = 1.0;
			this.B_num = 0;
			this.L_num = 0;
			this.S_num = 0;
			this.T_num = 0;
			this.B_num_intwo = 0;
			this.L_num_intwo = 0;
			this.S_num_intwo = 0;
			this.T_num_intwo = 0;
			this.vms_canbe_grouped.clear();
			this.vms_canbe_grouped_intwo.clear();
		}

	}

	/**
	 * this methond is used to update the vm's resource demand deployed in this host
	 * 
	 * @param vm
	 * @param original_cpu
	 * @param original_mem
	 * @param new_cpu
	 * @param new_mem
	 */
	@Override
	public void update_vm_demand(String vm_id, Double original_cpu, Double original_mem, Double new_cpu, Double new_mem) {
		cpu_utilization = cpu_utilization - original_cpu + new_cpu;
		mem_utilization = mem_utilization - original_mem + new_mem;
		max_uitilization = max_uitilization - Math.max(original_cpu, original_mem) + Math.max(new_cpu, new_mem);
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;
		max_gap = 1 - max_uitilization;

		if (original_cpu <= this.group_threshold) {
			this.vms_canbe_grouped.remove(vm_id);
			if (original_mem <= this.group_threshold) {
				this.vms_canbe_grouped_intwo.remove(vm_id);
			}
		}

		if (new_cpu <= this.group_threshold) {
			this.vms_canbe_grouped.add(vm_id);
			if (new_mem <= this.group_threshold) {
				this.vms_canbe_grouped_intwo.add(vm_id);
			}
		}

		// clear the original item type counter
		if (original_cpu <= 1.0 && original_cpu > 2.0 / 3.0) { // B-item
			this.B_num = this.B_num - 1;
		}
		if (original_cpu <= 2.0 / 3.0 && original_cpu > 1.0 / 2.0) { // L-item
			this.L_num = this.L_num - 1;
		}
		if (original_cpu <= 1.0 / 2.0 && original_cpu > 1.0 / 3.0) { // S-item
			this.S_num = this.S_num - 1;
		}
		if (original_cpu <= 1.0 / 3.0 && original_cpu > 0) { // T-item
			this.T_num = this.T_num - 1;
		}

		// clear the original item type counter in two dimensional situation
		if (Math.max(original_cpu, original_mem) <= 1.0 && Math.max(original_cpu, original_mem) > 2.0 / 3.0) { // B-item
			this.B_num_intwo = this.B_num_intwo - 1;
		}
		if (Math.max(original_cpu, original_mem) <= 2.0 / 3.0 && Math.max(original_cpu, original_mem) > 1.0 / 2.0) { // L-item
			this.L_num_intwo = this.L_num_intwo - 1;
		}
		if (Math.max(original_cpu, original_mem) <= 1.0 / 2.0 && Math.max(original_cpu, original_mem) > 1.0 / 3.0) { // S-item
			this.S_num_intwo = this.S_num_intwo - 1;
		}
		if (Math.max(original_cpu, original_mem) <= 1.0 / 3.0 && Math.max(original_cpu, original_mem) > 0) { // T-item
			this.T_num_intwo = this.T_num_intwo - 1;
		}

		// update the item type counter
		if (new_cpu <= 1.0 && new_cpu > 2.0 / 3.0) { // B-item
			this.B_num = this.B_num + 1;
		}
		if (new_cpu <= 2.0 / 3.0 && new_cpu > 1.0 / 2.0) { // L-item
			this.L_num = this.L_num + 1;
		}
		if (new_cpu <= 1.0 / 2.0 && new_cpu > 1.0 / 3.0) { // S-item
			this.S_num = this.S_num + 1;
		}
		if (new_cpu <= 1.0 / 3.0 && new_cpu > 0) { // T-item
			this.T_num = this.T_num + 1;
		}

		// update the item type counter in two dimensional situation
		if (Math.max(new_cpu, new_mem) <= 1.0 && Math.max(new_cpu, new_mem) > 2.0 / 3.0) { // B-item
			this.B_num_intwo = this.B_num_intwo + 1;
		}
		if (Math.max(new_cpu, new_mem) <= 2.0 / 3.0 && Math.max(new_cpu, new_mem) > 1.0 / 2.0) { // L-item
			this.L_num_intwo = this.L_num_intwo + 1;
		}
		if (Math.max(new_cpu, new_mem) <= 1.0 / 2.0 && Math.max(new_cpu, new_mem) > 1.0 / 3.0) { // S-item
			this.S_num_intwo = this.S_num_intwo + 1;
		}
		if (Math.max(new_cpu, new_mem) <= 1.0 / 3.0 && Math.max(new_cpu, new_mem) > 0) { // T-item
			this.T_num_intwo = this.T_num_intwo + 1;
		}

	}

	/**
	 * get the group list in this host
	 * 
	 * @param vm_table
	 * @return
	 */
	public ArrayList<ArrayList<String>> get_group(Hashtable<String, VM> vm_table) {
		ArrayList<ArrayList<String>> group_list = new ArrayList<>();
		ArrayList<String> tmp_vm_list = (ArrayList<String>) this.vms_canbe_grouped.clone();
		while (!tmp_vm_list.isEmpty()) {
			ArrayList<String> group = new ArrayList<>();
			Double group_size = 0.0;
			for (String vm_id : tmp_vm_list) {
				VM current_vm = vm_table.get(vm_id);
				if (current_vm.getCpu_demand() + group_size <= group_threshold) {
					group.add(vm_id);
					group_size = group_size + current_vm.getCpu_demand();
				}
			}
			tmp_vm_list.removeAll(group);
			group_list.add(group);
		}
		return group_list;
	}

	/**
	 * get the group list in this host in two dimensional situation
	 * 
	 * @param vm_table
	 * @return
	 */
	public ArrayList<ArrayList<String>> get_group_intwo(Hashtable<String, VM> vm_table) {
		ArrayList<ArrayList<String>> group_list = new ArrayList<>();
		ArrayList<String> tmp_vm_list = (ArrayList<String>) this.vms_canbe_grouped_intwo.clone();

		while (!tmp_vm_list.isEmpty()) {
			ArrayList<String> group = new ArrayList<>();
			Double group_max_size = 0.0;
			for (String vm_id : tmp_vm_list) {
				VM current_vm = vm_table.get(vm_id);
				if (group_max_size + Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand()) <= group_threshold) {
					group.add(vm_id);
					group_max_size = group_max_size + Math.max(current_vm.getCpu_demand(), current_vm.getMem_demand());
				}
			}
			tmp_vm_list.removeAll(group);
			group_list.add(group);
		}
		return group_list;
	}

	public Integer getB_num() {
		return B_num;
	}

	public void setB_num(Integer b_num) {
		B_num = b_num;
	}

	public Integer getL_num() {
		return L_num;
	}

	public void setL_num(Integer l_num) {
		L_num = l_num;
	}

	public Integer getS_num() {
		return S_num;
	}

	public void setS_num(Integer s_num) {
		S_num = s_num;
	}

	public Integer getT_num() {
		return T_num;
	}

	public void setT_num(Integer t_num) {
		T_num = t_num;
	}

	public Integer getB_num_intwo() {
		return B_num_intwo;
	}

	public Integer getL_num_intwo() {
		return L_num_intwo;
	}

	public Integer getS_num_intwo() {
		return S_num_intwo;
	}

	public Integer getT_num_intwo() {
		return T_num_intwo;
	}

	public ArrayList<String> getVms_canbe_grouped() {
		return vms_canbe_grouped;
	}

	public ArrayList<String> getVms_canbe_grouped_intwo() {
		return vms_canbe_grouped_intwo;
	}

}
