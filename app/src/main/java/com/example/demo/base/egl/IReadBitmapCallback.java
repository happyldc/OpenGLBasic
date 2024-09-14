package com.example.demo.base.egl;

import java.nio.ByteBuffer;

/**
 * TODO 类描述
 *
 * @author HB.LDC
 * @date 2024-09-13
 */
public interface IReadBitmapCallback {
    void onRead(ByteBuffer buffer, int width, int height);
}
