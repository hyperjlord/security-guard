#include "com_xu_testNative.h"
#include "jni.h"//◊¢“‚ÃÌº”’‚æ‰
#include<iostream>
#include"C2Java.h"

JNIEXPORT void JNICALL Java_com_xu_testNative_result(JNIEnv* env, jobject thisobj, jstring address)
{
	std::cout << "I'm fine" << std::endl;
	const char* str;
	str = env->GetStringUTFChars(address, NULL);
	if (str == NULL) 
	{
		return; /* OutOfMemoryError already thrown */
	}
	std::cout << str << std::endl;
	result(str);

}

JNIEXPORT void JNICALL Java_com_xu_testNative_test(JNIEnv* env, jobject thisobj)
{
	std::cout << "I'm fine" << std::endl;
}