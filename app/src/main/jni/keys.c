#include <jni.h>

/*
JNIEXPORT jstring JNICALL
Java_com_companion_app_di_NetworkModule_getBaseUrl(JNIEnv *env, jobject networkModule) {
    return (*env)-> NewStringUTF(env, "aHR0cDovL2FwaWNvbXBhbmlvbmRldi5hcC1zb3V0aC0xLmVsYXN0aWNiZWFuc3RhbGsuY29tLw==");
}

JNIEXPORT jstring JNICALL
Java_com_companion_app_di_NetworkModule_getProdUrl(JNIEnv *env, jobject thiz) {
    return (*env)-> NewStringUTF(env, "aHR0cDovL2FwaWNvbXBhbmlvbmRldi5hcC1zb3V0aC0xLmVsYXN0aWNiZWFuc3RhbGsuY29tLw==");
}

JNIEXPORT jstring JNICALL
Java_com_companion_app_base_CompanionApplication_getPlacesKey(JNIEnv *env, jobject thiz) {
    return (*env)-> NewStringUTF(env, "QUl6YVN5QmdpXzBTdHFPSW01SkNjMlFPeW96Z2dqS29XTzJfdU40");
}

JNIEXPORT jstring JNICALL
Java_com_companion_app_base_CompanionApplication_getDivineAPIKey(JNIEnv *env, jobject thiz) {
    return (*env)-> NewStringUTF(env, "ZTNjYTA0NDlmYTJlYTc3MDFhN2FjNTNmYjcxOWM1MWE=");
}
JNIEXPORT jstring JNICALL
Java_com_companion_app_base_CompanionApplication_getDivineAPIAccessToken(JNIEnv *env, jobject thiz) {
    return (*env)-> NewStringUTF(env, "ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SnBjM01pT2lKb2RIUndjem92TDJGemRISnZZWEJwTFRFdVpHbDJhVzVsWVhCcExtTnZiUzloY0drdllYVjBhQzFoY0drdGRYTmxjaUlzSW1saGRDSTZNVGN5TlRNek9ERTBOQ3dpYm1KbUlqb3hOekkxTXpNNE1UUTBMQ0pxZEdraU9pSjRiRFl4VjA0NFlXSk5kVzh3ZDBrMUlpd2ljM1ZpSWpvaU1qUXhOU0lzSW5CeWRpSTZJbVUyWlRZMFltSXdZall4TWpaa056TmpObUk1TjJGbVl6TmlORFkwWkRrNE5XWTBObU01WkRjaWZRLk1KS2pXUnBzR2NvZGs3c252d3ppcmRGNUZ3bVFBbEhkY1JRRjZ1V0JJQUk=");
}

JNIEXPORT jstring JNICALL
Java_com_companion_app_di_NetworkModule_getDivineAPIBaseUrl(JNIEnv *env, jobject thiz) {
    return (*env)-> NewStringUTF(env, "aHR0cHM6Ly9hc3Ryb2FwaS0zLmRpdmluZWFwaS5jb20vaW5kaWFuLWFwaS8=");
}*/

JNIEXPORT jstring JNICALL
Java_com_companion_astrodating_base_CompanionApplication_getPlacesKey(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "QUl6YVN5QlNmZ19wajkyT05aWFByYTkwSlR3akItWUh6T2ZKUUt3");
}

JNIEXPORT jstring JNICALL
Java_com_companion_astrodating_base_CompanionApplication_getDivineAPIKey(JNIEnv *env,
                                                                         jobject thiz) {
    return (*env)->NewStringUTF(env, "ZTNjYTA0NDlmYTJlYTc3MDFhN2FjNTNmYjcxOWM1MWE=");
}

JNIEXPORT jstring JNICALL
Java_com_companion_astrodating_base_CompanionApplication_getDivineAPIAccessToken(JNIEnv *env,
                                                                                 jobject thiz) {
    return (*env)->NewStringUTF(env,
                                "ZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKSVV6STFOaUo5LmV5SnBjM01pT2lKb2RIUndjem92TDJGemRISnZZWEJwTFRFdVpHbDJhVzVsWVhCcExtTnZiUzloY0drdllYVjBhQzFoY0drdGRYTmxjaUlzSW1saGRDSTZNVGN5TlRNek9ERTBOQ3dpYm1KbUlqb3hOekkxTXpNNE1UUTBMQ0pxZEdraU9pSjRiRFl4VjA0NFlXSk5kVzh3ZDBrMUlpd2ljM1ZpSWpvaU1qUXhOU0lzSW5CeWRpSTZJbVUyWlRZMFltSXdZall4TWpaa056TmpObUk1TjJGbVl6TmlORFkwWkRrNE5XWTBObU01WkRjaWZRLk1KS2pXUnBzR2NvZGs3c252d3ppcmRGNUZ3bVFBbEhkY1JRRjZ1V0JJQUk=");
}

JNIEXPORT jstring JNICALL
Java_com_companion_astrodating_di_NetworkModule_getBaseUrl(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env,
                                "aHR0cHM6Ly9jb21wYW5pb24tYmFja2VuZC1wcm9kdWN0aW9uLWJmMWUudXAucmFpbHdheS5hcHAv");
}

JNIEXPORT jstring JNICALL
Java_com_companion_astrodating_di_NetworkModule_getProdUrl(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env,
                                "aHR0cHM6Ly9jb21wYW5pb24tYmFja2VuZC1wcm9kdWN0aW9uLWJmMWUudXAucmFpbHdheS5hcHAv");
}

JNIEXPORT jstring JNICALL
Java_com_companion_astrodating_di_NetworkModule_getDivineAPIBaseUrl(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env,
                                "aHR0cHM6Ly9hc3Ryb2FwaS0zLmRpdmluZWFwaS5jb20vaW5kaWFuLWFwaS8=");
}

JNIEXPORT jstring JNICALL
Java_com_companion_astrodating_base_CompanionApplication_getAgoraChatAppKey(JNIEnv *env,jobject thiz) {
    return (*env)->NewStringUTF(env,"NjExMjI4NTczIzE0MTk2MjU=");

}