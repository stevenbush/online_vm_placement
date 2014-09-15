/**
 * this class is used to represent a physic host using by bestfit related algorithm
 * 
 * @author jiyuanshi(shijiyuan.seu@gmail.com)
 * @since 2013-12-13
 * @version V1.0
 */
package core;

import java.util.ArrayList;

public class BestFit_Host extends Abstract_Host {

	public BestFit_Host() {
		super();
		this.cpu_utilization = 0.0;
		this.mem_utilization = 0.0;
		this.cpu_gap = 1.0;
		this.mem_gap = 1.0;
		this.vm_list = new ArrayList<>();
	}

	/**
	 * this method is used to add a vm to this host
	 * 
	 * @param vm
	 */
	@Override
	public void add_vm(VM vm) {
		vm_list.add(vm.getVm_id());
		vm.setDeployed_host(this);
		cpu_utilization = cpu_utilization + vm.getCpu_demand();
		mem_utilization = mem_utilization + vm.getMem_demand();
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;

	}

	/**
	 * this method is used to delete a vm from this host
	 * 
	 * @param vm
	 */
	@Override
	public void delete_vm(VM vm) {
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
	 * this methond is used to update the vm's resource demand deployed in this host
	 * 
	 * @param original_cpu
	 * @param original_mem
	 * @param new_cpu
	 * @param new_mem
	 */
	@Override
	public void update_vm_demand(String vm_id, Double original_cpu, Double original_mem, Double new_cpu, Double new_mem) {
		cpu_utilization = cpu_utilization - original_cpu + new_cpu;
		mem_utilization = mem_utilization - original_mem + new_mem;
		cpu_gap = 1 - cpu_utilization;
		mem_gap = 1 - mem_utilization;
	}

}
