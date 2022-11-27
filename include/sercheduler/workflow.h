
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
  std::vector<TaskFile> input_files;
  std::vector<TaskFile> output_files;
  double output_data;

  double runtime;
};

struct TaskFile {
  std::string name;
  std::string direction;
  double size;
};

/**
 * @brief Defines a vm machine
 *
 *
 */
struct Host {
  int id;
  std::string name;
  double flops;
  int cores;
};

}  // namespace sercheduler

#endif  // SERCHEDULER_WORKFLOW_H