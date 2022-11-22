#include <gtest/gtest.h>

#include <cstdlib>
#include <fstream>
#include <sstream>
#include <string>

// Demonstrate some basic assertions.
TEST(HelloTest, BasicAssertions) {
  // Expect two strings not to be equal.
  EXPECT_STRNE("hello", "world");
  // Expect equality.
  EXPECT_EQ(7 * 6, 42);
  // Expect test dir
  // auto test_dir = std::getenv("DATADIR");

  std::ifstream infile("data/test.txt");
  std::string line;
  auto expected = "42";
  while (std::getline(infile, line)) {
    EXPECT_STREQ(line.c_str(), expected);
  }
}