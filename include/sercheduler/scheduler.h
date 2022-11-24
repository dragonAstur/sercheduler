#ifndef SERCHEDULER_SCHEDULER_H
#define SERCHEDULER_SCHEDULER_H
#include <vector>

#include "sercheduler/workflow.h"
namespace sercheduler {
class Scheduler {
 private:
  /* data */

  std::vector<std ::vector<double>> computation_matrix_;
  std::vector<std ::vector<double>> CalculateComputationMatrix(
      std::vector<Task>& tasks, std::vector<Host>& hosts, int reference_flops);

 public:
  // Scheduler(std::vector<Task>& tasks, std::vector<Host>& hosts, int
  // reference_flops);
  //   ~Scheduler();
};

}  // namespace sercheduler
#endif  // SERCHEDULER_SCHEDULER_H
