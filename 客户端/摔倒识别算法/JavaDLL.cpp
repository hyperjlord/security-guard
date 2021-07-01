// JavaDLL.cpp : 定义 DLL 的导出函数。
//

#include "framework.h"
#include "JavaDLL.h"


// 这是导出变量的一个示例
JAVADLL_API int nJavaDLL=0;

// 这是导出函数的一个示例。
JAVADLL_API int fnJavaDLL(void)
{
    return 0;
}

// 这是已导出类的构造函数。
CJavaDLL::CJavaDLL()
{
    return;
}
