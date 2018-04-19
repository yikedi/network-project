cmake_minimum_required(VERSION 3.4)
project(a3_u7h0b_x5n0b)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES CSftp.c dir.h dir.c usage.h usage.c)
add_executable(CSftp_try ${SOURCE_FILES})