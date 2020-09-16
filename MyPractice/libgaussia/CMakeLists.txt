# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

MESSAGE(STATUS "android source dir ${CMAKE_SOURCE_DIR}")


#----
#    set(GAUSSIAN_SOURCE
#            src/main/cpp/gaussian_logger.c
#            src/main/cpp/com_demon_yu_lib_gaussian_GaussianHelper.c)
#    MESSAGE(STATUS "sources GAUSSIAN_SRC")
#----
aux_source_directory("src/main/cpp" GAUSSIAN_SOURCE)
MESSAGE(STATUS "sources ${GAUSSIAN_SOURCE}")


#    include_directories(${INCLUDE_DIR})     # make sure your .h all inside.

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.


add_library( # Specifies the name of the library.
        gaussia-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${GAUSSIAN_SOURCE}
        )

find_library( # Defines the name of the path variable that stores the
        # location of the NDK library.
        log-lib

        # Specifies the name of the NDK library that
        # CMake needs to locate.
        log)

target_link_libraries(gaussia-lib jnigraphics android ${log-lib})

