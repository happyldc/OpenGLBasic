cmake_minimum_required(VERSION 3.10.2)

# 手动指定 Android NDK 路径

set(egl ${CMAKE_SOURCE_DIR}/egl)

file(GLOB ALLCPP *.cpp  ${egl}/*.cpp)
# 添加你的本地库
add_library( # 设置库名称
        native-lib

        # 设置库类型为共享库
        SHARED

        # 你的源文件路径
#        native-lib.cpp
        ${ALLCPP}
)

# 查找 NDK 提供的 log 库
find_library(log-lib log)

# 查找 NDK 提供的 android 库
find_library(android-lib android)

find_library(
        jnigraphics-lib
        jnigraphics
)

# 链接 log 和 android 库
target_link_libraries(native-lib
        ${log-lib}
        ${android-lib}
        ${jnigraphics-lib}
        #openGL 环境
        EGL
        GLESv2
        ${android-lib}


)
