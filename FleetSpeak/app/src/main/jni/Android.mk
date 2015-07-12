

TOP_PATH := $(call_my_dir)/../..

#Opus library
include $(CLEAR_VARS)
LOCAL_PATH := $(TOP_PATH)/libs/opus
LOCAL_MODULE := opus
LOCAL_FILES := opus_encoder.c opus_decoder.c
include $(BUILD_SHARED_LIBRARY)



# Builds the APK
include $(BUILD_PACKAGE)
