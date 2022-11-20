#ifndef SERCHEDULER_PARSER_WORKFLOWPARSER_HPP
#define SERCHEDULER_PARSER_WORKFLOWPARSER_HPP

#include <memory>
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
    std::vector<std::shared_ptr<Task>> parents;
    std::vector<std::shared_ptr<Task>> children;
    double runtime;


} __attribute__((aligned(128))) ;


void sayHello();

}  // namespace sercheduler
#endif //SERCHEDULER_PARSER_WORKFLOWPARSER_HPP

