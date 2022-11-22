#include <memory>
#include <vector>

#include "sercheduler/parser/workflow_parser.h"
#include "sercheduler/workflow.h"

namespace sercheduler {
std::vector<Task> ParseJsonWorkflow(const nlohmann::json& j) {
  auto tasks_json = j.at("workflow").at("tasks");
  std::vector<Task> tasks;

  for (const auto& task_j : tasks_json) {
    Task task;
    task.name = task_j["name"].get<std::string>();

    tasks.push_back(task);
  }
  return tasks;
}

}  // namespace sercheduler
