#ifndef SERCHEDULER_PARSER_WORKFLOWPARSER_HPP
#define SERCHEDULER_PARSER_WORKFLOWPARSER_HPP

#include <memory>
#include <string>
#include <vector>

#include "nlohmann/json.hpp"
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
  std::vector<std::shared_ptr<Task>> parents;
  std::vector<std::shared_ptr<Task>> children;
  double runtime;
};

std::vector<std::shared_ptr<Task>> ParseJsonWorkflow(const nlohmann::json& j);

}  // namespace sercheduler
#endif  // SERCHEDULER_PARSER_WORKFLOWPARSER_HPP
