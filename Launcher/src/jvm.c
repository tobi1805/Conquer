#include "launcher.h"
#include <assert.h>
#include <dirent.h>
#include <jni.h>
#include <stddef.h>
#include <stdlib.h>
#include <string.h>
#ifdef _WIN32
#include <shlobj.h>
#include <windows.h>
#else
void appendAllJarsFromDir(const char *, char *);
#endif
void runJVM(Configuration configuration) {
	char *classpath = generateClasspath(configuration);
	JavaVMOption *jvmoptions = calloc(
		configuration->numOptions + NUM_PREDEFINED_ARGS, sizeof(JavaVMOption));
	assert(jvmoptions);
	// Just free the first optionstring.
	jvmoptions[0].optionString = classpath;
	jvmoptions[1].optionString = "--enable-preview";
	jvmoptions[2].optionString = "-XX:+ShowCodeDetailsInExceptionMessages";
	for (size_t i = 0; i < configuration->numOptions; i++)
		jvmoptions[NUM_PREDEFINED_ARGS + i].optionString =
			configuration->options[i];
	JavaVMInitArgs vmArgs = {JNI_VERSION_10,
							 configuration->numOptions + NUM_PREDEFINED_ARGS,
							 jvmoptions, 1};
	JavaVM *jvm;
	JNIEnv *env = NULL;
	void *handle = loadJavaLibrary(configuration);
	createJVM func = getHandleToFunction(handle);
	jint status = func(&jvm, (void **)&env, &vmArgs);
	if (status != JNI_OK) {
		fprintf(stderr, "Couldn't create JVM: %d\n", status);
		goto cleanup;
	}
	jclass introClass = (*env)->FindClass(env, "org/jel/gui/Intro");
	assert(introClass);
	jclass stringClass = (*env)->FindClass(env, "java/lang/String");
	assert(stringClass);
	jmethodID mainMethod = (*env)->GetStaticMethodID(env, introClass, "main",
													 "([Ljava/lang/String;)V");
	jobjectArray arr = (*env)->NewObjectArray(env, 0, stringClass, NULL);
	(*env)->CallStaticVoidMethod(env, introClass, mainMethod, arr);
	if ((*env)->ExceptionOccurred(env)) {
		(*env)->ExceptionDescribe(env);
	}
	(*jvm)->DestroyJavaVM(jvm);
cleanup:
	free(jvmoptions[0].optionString);
	free(jvmoptions);
}
char *generateClasspath(Configuration configuration) {
	char *ret = calloc(1024 * 1024 * 16, 1);
	assert(ret);
	strcat(ret, "-Djava.class.path=");
	char *c = "/";
#ifndef _WIN32
	strcat(ret,
		   "/usr/share/java/Conquer.jar:/usr/share/java/Conquer_frontend.jar:");
#else
#ifdef UNICODE
#error UNICODE has to be undefined!
#endif
	TCHAR pf[MAX_PATH];
	SHGetSpecialFolderPathA(NULL, pf, CSIDL_PROGRAM_FILES, FALSE);
	strcat(ret, pf);
	strcat(ret, "\\Conquer\\Conquer.jar;");
	strcat(ret, pf);
	strcat(ret, "\\Conquer\\Conquer_frontend.jar;");
#endif
	for (size_t i = 0; i < configuration->numClasspaths; i++) {
		strcat(ret, configuration->classpaths[i]);
		strcat(ret, c);
		strcat(ret, SEP);
	}
	char *base = getBaseDirectory();
	char *libs = calloc(strlen(base) + 10, 1);
	assert(libs);
	sprintf(libs, "%s%s%s", base, "/libs", c);
	DIR *dir = opendir(libs);
	if (dir != NULL) {
		struct dirent *ent;
		while ((ent = readdir(dir)) != NULL) {
#ifndef _WIN32
			if (ent->d_type == DT_REG) {
#endif
				char *name = ent->d_name;
				size_t ll = strlen(name);
				if (ll >= 4 && memcmp(&name[ll - 4], ".jar", 4) == 0) {
					strcat(ret, libs);
					strcat(ret, name);
					strcat(ret, SEP);
				}
#ifndef _WIN32
			}
#endif
		}
		closedir(dir);
	}
#ifndef _WIN32
	appendAllJarsFromDir("/usr/share/java/conquer/plugins",ret);
	appendAllJarsFromDir("/usr/share/java/conquer/strategies",ret);
	strcat(ret, "/usr/share/conquer/music:");
	strcat(ret, "/usr/share/conquer/sounds:");
	strcat(ret, "/usr/share/conquer/images:");
#endif
	strcat(ret, libs);
	strcat(ret, "music");
	strcat(ret, SEP);
	strcat(ret, libs);
	strcat(ret, "sounds");
	strcat(ret, SEP);
	strcat(ret, libs);
	strcat(ret, "images");
	strcat(ret, SEP);
	strcat(ret, ".");
	free(libs);
	return ret;
}
#ifndef _WIN32
void appendAllJarsFromDir(const char *path, char *to) {
	DIR *dir = opendir(path);
	if (dir != NULL) {
		struct dirent *ent;
		while ((ent = readdir(dir)) != NULL) {
			if (ent->d_type == DT_REG) {
				char *name = ent->d_name;
				size_t ll = strlen(name);
				if (ll >= 4 && memcmp(&name[ll - 4], ".jar", 4) == 0) {
					strcat(to, path);
					strcat(to, name);
					strcat(to, SEP);
				}
			}
		}
	}
}
#endif
