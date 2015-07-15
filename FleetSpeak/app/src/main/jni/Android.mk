

TOP_PATH := $(call_my_dir)

#Opus library
include $(CLEAR_VARS)
LOCAL_PATH += $(TOP_PATH)/opus
LOCAL_MODULE := "Opus"
LOCAL_C_FILES := opus_encoder.c opus_decoder.c opus_multistream.c  opus_multistream_encoder.c  opus_multistream_decoder.c
include $(BUILD_SHARED_LIBRARY)

