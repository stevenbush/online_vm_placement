/**
 * this class is used to represent a physic host
 * 
 * @author jiyuanshi(shijiyuan.seu@gmail.com)
 * @since 2013-12-13
 * @version V1.0
 */

package core;

public class VM {

	private String vm_id; // the id of this vm
	private Double cpu_demand; // the cpu resource demand
	private Double mem_demand; // the mem resource demand
	private Host deployed_host; // the host where this vm is deployed.

	public VM(String vm_id, Double cpu_demand, Double mem_demand) {
		this.vm_id = vm_id;
		this.cpu_demand = cpu_demand;
		this.mem_demand = mem_demand;
	}

	/**
	 * this method is used to update the resource demand of a vm
	 * 
	 * @param cpu
	 * @param mem
	 */
	public void update(Double cpu, Double mem) {
		deployed_host.update_vm_demand(cpu_demand, mem_demand, cpu, mem);
		cpu_demand = cpu;
		mem_demand = mem;
	}

	/**
	 * this method is used to update the resource demand of a vm with consideration of group operations
	 * 
	 * @param cpu
	 * @param mem
	 * @param group_threshold
	 */
	public void update_group(Double cpu, Double mem, Double group_threshold) {
		deployed_host.update_vm_demand_group(cpu_demand, mem_demand, cpu, mem, vm_id, group_threshold);
		cpu_demand = cpu;
		mem_demand = mem;
	}

	/**
	 * this method is used to update the resource demand of a vm with consideration of group operations in two
	 * dimensional situation
	 * 
	 * @param cpu
	 * @param mem
	 * @param group_threshold
	 */
	public void update_group_intwo(Double cpu, Double mem, Double group_threshold) {
		deployed_host.update_vm_demand_group_intwo(cpu_demand, mem_demand, cpu, mem, vm_id, group_threshold);
		cpu_demand = cpu;
		mem_demand = mem;
	}

	public String getVm_id() {
		return vm_id;
	}

	public void setVm_id(String vm_id) {
		this.vm_id = vm_id;
	}

	public Double getCpu_demand() {
		return cpu_demand;
	}

	public void setCpu_demand(Double cpu_demand) {
		this.cpu_demand = cpu_demand;
	}

	public Double getMem_demand() {
		return mem_demand;
	}

	public void setMem_demand(Double mem_demand) {
		this.mem_demand = mem_demand;
	}

	public Host getDeployed_host() {
		return deployed_host;
	}

	public void setDeployed_host(Host deployed_host) {
		this.deployed_host = deployed_host;
	}

	/**
	 * empty the deployed host of this vm
	 */
	public void emptyDeployed_host() {
		this.deployed_host = null;
	}

}
