//
// Created by lindiancheng on 2024/8/27.
//

#ifndef TQDEMO_ANDROIDLOG_H
#define TQDEMO_ANDROIDLOG_H

#include "android/log.h"

#define  TAG "native-lib"
#define  LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__);
#define  LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG,__VA_ARGS__);
#define  LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG,__VA_ARGS__);
#endif //TQDEMO_ANDROIDLOG_H
