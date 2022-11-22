
#ifndef SERCHEDULER_WORKFLOW_H
#define SERCHEDULER_WORKFLOW_H
#include <string>
#include <vector>
namespace sercheduler {
/**
 * @brief Structure to represent a set of task and the relationship between
 * them.
 *
 */
struct Workflow {};

/**
 * @brief
 */
struct Task {
  int id;
  std::string name;
  std::vector<Task> parents;
  std::vector<Task> children;
  double runtime;
};

}  // namespace sercheduler

#endif  // SERCHEDULER_WORKFLOW_H