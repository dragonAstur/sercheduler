# Works with 3.16 and tested through 3.25
cmake_minimum_required(VERSION 3.16...3.25)

project(
        Serchedule
        VERSION 0.1
        DESCRIPTION "Library for calculating schedules"
        LANGUAGES CXX)

include_directories(src/ include/ apps/)

# library header files
set(HEADER_FILES
        include/sercheduler/workflow.h
        include/sercheduler/parser/workflow_parser.h
        include/sercheduler/scheduler.h
)

# source files
set(SOURCE_FILES
        src/parser/workflow_parser.cc
        src/scheduler.cc
)

# test files
set(TEST_FILES
        test/parser/workflow_parser_test.cc
)

add_library(sercheduler STATIC ${SOURCE_FILES} ${HEADER_FILES})

# Let's nicely support folders in IDEs
set_property(GLOBAL PROPERTY USE_FOLDERS ON)

# Dependencies
include(FetchContent)

FetchContent_Declare(json URL https://github.com/nlohmann/json/releases/download/v3.11.2/json.tar.xz)
FetchContent_MakeAvailable(json)

target_link_libraries(sercheduler PRIVATE nlohmann_json::nlohmann_json)

# Testing
include(CTest)

FetchContent_Declare(
        googletest
        URL https://github.com/google/googletest/archive/58d77fa8070e8cec2dc1ed015d66b454c8d78850.zip
)

# For Windows: Prevent overriding the parent project's compiler/linker settings
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

add_executable(
        sercheduler_test
        ${SOURCE_FILES}
        ${HEADER_FILES}
        ${TEST_FILES}
)
enable_testing()

# add_test(HelloTest test)
# set_tests_properties(HelloTest PROPERTIES
# ENVIRONMENT
# DATADIR=${CMAKE_CURRENT_SOURCE_DIR}/tests/data/)
target_link_libraries(
        sercheduler_test
        GTest::gtest_main
        sercheduler
)

include(GoogleTest)

gtest_discover_tests(sercheduler_test)

# gtest_add_tests(TARGET      sercheduler_test
# TEST_SUFFIX .noArgs
# TEST_LIST   noArgsTests
# )
# set_tests_properties(${noArgsTests} PROPERTIES
# WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/test/)
