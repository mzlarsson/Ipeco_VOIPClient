LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#include the .mk files
include celt_sources.mk
include silk_sources.mk
include opus_sources.mk
include fleetspeak.mk


MY_MODULE_DIR       := app

LOCAL_MODULE        := Opus

#fixed point sources
SILK_SOURCES += $(SILK_SOURCES_FIXED)

#ARM build
CELT_SOURCES += $(CELT_SOURCES_ARM)
SILK_SOURCES += $(SILK_SOURCES_ARM)
LOCAL_SRC_FILES     := \
$(CELT_SOURCES) $(SILK_SOURCES) $(OPUS_SOURCES) $(FLEETSPEAK_SOURCES)

LOCAL_LDLIBS        := -lm -llog

LOCAL_C_INCLUDES    := \
$(LOCAL_PATH)/include \
$(LOCAL_PATH)/silk \
$(LOCAL_PATH)/silk/fixed \
$(LOCAL_PATH)/celt \
$(LOCAL_PATH)/native_fleetspeak


LOCAL_CFLAGS        := -DNULL=0 -DSOCKLEN_T=socklen_t -DLOCALE_NOT_USED -D_LARGEFILE_SOURCE=1 -D_FILE_OFFSET_BITS=64
LOCAL_CFLAGS        += -Drestrict='' -D__EMX__ -DOPUS_BUILD -DFIXED_POINT=1 -DDISABLE_FLOAT_API -DUSE_ALLOCA -DHAVE_LRINT -DHAVE_LRINTF -O3 -fno-math-errno
LOCAL_CPPFLAGS      := -DBSD=1
LOCAL_CPPFLAGS      += -ffast-math -O3 -funroll-loops

include $(BUILD_SHARED_LIBRARY)



# LOCAL_PATH := $(call my-dir)
# #Opus library
# include $(CLEAR_VARS)

# OPUS_PATH :=$LOCAL_PATH/opus
# LOCAL_MODULE    := Opus
# LOCAL_C_INCLUDES := $OPUS_PATH/celt
# LOCAL_SRC_FILES:=opus/src/opus_encoder.c 

# LOCAL_CFLAGS	:= -Drestrict='' -D__EMX__ -DOPUS_BUILD -DFIXED_POINT -DDISABLE_FLOAT_API -DUSE_ALLOCA -DHAVE_LRINT -DHAVE_LRINTF -O3 -fno-math-errno
# include $(BUILD_SHARED_LIBRARY)

