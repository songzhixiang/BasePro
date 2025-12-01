# 1. 一些内置命令
# 1.1 ${CMAKE_SOURCE_DIR} CMakeLists.txt所在目录
# 1.2 ${PROJECT_SOURCE_DIR} 工程的根目录
# 1.3 ${PROJECT_BINARY_DIR} 运行 cmake 命令的目录，通常是 ${PROJECT_SOURCE_DIR}/build
# 1.4 ${CMAKE_CURRENT_SOURCE_DIR} 当前处理的 CMakeLists.txt 所在的路径
# 1.5 ${CMAKE_CURRENT_BINARY_DIR} target 编译目录
# 1.6 ${CMAKE_CURRENT_LIST_DIR} CMakeLists.txt 的完整路径
# 1.7 ${CMAKE_C_FLAGS} 设置 C 编译选项
# 1.7 ${CMAKE_CXX_FLAGS} 设置 C++ 编译选项

# 2. 一些概念
# 2.1 设置第三方so库的路径,导入预编译的库，比如需要用到别人的so
# set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L[SO所在目录]")
# 2.2 设置Cmake最小版本
# cmake_minimum_required(VERSION 3.22.1)
# 2.3 设置项目名称
# project("can")
# 2.4 添加头文件
# include_directories(include)
# 2.5 查找当前目录所有源文件 并将名称保存到SRC_LIST变量，用于添加多个源文件
# a. 用aux
# aux_source_directory(. SRC_LIST)
# b. 也可以使用file
# file(GLOB SRC_LIST *.c *.cpp)

# 3. 常见的方法
# 3.1 编译library 库
# add_library(# 设置这个库的名字,# SHARE 动态库.so后缀 , STATIC 静态库 .a后缀,# 源文件的相对路径)
# 3.2 定位library 库，找到一个NDK库,并且把这个库的路径储存在一个变量中
# 例如例子中就是找到一个NDK中的log库（支持android的日志库），并且把变量储存在log-lib变量中
# find_library(
#         log-lib
#         log)
# 3.3 关联library 库，将指定的库关联起来
#target_link_libraries( <name> lib1 lib2 lib3
# <name> 目标库.
# lib1 目标库需要链接的库 这里有libzhixuan-can.so
# lib2 目标库需要链接的库 这里有libc++.so
#        ${log-lib}
#        android)

### 代码如下

# 要求的Cmake最低版本
cmake_minimum_required(VERSION 3.22.1)

#打印LOG
message(STATUS "Cmake build type is: "${CMAKE_BUILD_TYPE})
message(STATUS "Cmake build android abi is: "${ANDROID_ABI})

# 使得编译好的.so 放在当前CMakeList的文件夹目录的jniLibs中
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})

project("ipcdemo")

add_library(${CMAKE_PROJECT_NAME} SHARED
        HookFdUtils.cpp)

find_library(
        log-lib
        log)

target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log)