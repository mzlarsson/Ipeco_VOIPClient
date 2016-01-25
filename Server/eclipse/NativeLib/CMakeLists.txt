cmake_minimum_required(VERSION 3.4)

project(opus C)

SET(EXECUTABLE_OUTPUT_PATH ${opus_SOURCE_DIR}/bin)
SET(LIBRARY_OUTPUT_PATH ${opus_SOURCE_DIR}/lib)
SET(RUNTIME_OUTPUT_DIRECTORY ${opus_SOURCE_DIR}/bin)

SET(opus_BIN ${opus_SOURCE_DIR}/bin)

message("Source Dir:" ${opus_SOURCE_DIR})

add_definitions(/DHAVE_CONFIG_H)

IF(WIN32)
IF(MSVC12)
message("Detected MSVC12 compiler")
set(MSVC_VER VC12)
ELSEIF(MSVC11)
message("Detected MSVC11 compiler")
set(MSVC_VER VC11)
ELSEIF(MSVC10)
message("Detected MSVC10 compiler")
set(MSVC_VER VC10)
ELSE(MSVC12)
message("WARNING: Unknown/unsupported MSVC version")
ENDIF(MSVC12)
ENDIF(WIN32)



IF(WIN32)
SET(opusIncludes
${opus_SOURCE_DIR}
${opus_SOURCE_DIR}/include
${opus_SOURCE_DIR}/Win32
)
ELSEIF(UNIX)
set(CMAKE_C_COMPILER gcc)
SET(opusIncludes
${opus_SOURCE_DIR}
${opus_SOURCE_DIR}/include
/usr/local/include
)
ENDIF(WIN32)

message("opusIncludes directories:" ${opusIncludes})

IF(WIN32)
SET(opusLink
${opus_SOURCE_DIR}/lib
)
ELSEIF(UNIX)
SET(opusLink
${opus_SOURCE_DIR}/lib
/usr/local/lib
/usr/lib
)
ENDIF(WIN32)

include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

add_subdirectory(celt)
add_subdirectory(silk)
add_subdirectory(src)

# Allow the developer to select if Dynamic or Static libraries are built
OPTION (BUILD_SHARED_LIBS "Build Shared Libraries" OFF)
# Set the LIB_TYPE variable to STATIC
SET (LIB_TYPE STATIC)
IF (BUILD_SHARED_LIBS)
  # User wants to build Dynamic Libraries, so change the LIB_TYPE variable to CMake keyword 'SHARED'
  SET (LIB_TYPE SHARED)
ENDIF (BUILD_SHARED_LIBS)

set(CMAKE_SHARED_LINKER_FLAGS "-Wl,--export-all-symbols")

add_library(${PROJECT_NAME} SHARED $<TARGET_OBJECTS:celt>
                                   $<TARGET_OBJECTS:silk>
                                   $<TARGET_OBJECTS:opus_lib>)




