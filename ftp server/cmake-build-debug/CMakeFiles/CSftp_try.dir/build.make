# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.6

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Remove some rules from gmake that .SUFFIXES does not remove.
SUFFIXES =

.SUFFIXES: .hpux_make_needs_suffix_list


# Suppress display of executed commands.
$(VERBOSE).SILENT:


# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /Applications/CLion.app/Contents/bin/cmake/bin/cmake

# The command to remove a file.
RM = /Applications/CLion.app/Contents/bin/cmake/bin/cmake -E remove -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /Users/douglas/ClionProjects/a3_u7h0b_x5n0b

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/cmake-build-debug

# Include any dependencies generated for this target.
include CMakeFiles/CSftp_try.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/CSftp_try.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/CSftp_try.dir/flags.make

CMakeFiles/CSftp_try.dir/CSftp.c.o: CMakeFiles/CSftp_try.dir/flags.make
CMakeFiles/CSftp_try.dir/CSftp.c.o: ../CSftp.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/douglas/ClionProjects/a3_u7h0b_x5n0b/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building C object CMakeFiles/CSftp_try.dir/CSftp.c.o"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/cc  $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/CSftp_try.dir/CSftp.c.o   -c /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/CSftp.c

CMakeFiles/CSftp_try.dir/CSftp.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/CSftp_try.dir/CSftp.c.i"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/cc  $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/CSftp.c > CMakeFiles/CSftp_try.dir/CSftp.c.i

CMakeFiles/CSftp_try.dir/CSftp.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/CSftp_try.dir/CSftp.c.s"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/cc  $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/CSftp.c -o CMakeFiles/CSftp_try.dir/CSftp.c.s

CMakeFiles/CSftp_try.dir/CSftp.c.o.requires:

.PHONY : CMakeFiles/CSftp_try.dir/CSftp.c.o.requires

CMakeFiles/CSftp_try.dir/CSftp.c.o.provides: CMakeFiles/CSftp_try.dir/CSftp.c.o.requires
	$(MAKE) -f CMakeFiles/CSftp_try.dir/build.make CMakeFiles/CSftp_try.dir/CSftp.c.o.provides.build
.PHONY : CMakeFiles/CSftp_try.dir/CSftp.c.o.provides

CMakeFiles/CSftp_try.dir/CSftp.c.o.provides.build: CMakeFiles/CSftp_try.dir/CSftp.c.o


CMakeFiles/CSftp_try.dir/dir.c.o: CMakeFiles/CSftp_try.dir/flags.make
CMakeFiles/CSftp_try.dir/dir.c.o: ../dir.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/douglas/ClionProjects/a3_u7h0b_x5n0b/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Building C object CMakeFiles/CSftp_try.dir/dir.c.o"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/cc  $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/CSftp_try.dir/dir.c.o   -c /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/dir.c

CMakeFiles/CSftp_try.dir/dir.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/CSftp_try.dir/dir.c.i"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/cc  $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/dir.c > CMakeFiles/CSftp_try.dir/dir.c.i

CMakeFiles/CSftp_try.dir/dir.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/CSftp_try.dir/dir.c.s"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/cc  $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/dir.c -o CMakeFiles/CSftp_try.dir/dir.c.s

CMakeFiles/CSftp_try.dir/dir.c.o.requires:

.PHONY : CMakeFiles/CSftp_try.dir/dir.c.o.requires

CMakeFiles/CSftp_try.dir/dir.c.o.provides: CMakeFiles/CSftp_try.dir/dir.c.o.requires
	$(MAKE) -f CMakeFiles/CSftp_try.dir/build.make CMakeFiles/CSftp_try.dir/dir.c.o.provides.build
.PHONY : CMakeFiles/CSftp_try.dir/dir.c.o.provides

CMakeFiles/CSftp_try.dir/dir.c.o.provides.build: CMakeFiles/CSftp_try.dir/dir.c.o


