#ifndef SERCHEDULER_SCHEDULER_H
#define SERCHEDULER_SCHEDULER_H
#include <vector>

#include "sercheduler/platform.h"
#include "sercheduler/workflow.h"
namespace sercheduler {
class Scheduler {
 private:
  /* data */
  Platform platform_;

 public:
  // Scheduler(std::vector<Task>& tasks, std::vector<Host>& hosts, int
  // reference_flops);
  //   ~Scheduler();
};

}  // namespace sercheduler
#endif  // SERCHEDULER_SCHEDULER_H
