#include <gmock/gmock-matchers.h>
#include <gtest/gtest.h>
#include <sercheduler/schedule_platform.h>
#include <sercheduler/workflow.h>

#include <vector>

TEST(SchedulerPlatformTest, CalculateComputationMatrix) {
  // Given a set of tasks and a set hosts
  std::vector<sercheduler::Task> tasks = {
      {.id = 0, .name = "Task 1", .runtime = 4},
      {.id = 1, .name = "Task 2", .runtime = 11},
      {.id = 2, .name = "Task 3", .runtime = 5},
      {.id = 3, .name = "Task 4", .runtime = 3},
      {.id = 4, .name = "Task 5", .runtime = 6}};

  std::vector<sercheduler::Host> hosts = {
      {.id = 0, .name = "Host1", .flops = 2000e3, .cores = 1},
      {.id = 1, .name = "Host2", .flops = 1000e3, .cores = 4},
      {.id = 2, .name = "Host3", .flops = 800e3, .cores = 1},
  };

  // The matrix should be this
  std::vector<std::vector<double>> expected_matrix = {{2, 1, 5},
                                                      {5.5, 2.75, 13.75},
                                                      {2.5, 1.25, 6.25},
                                                      {1.5, 0.75, 3.75},
                                                      {3, 1.5, 7.5}

  };

  auto result_matrix =
      sercheduler::CalculateComputationMatrix(tasks, hosts, 1000e3);
  EXPECT_THAT(result_matrix, testing::ContainerEq(expected_matrix));
}

TEST(SchedulerPlatformTest, CalculateNetworkMatrix) {
  std::vector<sercheduler::Task> tasks = {
      {.id = 0,
       .name = "Task 1",
       .runtime = 4,
       .input_files = {{.name = "stagging_1.txt",
                        .direction = "input",
                        .size = 100}},
       .output_files = {{.name = "1_2.txt", .direction = "output", .size = 200},
                        {.name = "1_3.txt",
                         .direction = "output",
                         .size = 300}}},
      {.id = 1,
       .name = "Task 2",
       .runtime = 11,
       .input_files = {{.name = "1_2.txt", .direction = "input", .size = 200},
                       {.name = "stagging_2.txt",
                        .direction = "input",
                        .size = 100}},
       .output_files = {{.name = "2_4.txt", .direction = "output", .size = 100},
                        {.name = "2_4.md",
                         .direction = "output",
                         .size = 100}}},
      {.id = 2,
       .name = "Task 3",
       .runtime = 5,
       .input_files = {{.name = "1_3.txt", .direction = "input", .size = 300}},
       .output_files = {{.name = "3_4.txt", .direction = "output", .size = 100},
                        {.name = "3_4.md",
                         .direction = "output",
                         .size = 200}}},
      {.id = 4,
       .name = "Task 4",
       .runtime = 3,
       .input_files = {{.name = "2_4.txt", .direction = "input", .size = 100},
                       {.name = "2_4.md", .direction = "input", .size = 100},
                       {.name = "3_4.txt", .direction = "input", .size = 100},
                       {.name = "3_4.md", .direction = "input", .size = 200}},
       .output_files = {
           {.name = "output.txt", .direction = "output", .size = 400},
       }}};

  // Assign the parents and children
  // Task 1
  tasks[0].children.emplace_back(&tasks[1]);
  tasks[0].children.emplace_back(&tasks[2]);

  // Task 2
  tasks[1].parents.emplace_back(&tasks[0]);
  tasks[1].children.emplace_back(&tasks[3]);
  // Task 3
  tasks[2].parents.emplace_back(&tasks[0]);
  tasks[2].children.emplace_back(&tasks[3]);

  // Task 4
  tasks[3].parents.emplace_back(&tasks[1]);
  tasks[3].parents.emplace_back(&tasks[1]);

  // Expected matrix
  std::vector<std::vector<double>> expected_matrix = {
      {100, 200, 300, 0}, {0, 100, 0, 200}, {0, 0, 0, 300}, {0, 0, 0, 0}};

  auto result_matrix = sercheduler::CalculateNetworkMatrix(tasks);
  EXPECT_THAT(result_matrix, testing::ContainerEq(expected_matrix));
}
