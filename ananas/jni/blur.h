#ifndef BLUR_H
#define BLUR_H

int stackBlur(float* radius, unsigned char* srcRed, unsigned char* srcGreen, unsigned char* srcBlue, int* width, int* height,
              unsigned char* dstRed, unsigned char* dstGreen, unsigned char* dstBlue);

#endif //BLUR_H
