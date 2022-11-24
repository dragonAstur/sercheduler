#include <memory>
#include <vector>

#include "sercheduler/parser/workflow_parser.h"
#include "sercheduler/workflow.h"

namespace sercheduler {
std::vector<Task> ParseJsonWorkflow(const nlohmann::json& j) {
  // We need to record the map between task name and task id.
  // With this information we can then add the task struct to
  // the array of children and parents.
  std::map<std::string, int, std::less<>> name_to_id;

  auto tasks_json = j.at("workflow").at("tasks");
  std::vector<Task> tasks;

  int i = 0;
  for (const auto& task_j : tasks_json) {
    Task task;
    task.name = task_j["name"].get<std::string>();
    task.id = i;
    task.runtime = task_j["runtime"].get<double>();

    tasks.push_back(task);
    name_to_id[task.name] = i;
    ++i;
  }

  // Have to do a second loop to add parents and children
  i = 0;
  for (const auto& task_j : tasks_json) {
    Task& task = tasks[i];

    for (const auto& child : task_j["children"]) {
      auto child_id = name_to_id[child.get<std::string>()];
      task.children.push_back(tasks[child_id]);
    }
    for (const auto& parent : task_j["parents"]) {
      auto parent_id = name_to_id[parent.get<std::string>()];
      task.parents.push_back(tasks[parent_id]);
    }
    ++i;
  }

  return tasks;
}

}  // namespace sercheduler
