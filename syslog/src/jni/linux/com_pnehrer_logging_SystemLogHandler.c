#include <jni.h>
#include "com_pnehrer_logging_SystemLogHandler.h"
#include <stdio.h>
#include <syslog.h>

static char *loggerName;

/*
 * Class:     com_pnehrer_logging_SystemLogHandler
 * Method:    openLog
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_com_pnehrer_logging_SystemLogHandler_openLog
  (JNIEnv *env, jclass clazz, jstring identifier)
{
	loggerName = NULL;
	const char *utf = (*env)->GetStringUTFChars(env, identifier, NULL);
	if(utf != NULL) {
		loggerName = (char *)malloc(sizeof(char) * (1 + strlen(utf)));
		openlog(strcpy(loggerName, utf), LOG_CONS | LOG_ODELAY, LOG_USER);
		(*env)->ReleaseStringUTFChars(env, identifier, utf);
	}

	return 0;
}

/*
 * Class:     com_pnehrer_logging_SystemLogHandler
 * Method:    closeLog
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_pnehrer_logging_SystemLogHandler_closeLog
  (JNIEnv *env, jclass clazz, jint handle)
{
	closelog();
	free(loggerName);
}

/*
 * Class:     com_pnehrer_logging_SystemLogHandler
 * Method:    writeLog
 * Signature: (IILjava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_com_pnehrer_logging_SystemLogHandler_writeLog
  (JNIEnv *env, jclass clazz, jint handle, jint level, jstring message)
{
	static int levelMap[8] = {LOG_DEBUG, LOG_INFO, LOG_NOTICE, LOG_WARNING, LOG_ERR, LOG_CRIT, LOG_ALERT, LOG_EMERG};

	if(level < 0 || level > 7) return;

	const char *utf = (*env)->GetStringUTFChars(env, message, NULL);
	if(utf != NULL) {
		syslog(LOG_USER | levelMap[level], "%s", utf);
		(*env)->ReleaseStringUTFChars(env, message, utf);
	}
}
