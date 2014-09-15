package core;

import java.util.ArrayList;

/**
 * this is a abstract class used to represent a physic host
 * 
 * @author jiyuanshi(shijiyuan.seu@gmail.com)
 * @since 2013-12-13
 * @version V1.0
 */

public abstract class Abstract_Host {
	/** the utilization of cpu resource */
	protected Double cpu_utilization;
	/** the utilization of mem resource */
	protected Double mem_utilization;
	/** the max resource utilization for two dimensional situation */
	protected Double max_uitilization;
	/** the gap of cpu resource */
	protected Double cpu_gap;
	/** the gap of mem resource */
	protected Double mem_gap;
	/** the max resource gap for two dimensional situation */
	protected Double max_gap;
	/** the vm putted in this host */
	protected ArrayList<String> vm_list;

	public Abstract_Host() {
		this.cpu_utilization = 0.0;
		this.mem_utilization = 0.0;
		this.max_uitilization = 0.0;
		this.cpu_gap = 1.0;
		this.mem_gap = 1.0;
		this.max_gap = 1.0;
		this.vm_list = new ArrayList<>();
	}

	/**
	 * this method is used to add a vm to this host
	 * 
	 * @param vm
	 * @throws Exception 
	 */
	public abstract void add_vm(VM vm) throws Exception;

	/**
	 * this method is used to delete a vm from this host
	 * 
	 * @param vm
	 */
	public abstract void delete_vm(VM vm);

	/**
	 * this methond is used to update the vm's resource demand deployed in this host
	 * 
	 * @param original_cpu
	 * @param original_mem
	 * @param new_cpu
	 * @param new_mem
	 */
	public abstract void update_vm_demand(String vm_id, Double original_cpu, Double original_mem, Double new_cpu,
			Double new_mem);

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

	public Double getMax_uitilization() {
		return max_uitilization;
	}

	public Double getMax_gap() {
		return max_gap;
	}

}
