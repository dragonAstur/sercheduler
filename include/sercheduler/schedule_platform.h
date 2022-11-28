#ifndef SERCHEDULER_SCHEDULE_PLATFORM_H
#define SERCHEDULER_SCHEDULE_PLATFORM_H
#include <string>
#include <vector>

#include "sercheduler/workflow.h"
namespace sercheduler {
struct Platform {
  std::vector<std ::vector<double>> computation_matrix;
  std::vector<std ::vector<double>> network_matrix;
  std::vector<Task> tasks;
  std::vector<Host> hosts;
  std::string reference_flops;
};

/**
 * @brief Creates a matrix to know how long it takes to execute each task in
 * each host To calculate the matrix we need to know the exact flops of each
 * task, we only have the time expent in a past execution so we can use that as
 * a base, so we say the reference host has 1GFlops and the task took 10 secs
 * so the task is 10 million mips
 * @param tasks List of task to execute
 * @param hosts Available hosts to use
 * @param reference_flops The CPU used for the runtime in the tasks
 * @return std::vector<std ::vector<double>>
 */
std::vector<std ::vector<double>> CalculateComputationMatrix(
    const std::vector<Task>& tasks, const std::vector<Host>& hosts,
    double reference_flops);

/**
 * @brief Given a workflow calculate all bits to transmit
 * The diagonal represents staging files.
 * @param tasks
 * @return std::vector<std ::vector<double>>
 */
std::vector<std ::vector<double>> CalculateNetworkMatrix(
    std::vector<Task>& tasks);

Platform CreatePlatform(std::vector<Task>& tasks, std::vector<Host>& hosts,
                        double reference_flops, double reference_speed);
}  // namespace sercheduler
#endif  // SERCHEDULER_SCHEDULE_PLATFORM_H