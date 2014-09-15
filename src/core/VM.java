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
	private Abstract_Host deployed_host; // the host where this vm is deployed.

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
		deployed_host.update_vm_demand(this.getVm_id(), cpu_demand, mem_demand, cpu, mem);
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

	public Abstract_Host getDeployed_host() {
		return deployed_host;
	}

	public void setDeployed_host(Abstract_Host deployed_host) {
		this.deployed_host = deployed_host;
	}

	/**
	 * empty the deployed host of this vm
	 */
	public void emptyDeployed_host() {
		this.deployed_host = null;
	}

}
