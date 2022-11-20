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
        include/sercheduler/parser/WorkflowParser.hpp

)

# source files
set(SOURCE_FILES 
        src/parser/WorkflowParser.cpp
)



add_library(sercheduler STATIC ${SOURCE_FILES} ${HEADER_FILES})




# Note: by default ENABLE_DEVELOPER_MODE is True
# This means that all analysis (sanitizers, static analysis)
# is enabled and all warnings are treated as errors
# if you want to switch this behavior, change TRUE to FALSE
set(ENABLE_DEVELOPER_MODE
    TRUE
    CACHE BOOL "Enable 'developer mode'")




# Let's nicely support folders in IDEs
set_property(GLOBAL PROPERTY USE_FOLDERS ON)

# Testing only available if this is the main app
# Note this needs to be done in the main CMakeLists
# since it calls enable_testing, which must be in the
# main CMakeLists.
include(CTest)


include(FetchContent)







