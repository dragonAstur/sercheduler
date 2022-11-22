#include <gtest/gtest.h>

#include <fstream>

#include "sercheduler/parser/workflow_parser.h"

// Check the correct parse of a workflow from json
TEST(WorkflowParserTest, ParseFromJson) {
  std::ifstream i("../data/test/workflow_test.json");
  nlohmann::json j;
  i >> j;

  auto result = sercheduler::ParseJsonWorkflow(j);

  EXPECT_EQ(result.size(), 10);
  i.close();
}
