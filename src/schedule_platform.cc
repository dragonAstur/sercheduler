#include "sercheduler/schedule_platform.h"

namespace sercheduler {

std::vector<std ::vector<double>> CalculateComputationMatrix(
    const std::vector<Task>& tasks, const std::vector<Host>& hosts,
    double reference_flops) {
  std::vector<std ::vector<double>> computation_matrix(
      tasks.size(), std::vector<double>(hosts.size()));

  for (const auto& task : tasks) {
    for (const auto& host : hosts) {
      float flops_factor =
          float(reference_flops) / float(host.flops * host.cores);
      // Multiply the runtime by the factor to know the runtime with the new
      // hardware
      computation_matrix[task.id][host.id] = task.runtime * flops_factor;
    }
  }

  return computation_matrix;
}

std::vector<std ::vector<double>> CalculateNetworkMatrix(
    std::vector<Task>& tasks) {
  std::vector<std::vector<double>> network_matrix(
      tasks.size(), std::vector<double>(tasks.size()));

  for (const auto& task : tasks) {
    double coms_inter_tasks = 0;

    for (const auto& parent : task.parents) {
      // network_matrix[parent->id]
    }
  }

  return network_matrix;
}

}  // namespace sercheduler
