#ifndef MATRIX_H
#define MATRIX_H

struct Bitmap;   // forward declaration is enough for Bitmap* used in applyMatrix below

void multiplyMatricies(float a[4][4], float b[4][4], float c[4][4]);
void identMatrix(float *matrix);
void saturateMatrix(float matrix[4][4], float* saturation);
void applyMatrix(Bitmap* bitmap, float matrix[4][4]);
void applyMatrixToPixel(unsigned char* red, unsigned char* green, unsigned char* blue, float matrix[4][4]);


#endif //MATRIX_H
