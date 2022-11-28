#include <gtest/gtest.h>

#include <fstream>

#include "sercheduler/parser/workflow_parser.h"

// Check the correct parse of a workflow from json
TEST(WorkflowParserTest, ParseFromJson) {
  std::ifstream i("../data/test/workflow_test.json");
  nlohmann::json j;
  i >> j;

  auto result = sercheduler::ParseJsonWorkflow(j);

  ASSERT_EQ(result.size(), 10);

  // Check if we map every field

  auto const& task4 = result[3];

  EXPECT_STREQ(task4.name.c_str(), "task04");
  EXPECT_EQ(task4.runtime, 13);
  EXPECT_EQ(task4.id, 3);
  EXPECT_EQ(task4.parents[0]->name, "task01");
  EXPECT_EQ(task4.parents[0]->id, 0);
  // Parent should update the references
  EXPECT_EQ(task4.parents[0]->children[0]->id, 1);

  ASSERT_EQ(task4.children.size(), 2);
  EXPECT_EQ(task4.children[0]->name, "task08");
  EXPECT_EQ(task4.children[0]->id, 7);

  // Check if the children update the parents

  auto const& task1 = result[0];
  EXPECT_EQ(task1.children[0]->parents[0]->id, 0);

  i.close();
}
