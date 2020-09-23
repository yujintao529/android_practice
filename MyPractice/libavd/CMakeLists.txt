# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.6.0-rc2)

MESSAGE(STATUS "android source dir ${CMAKE_SOURCE_DIR} ")
MESSAGE(STATUS "android binary dir ${CMAKE_BINARY_DIR} ")


#----
#    set(GAUSSIAN_SOURCE
#            src/main/cpp/gaussian_logger.c
#            src/main/cpp/com_demon_yu_lib_gaussian_GaussianHelper.c)
#    MESSAGE(STATUS "sources GAUSSIAN_SRC")
#----

aux_source_directory("src/main/cpp" webrtc_avd_source)
aux_source_directory("src/main/cpp/common_audio/signal_processing" webrtc_avd_source)
set(VAD_SRC "${CMAKE_SOURCE_DIR}/src/main/cpp")

MESSAGE(STATUS "sources ${webrtc_avd_source}")

MESSAGE(STATUS "VAD_SRC path ${VAD_SRC}")

add_definitions(-DANDROID -DWEBRTC_POSIX -DDCHECK_ALWAYS_ON)

set(SIGNAL_PROCESSING ${VAD_SRC}/common_audio/signal_processing/include)
set(SYSTEM ${VAD_SRC}/system/system_wrappers/include)

set(WEBRTC_VAD_SRC
        ${VAD_SRC}/vad_core.c
        ${VAD_SRC}/vad_filterbank.c
        ${VAD_SRC}/vad_gmm.c
        ${VAD_SRC}/vad_sp.c
        ${VAD_SRC}/webrtc_vad.c
        ${VAD_SRC}/spl_sqrt_floor/spl_sqrt_floor.c
        ${VAD_SRC}/rtc_base/checks.cc)

include_directories(${VAD_SRC} ${VAD_SRC}/include ${SIGNAL_PROCESSING} ${SYSTEM})



#    include_directories(${INCLUDE_DIR})     # make sure your .h all inside.

# Specifies a library name, specifies whether the library is STATIC or
# SHARED, and provides relative paths to the source code. You can
# define multiple libraries by adding multiple add_library() commands,
# and CMake builds them for you. When you build your app, Gradle
# automatically packages shared libraries with your APK.




add_library( # Specifies the name of the library.
        webrtcavd

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${webrtc_avd_source}
        )

find_library( # Defines the name of the path variable that stores the
        # location of the NDK library.
        log-lib

        # Specifies the name of the NDK library that
        # CMake needs to locate.
        log)

target_link_libraries(webrtcavd android ${log-lib})

