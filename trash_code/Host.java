/**
 * this class is used to represent a physic host
 * 
 * @author jiyuanshi(shijiyuan.seu@gmail.com)
 * @since 2013-12-13
 * @version V1.0
 */
package core;

import java.util.ArrayList;
import java.util.Hashtable;

public class Host {

	/** the utilization of cpu resource */
	private Double cpu_utilization;
	/** the utilization of mem resource */
	private Double mem_utilization;
	/** the gap of cpu resource */
	private Double cpu_gap;
	/** the gap of mem resource */
	private Double mem_gap;
	/** the vm putted in this host */
	private ArrayList<String> vm_list;
	/** these vms that can be grouped */
	public ArrayList<String> vms_canbe_grouped;
	/** these vms that can be grouped in two dimensional situation */
	public ArrayList<String> vms_canbe_grouped_two_dimensional;
	/** the vm groups in this host */
	private ArrayList<ArrayList<String>> group_list;
	/** the vm groups in this host in two dimensional */
	private ArrayList<ArrayList<String>> group_list_two_dimensional;

	public Host() {
		super();
		this.cpu_utilization = 0.0;
		this.mem_utilization = 0.0;
		this.cpu_gap = 1.0;
		this.mem_gap = 1.0;
		this.vm_list = new ArrayList<>();
		this.group_list = new ArrayList<>();
		this.group_list_two_dimensional = new ArrayList<>();
		this.vms_canbe_grouped = new ArrayList<>();
		this.vms_canbe_grouped_two_dimensional = new ArrayList<>();
	}

