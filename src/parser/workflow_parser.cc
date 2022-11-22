#include <memory>
#include <vector>

#include "sercheduler/parser/workflow_parser.h"

namespace sercheduler {
std::vector<std::shared_ptr<Task>> ParseJsonWorkflow(const nlohmann::json& j) {
  auto tasks_json = j.at("workflow").at("tasks");
  std::vector<std::shared_ptr<Task>> tasks(tasks_json.size());

  for (const auto& task_j : tasks_json) {
    Task task;

    // tasks.push_back(std::shared_ptr<Task>(&task));
  }
  return tasks;
}

}  // namespace sercheduler
