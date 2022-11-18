
set(HEADER_LIST "${Serchedule_SOURCE_DIR}/include/sercheduler/lib.hpp")

add_library(sercheduler_lib lib.cpp ${HEADER_LIST})

# All users of this library will need at least C++20
target_compile_features(sercheduler_lib PUBLIC cxx_std_20)

# We need this directory, and users of our library will need it too
target_include_directories(sercheduler_lib PUBLIC ../include)



# IDEs should put the headers in a nice place
source_group(
  TREE "${PROJECT_SOURCE_DIR}/include"
  PREFIX "Header Files"
  FILES ${HEADER_LIST})
