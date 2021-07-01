#include "SharedObject.h"

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "SharedObject", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "SharedObject", __VA_ARGS__))
extern float TestOpencv(float* buf, int len);
extern float TestMath();
extern "C" {

    float ExternTestOpencv(float* buf, int len) //这个用来导出给Android JNI使用
    {
        return TestOpencv(buf, len);
    }

    float ExternTestMath()//这个用来导出给Android JNI使用
    {
        return TestMath();
    }

    //C++导出给Java类使用的命名规范
    //Java_packagename_classname_functionname
    //第一个传参总是JNIEnv* env
    //第二个传参 如果是static成员函数就是jclass type,
    //		    如果是非static成员函数就是jobject thiz,
    //第三个传参才是真正的参数
    JNIEXPORT jfloat JNICALL
        Java_com_example_myapplication8_MainActivity_CVTestSum(JNIEnv* env, jclass type, jfloatArray buf) //这个用来导出给Java使用
    {
        auto len = env->GetArrayLength(buf);
        jboolean notcopy = JNI_FALSE;
        float* fptr = env->GetFloatArrayElements(buf, &notcopy);//从Java内存转换到native指针
        return TestOpencv(fptr, len);
    }
    JNIEXPORT jfloat JNICALL
        Java_com_example_myapplication8_MainActivity_TestSum(JNIEnv* env, jclass type, jfloatArray buf)//这个用来导出给Java使用
    {
        auto len = env->GetArrayLength(buf);
        jboolean notcopy = JNI_FALSE;
        float* fptr = env->GetFloatArrayElements(buf, &notcopy);
        float sum = 0;
        for (size_t i = 0; i < len; i++)
        {
            sum += fptr[i];
        }
        return sum;
    }
    JNIEXPORT jfloat JNICALL
        Java_com_example_myapplication8_MainActivity_TestMath(JNIEnv* env, jclass type)//这个用来导出给Java使用
    {
        return TestMath();
    }

	/*此简单函数返回平台 ABI，此动态本地库为此平台 ABI 进行编译。*/
	const char * SharedObject::getPlatformABI()
	{
	#if defined(__arm__)
	#if defined(__ARM_ARCH_7A__)
	#if defined(__ARM_NEON__)
		#define ABI "armeabi-v7a/NEON"
	#else
		#define ABI "armeabi-v7a"
	#endif
	#else
		#define ABI "armeabi"
	#endif
	#elif defined(__i386__)
		#define ABI "x86"
	#else
		#define ABI "unknown"
	#endif
		LOGI("This dynamic shared library is compiled with ABI: %s", ABI);
		return "This native library is compiled with ABI: %s" ABI ".";
	}

	void SharedObject()
	{
	}

	SharedObject::SharedObject()
	{
	}

	SharedObject::~SharedObject()
	{
	}
}
