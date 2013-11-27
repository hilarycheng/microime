#ifndef INPUT_METHOD_H
#define INPUT_METHOD_H

#ifndef X86
#include <jni.h>
#else
#include <common.h>
#endif

typedef enum {
  QUICK   = 0,
  CANGJIE = 1,
  STROKE  = 2
} INPUT_METHOD;

struct _input_method {
  void  (*init)(char *path);
  int   (*maxKey)(void);
  void  (*searchWord)(jchar c0, jchar c1, jchar c2, jchar c3, jchar c4);
  void  (*searchWordMore)(jchar *c0, jchar *c1, jchar *c2, jchar *c3, jchar *c4);
  void  (*searchWordArray)(jchar* c0, int len);
  jboolean (*tryMatchWord)(jchar c0, jchar c1, jchar c2, jchar c3, jchar c4);
  jboolean (*tryMatchWordMore)(jchar* c0, jchar* c1, jchar* c2, jchar* c3, jchar* c4);
  jboolean (*tryMatchWordArray)(jchar *c0, int len);
  void  (*enableHongKongChar)(jboolean hk);
  int   (*totalMatch)(void);
  int   (*updateFrequency)(jchar c0);
  void  (*clearFrequency)(void);
  void  (*reset)(void);
  void  (*setSortingMethod)(int method);
  jchar (*getMatchChar)(int index);
  jint  (*getFrequency)(int index);
  void  (*saveMatch)(void);
  char  mPath[1024];
  char  mBuffer[8];
  int   mTotalMatch;
  int   mSortingMethod;
  int   mSaved;
  jboolean mEnableHK;
};

/* extern struct _input_method *input_method[4]; */

#endif