	/**
	 * update the group list in this host
	 * 
	 * @param vm_table
	 * @param group_threshold
	 */
	public void update_group(Hashtable<String, VM> vm_table, Double group_threshold) {
		this.group_list.clear();
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
			this.group_list.add(group);
		}
	}

	/**
	 * update the group list in this host in two dimensional situation
	 * 
	 * @param vm_table
	 * @param group_threshold
	 */
	public void update_group_two_dimensional(Hashtable<String, VM> vm_table, Double group_threshold) {
		this.group_list_two_dimensional.clear();
		ArrayList<String> tmp_vm_list = (ArrayList<String>) this.vms_canbe_grouped_two_dimensional.clone();
		while (!tmp_vm_list.isEmpty()) {
			ArrayList<String> group = new ArrayList<>();
			Double group_cpu_size = 0.0;
			Double group_mem_size = 0.0;
			for (String vm_id : tmp_vm_list) {
				VM current_vm = vm_table.get(vm_id);
				if (current_vm.getCpu_demand() + group_cpu_size <= group_threshold
						&& current_vm.getMem_demand() + group_mem_size <= group_threshold) {
					group.add(vm_id);
					group_cpu_size = group_cpu_size + current_vm.getCpu_demand();
					group_mem_size = group_mem_size + current_vm.getMem_demand();
				}
			}
			tmp_vm_list.removeAll(group);
			this.group_list.add(group);
		}
	}

	/**
	 * this method is used to add a vm to this host
	 * 
	 * @param vm
	 */
	public void add_vm(VM vm) {
		vm_list.add(vm.getVm_id());
		cpu_utilization = cpu_utilization + vm.getCpu_demand();
		mem_utilization = mem_utilization + vm.getMem_demand();
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;

	}

	/**
	 * this method is used to add a vm to this host with consideration of group operation
	 * 
	 * @param vm
	 * @param group_threshold
	 */
	public void add_vm_in_group(VM vm, Double group_threshold) {
		vm_list.add(vm.getVm_id());
		cpu_utilization = cpu_utilization + vm.getCpu_demand();
		mem_utilization = mem_utilization + vm.getMem_demand();
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;

		if (vm.getCpu_demand() <= group_threshold) {
			this.vms_canbe_grouped.add(vm.getVm_id());
		}
	}

	/**
	 * this method is used to add a vm to this host with consideration of group operation in two dimensional situation
	 * 
	 * @param vm
	 * @param group_threshold
	 */
	public void add_vm_in_group_intwo(VM vm, Double group_threshold) {
		vm_list.add(vm.getVm_id());
		cpu_utilization = cpu_utilization + vm.getCpu_demand();
		mem_utilization = mem_utilization + vm.getMem_demand();
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;

		if (vm.getCpu_demand() <= group_threshold && vm.getMem_demand() <= group_threshold) {
			this.vms_canbe_grouped_two_dimensional.add(vm.getVm_id());
		}
	}

	/**
	 * this method is used to delete a vm from this host
	 * 
	 * @param vm
	 */
	public void delete_vm(VM vm) {
		// System.out.println(vm);
		vm_list.remove(vm.getVm_id());
		vm.emptyDeployed_host();
		cpu_utilization = cpu_utilization - vm.getCpu_demand();
		mem_utilization = mem_utilization - vm.getMem_demand();
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;
		if (this.vm_list.isEmpty()) {
			cpu_utilization = 0.0;
			mem_utilization = 0.0;
			cpu_gap = 1.0;
			mem_gap = 1.0;
		}
	}

	/**
	 * this method is used to delete a vm from this host with consideration of group operations
	 * 
	 * @param vm
	 * @param group_threshold
	 */
	public void delete_vm_in_group(VM vm, Double group_threshold) {
		vm_list.remove(vm.getVm_id());
		vm.emptyDeployed_host();
		cpu_utilization = cpu_utilization - vm.getCpu_demand();
		mem_utilization = mem_utilization - vm.getMem_demand();
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;

		if (vm.getCpu_demand() <= group_threshold) {
			this.vms_canbe_grouped.remove(vm.getVm_id());
		}
		if (this.vm_list.isEmpty()) {
			cpu_utilization = 0.0;
			mem_utilization = 0.0;
			cpu_gap = 1.0;
			mem_gap = 1.0;
		}
	}

	/**
	 * this method is used to delete a vm from this host with consideration of group operations in two dimensional
	 * situation
	 * 
	 * @param vm
	 * @param group_threshold
	 */
	public void delete_vm_in_group_intwo(VM vm, Double group_threshold) {
		vm_list.remove(vm.getVm_id());
		vm.emptyDeployed_host();
		cpu_utilization = cpu_utilization - vm.getCpu_demand();
		mem_utilization = mem_utilization - vm.getMem_demand();
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;

		if (vm.getCpu_demand() <= group_threshold && vm.getMem_demand() <= group_threshold) {
			this.vms_canbe_grouped_two_dimensional.remove(vm.getVm_id());
		}
		if (this.vm_list.isEmpty()) {
			cpu_utilization = 0.0;
			mem_utilization = 0.0;
			cpu_gap = 1.0;
			mem_gap = 1.0;
		}
	}

	/**
	 * delete a vm group from current host
	 * 
	 * @param vm_group
	 */
	public void delete_group(ArrayList<String> vm_group, Hashtable<String, VM> vm_table) {
		for (String vm_id : vm_group) {
			VM current_vm = vm_table.get(vm_id);
			// System.out.println(current_vm);
			this.delete_vm(current_vm);
			vms_canbe_grouped.remove(vm_id);
		}
		this.group_list.remove(vm_group);
	}

	/**
	 * delete a vm group from current host considering two dimensional situation
	 * 
	 * @param vm_group
	 */
	public void delete_group_intwo(ArrayList<String> vm_group, Hashtable<String, VM> vm_table) {
		for (String vm_id : vm_group) {
			VM current_vm = vm_table.get(vm_id);
			this.delete_vm(current_vm);
			vms_canbe_grouped_two_dimensional.remove(vm_id);
		}
		this.group_list_two_dimensional.remove(vm_group);
	}

	/**
	 * this methond is used to update the vm's resource demand deployed in this host
	 * 
	 * @param original_cpu
	 * @param original_mem
	 * @param new_cpu
	 * @param new_mem
	 */
	public void update_vm_demand(Double original_cpu, Double original_mem, Double new_cpu, Double new_mem) {
		cpu_utilization = cpu_utilization - original_cpu + new_cpu;
		mem_utilization = mem_utilization - original_mem + new_mem;
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;
	}

	/**
	 * this methond is used to update the vm's resource demand deployed in this host with consideration of group
	 * operation
	 * 
	 * @param original_cpu
	 * @param original_mem
	 * @param new_cpu
	 * @param new_mem
	 * @param vm_id
	 * @param group_threshold
	 */
	public void update_vm_demand_group(Double original_cpu, Double original_mem, Double new_cpu, Double new_mem,
			String vm_id, Double group_threshold) {
		cpu_utilization = cpu_utilization - original_cpu + new_cpu;
		mem_utilization = mem_utilization - original_mem + new_mem;
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;

		if (original_cpu <= group_threshold) {
			this.vms_canbe_grouped.remove(vm_id);
		}
		if (new_cpu <= group_threshold) {
			this.vms_canbe_grouped.add(vm_id);
		}
	}

	/**
	 * this methond is used to update the vm's resource demand deployed in this host with consideration of group
	 * operation in two dimensional situation
	 * 
	 * @param original_cpu
	 * @param original_mem
	 * @param new_cpu
	 * @param new_mem
	 * @param vm_id
	 * @param group_threshold
	 */
	public void update_vm_demand_group_intwo(Double original_cpu, Double original_mem, Double new_cpu, Double new_mem,
			String vm_id, Double group_threshold) {
		cpu_utilization = cpu_utilization - original_cpu + new_cpu;
		mem_utilization = mem_utilization - original_mem + new_mem;
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;

		if (original_cpu <= group_threshold && original_mem <= group_threshold) {
			this.vms_canbe_grouped_two_dimensional.remove(vm_id);
		}
		if (new_cpu <= group_threshold && new_mem <= group_threshold) {
			this.vms_canbe_grouped_two_dimensional.add(vm_id);
		}
	}

	public Double getCpu_utilization() {
		return cpu_utilization;
	}

	public void setCpu_utilization(Double cpu_utilization) {
		this.cpu_utilization = cpu_utilization;
	}

	public Double getMem_utilization() {
		return mem_utilization;
	}

	public void setMem_utilization(Double mem_utilization) {
		this.mem_utilization = mem_utilization;
	}

	public Double getCpu_gap() {
		return cpu_gap;
	}

	public void setCpu_gap(Double cpu_gap) {
		this.cpu_gap = cpu_gap;
	}

	public Double getMem_gap() {
		return mem_gap;
	}

	public void setMem_gap(Double mem_gap) {
		this.mem_gap = mem_gap;
	}

	public ArrayList<String> getVm_list() {
		return vm_list;
	}

	public void setVm_list(ArrayList<String> vm_list) {
		this.vm_list = vm_list;
	}

	public ArrayList<ArrayList<String>> getGroup_list() {
		return group_list;
	}

	public void setGroup_list(ArrayList<ArrayList<String>> group_list) {
		this.group_list = group_list;
	}

	public ArrayList<ArrayList<String>> getGroup_list_two_dimensional() {
		return group_list_two_dimensional;
	}

	public void setGroup_list_two_dimensional(ArrayList<ArrayList<String>> group_list_two_dimensional) {
		this.group_list_two_dimensional = group_list_two_dimensional;
	}

}
