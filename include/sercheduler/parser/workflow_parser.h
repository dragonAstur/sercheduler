#ifndef SERCHEDULER_PARSER_WORKFLOW_PARSER_H
#define SERCHEDULER_PARSER_WORKFLOW_PARSER_H

#include <string>
#include <vector>

#include "nlohmann/json.hpp"
#include "sercheduler/workflow.h"
namespace sercheduler {

std::vector<Task> ParseJsonWorkflow(const nlohmann::json& j);

}  // namespace sercheduler
#endif  // SERCHEDULER_PARSER_WORKFLOW_PARSER_H
