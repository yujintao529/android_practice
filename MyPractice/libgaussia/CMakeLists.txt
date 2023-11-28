    # Sets the minimum version of CMake required to build your native library.
    # This ensures that a certain set of CMake features is available to
    # your build.

    cmake_minimum_required(VERSION 3.6.0)

    MESSAGE(STATUS "android source dir ${PROJECT_SOURCE_DIR}")

    set(PROJECT_ROOT ${PROJECT_SOURCE_DIR})
    set(INCLUDE_DIR ${PROJECT_ROOT}/src/main/cpp/include)
#----
#    set(GAUSSIAN_SOURCE
#            src/main/cpp/gaussian_logger.c
#            src/main/cpp/com_demon_yu_lib_gaussian_GaussianHelper.c)
#    MESSAGE(STATUS "sources GAUSSIAN_SRC")
#----
    aux_source_directory("src/main/cpp" GAUSSIAN_SOURCE) # 会扫描src/main/cpp下的所有.c,.cc,cpp文件作为源码
    MESSAGE(STATUS "sources ${GAUSSIAN_SOURCE}")

    include_directories(${PROJECT_ROOT} ${INCLUDE_DIR} )     # make sure your .h all inside.

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


    target_link_libraries(gaussia-lib jnigraphics android  log)