CMakeFiles/CSftp_try.dir/usage.c.o: CMakeFiles/CSftp_try.dir/flags.make
CMakeFiles/CSftp_try.dir/usage.c.o: ../usage.c
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/Users/douglas/ClionProjects/a3_u7h0b_x5n0b/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_3) "Building C object CMakeFiles/CSftp_try.dir/usage.c.o"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/cc  $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -o CMakeFiles/CSftp_try.dir/usage.c.o   -c /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/usage.c

CMakeFiles/CSftp_try.dir/usage.c.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing C source to CMakeFiles/CSftp_try.dir/usage.c.i"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/cc  $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -E /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/usage.c > CMakeFiles/CSftp_try.dir/usage.c.i

CMakeFiles/CSftp_try.dir/usage.c.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling C source to assembly CMakeFiles/CSftp_try.dir/usage.c.s"
	/Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/bin/cc  $(C_DEFINES) $(C_INCLUDES) $(C_FLAGS) -S /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/usage.c -o CMakeFiles/CSftp_try.dir/usage.c.s

CMakeFiles/CSftp_try.dir/usage.c.o.requires:

.PHONY : CMakeFiles/CSftp_try.dir/usage.c.o.requires

CMakeFiles/CSftp_try.dir/usage.c.o.provides: CMakeFiles/CSftp_try.dir/usage.c.o.requires
	$(MAKE) -f CMakeFiles/CSftp_try.dir/build.make CMakeFiles/CSftp_try.dir/usage.c.o.provides.build
.PHONY : CMakeFiles/CSftp_try.dir/usage.c.o.provides

CMakeFiles/CSftp_try.dir/usage.c.o.provides.build: CMakeFiles/CSftp_try.dir/usage.c.o


# Object files for target CSftp_try
CSftp_try_OBJECTS = \
"CMakeFiles/CSftp_try.dir/CSftp.c.o" \
"CMakeFiles/CSftp_try.dir/dir.c.o" \
"CMakeFiles/CSftp_try.dir/usage.c.o"

# External object files for target CSftp_try
CSftp_try_EXTERNAL_OBJECTS =

CSftp_try: CMakeFiles/CSftp_try.dir/CSftp.c.o
CSftp_try: CMakeFiles/CSftp_try.dir/dir.c.o
CSftp_try: CMakeFiles/CSftp_try.dir/usage.c.o
CSftp_try: CMakeFiles/CSftp_try.dir/build.make
CSftp_try: CMakeFiles/CSftp_try.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/Users/douglas/ClionProjects/a3_u7h0b_x5n0b/cmake-build-debug/CMakeFiles --progress-num=$(CMAKE_PROGRESS_4) "Linking C executable CSftp_try"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/CSftp_try.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/CSftp_try.dir/build: CSftp_try

.PHONY : CMakeFiles/CSftp_try.dir/build

CMakeFiles/CSftp_try.dir/requires: CMakeFiles/CSftp_try.dir/CSftp.c.o.requires
CMakeFiles/CSftp_try.dir/requires: CMakeFiles/CSftp_try.dir/dir.c.o.requires
CMakeFiles/CSftp_try.dir/requires: CMakeFiles/CSftp_try.dir/usage.c.o.requires

.PHONY : CMakeFiles/CSftp_try.dir/requires

CMakeFiles/CSftp_try.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/CSftp_try.dir/cmake_clean.cmake
.PHONY : CMakeFiles/CSftp_try.dir/clean

CMakeFiles/CSftp_try.dir/depend:
	cd /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/cmake-build-debug && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /Users/douglas/ClionProjects/a3_u7h0b_x5n0b /Users/douglas/ClionProjects/a3_u7h0b_x5n0b /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/cmake-build-debug /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/cmake-build-debug /Users/douglas/ClionProjects/a3_u7h0b_x5n0b/cmake-build-debug/CMakeFiles/CSftp_try.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/CSftp_try.dir/depend

