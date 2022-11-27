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
  // Expeted matrix
  std::vector<sercheduler::Task> tasks = {
      {.id = 0, .name = "Task 1", .runtime = 4},
      {.id = 1, .name = "Task 2", .runtime = 11},
      {.id = 2, .name = "Task 3", .runtime = 5},
      {.id = 3, .name = "Task 4", .runtime = 3}};
  std::vector<std::vector<double>> expected_matrix = {
      {100, 200, 300, 0}, {0, 100, 0, 200}, {0, 0, 0, 300}, {0, 0, 0, 0}};
}
