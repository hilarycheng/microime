#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <android/log.h>
#ifdef SQLITE
#include "sqlite3.h"
#endif

#define  LOG_TAG    "Cangjie"
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#ifndef SQLITE
#include "phrase_internal.h"
#endif

int phrase_count = 0;
int phrase_index = 0;
int phrase_max   = 0;
int phrase_saved = 0;
char phrase_path[1024];
char phrase_user_path[1024];
#ifndef SQLITE
int phrase_map[32768];
int phrase_freq[sizeof(phrase) / sizeof(struct PHRASE_INDEX)];
#define PHRASE_TOTAL_USER 2048
jchar phrase_user[PHRASE_TOTAL_USER][4];
int phrase_user_search_count = 0;
#define PHRASE_USER_KEY  0
#define PHRASE_USER_FREQ 1
#define PHRASE_USER_VAL  2
#endif

#ifdef SQLITE
jchar phrase_last_key = 0;
jchar phrase_word[256][10];
int   phrase_length[256];
int   phrase_frequency[256];
int   phrase_rowid[256];
int   counter = 0;
int   phrase_exists = 0;
char sql[1024];
char utf[1024];
static sqlite3 *db;
#endif

void init_phrase()
{
#ifndef SQLITE
  memset((char *) phrase_map,  0, sizeof(phrase_map));
  memset((char *) phrase_user, 0, sizeof(phrase_user));
  LOGE("User Phrase Size : %d", sizeof(phrase_user));
#endif
  phrase_count = 0;
  phrase_index = 0;
  phrase_max   = 0;
  phrase_saved = 0;
}

#ifdef SQLITE
static int callback(void *NotUsed, int argc, char **argv, char **azColName)
{
  if (counter >= 256) return 0;

  unsigned char *ptr = (unsigned char *) &phrase_word[counter][0];
  int count, index = 0, state = 0, bits = 0;

  index = 0;
  for (count = 0; count < strlen(argv[0]);) {
    ptr[index + 0] = ptr[index + 1] = 0;
    if (((argv[0][count] & 0x00FF) & 0x80) == 0) {
      ptr[index + 1] = argv[0][count] & 0x7f;
      ptr[index + 0] = 0;
      count += 1;
    } else if (((argv[0][count] & 0x00FF) & 0xe0) == 0xc0) {
      ptr[index + 1] = (argv[0][count] >> 2) & 0x07;
      ptr[index + 0] = ((argv[0][count] << 6) & 0xc0) | (argv[0][count + 1] & 0x3f);
      count += 2;
    } else if (((argv[0][count] & 0x00FF) & 0xe0) == 0xe0) {
      ptr[index + 1] = ((argv[0][count + 0] << 4) & 0xf0) | ((argv[0][count + 1] >> 2) & 0x0f);
      ptr[index + 0] = ((argv[0][count + 1] << 6) & 0xc0) | ((argv[0][count + 2]     ) & 0x3f);
      count += 3;
    }
    index += 2;
  }
  phrase_length[counter] = (index >> 1);
  ptr[index + 0] = 0;
  ptr[index + 1] = 0;
  phrase_frequency[counter] = atoi(argv[1]);
  phrase_rowid[counter]     = atoi(argv[2]);
  if (phrase_max < phrase_length[counter]) phrase_max = phrase_length[counter];
  
  /* sql[0] = 0; */
  /* for (count = 0; count < (index >> 1); count++) { */
  /*   sprintf(sql + strlen(sql), "%04X ", phrase_word[counter][count]); */
  /* } */
  /* LOGE("Phase Length : %d %d %d , Counter : %d, Word : %s, Utf : %s", index, */
  /*      phrase_length[counter], phrase_max, */
  /*      counter, sql, utf); */
  counter++;

  /* LOGE("Value : %s", argv[1]); */
  
  return 0;
}
#endif

int search_phrase(jchar index)
{
#ifndef SQLITE  
  int min = 0, max = sizeof(phraseindex) / sizeof(struct PHRASE_INDEX), mid = 0;
#endif
  int count = 0;
  int ch = (int) index;
  int loop = 1;
  int found = -1;
  int total = 0;
  /* LOGE("Phrase : %d", sizeof(phraseindex) / sizeof(struct PHRASE_INDEX)); */

#ifdef SQLITE  
  counter = 0;
  phrase_max   = 0;
  snprintf(sql, 1024, "select phrase, frequency, rowid from phrase where key = %d order by frequency desc limit 256", index);
  int rc = sqlite3_exec(db, sql, callback, 0, 0);
  if (rc != 0) return 0;

  phrase_index = 0;
  phrase_count = counter;
  phrase_last_key = index;
  /* LOGE("Total Phrase : %d", phrase_count); */

  return phrase_count;
#endif

#ifndef SQLITE
  while (max > min) {
    if (phraseindex[min].c == index) {
      found = min;
      break;
    }
    if (phraseindex[max].c == index) {
      found = max;
      break;
    }
    if (min == max)
      break;
    mid = (min + max) / 2;
    if (phraseindex[mid].c == index) {
      found = mid;
      break;
    }
    if (phraseindex[mid].c > index) {
      max = mid - 1;
    } else {
      min = mid + 1;
    }
    total++;
  }
  if (found >= 0) {
    int count = 0;
    /* LOGE("Phrase : %d %d %d %d %d %d", total, found, phraseindex[found].index, phraseindex[found].size, ch, phraseindex[found].c); */
    phrase_index = phraseindex[found].index;
    phrase_count = phraseindex[found].size;
    phrase_max   = phraseindex[found].maxphrase;
    /* if (pcount >= 10) pcount = 10; */
    /* for (count = pindex; count < pindex + pcount; count++) { */
    /*   LOGE("Phrase : %s", phrase[count]); */
    /* } */
    for (count = 0; count < phrase_count; count++) {
      phrase_map[count] = phrase_index + count;
      /* LOGE("Phrase Map0 : %d %d %d", count, phrase_map[count], phrase_freq[phrase_map[count]]); */
    }

    /* LOGE("Phrase Count 0 : %d %d", phrase_count, index); */
    for (count = 0; count < PHRASE_TOTAL_USER; count++) {
      if (phrase_user[count][PHRASE_USER_KEY] == index) {
        phrase_map[phrase_count] = -(count + 1);
	phrase_count++;
      }
    }
    /* LOGE("Phrase Count 1 : %d", phrase_count); */

    int n = phrase_count;
    int swap = 1;
    int temp = 0;
    int first = 0;
    int second = 0;
    do {
      swap = 0;
      int i = 0;
      for (i = 0; i < n - 1; i++) {
        first = phrase_map[i];
	second = phrase_map[i + 1];
	if (first >=  0)  first = phrase_freq[first];  else first  = phrase_user[-first - 1][PHRASE_USER_FREQ];
	if (second >= 0) second = phrase_freq[second]; else second = phrase_user[-second - 1][PHRASE_USER_FREQ];
    	if (first < second) {
    	  temp = phrase_map[i];
    	  phrase_map[i] = phrase_map[i + 1];
    	  phrase_map[i + 1] = temp;
    	  swap = 1;
    	}
      }
    } while (swap);

    /* for (count = 0; count < phrase_count; count++) { */
    /*   LOGE("Phrase Map1 : %d %d %d", count, phrase_map[count], phrase_freq[phrase_map[count]]); */
    /* } */
    /* LOGE("Search Phrase 3"); */

  } else {
    phrase_index = 0;
    phrase_count = 0;
    phrase_max   = 0;
    memset(phrase_map, 0, sizeof(phrase_map));
    /* LOGE("No Phrase : %d", sizeof(phraseindex) / sizeof(struct PHRASE_INDEX)); */
  }

  return phrase_count;
#endif
}

int get_phrase_count()
{
  return phrase_count;
}

int get_phrase_index()
{
  return phrase_index;
}

int get_phrase_max()
{
  /* LOGE("Phrase Max : %d", phrase_max); */
  return phrase_max;
}

jchar* get_phrase(int index)
{
#ifndef SQLITE
  /* LOGE("Get Phrase : %d %d %d", index, phrase_index, index - phrase_index); */
  int i = phrase_map[index - phrase_index];
  if (i >= 0) 
    return &phrase[i][1];
  else
    return &phrase_user[-i - 1][PHRASE_USER_VAL];
#endif
#ifdef SQLITE
  /* LOGE("Get Phrase : %d %04X", index, phrase_word[index][0] & 0x00FFFF); */
  return phrase_word[index];
#endif
}

int get_phrase_length(int index)
{
#ifndef SQLITE
  int i = phrase_map[index - phrase_index];
  if (i >= 0) 
    return (int) phrase[i][0];
  else
    return 1;
#endif
  /* LOGE("Phrase Len :  %d %d", index, phrase_length[index]); */
#ifdef SQLITE
  return phrase_length[index];
#endif
}

void update_phrase_frequency(int index)
{
#ifndef SQLITE
  /* LOGE("Update Phrase Frequency : %d", index); */
  phrase_saved = 1;
  int i = phrase_map[index - phrase_index];
  if (i >= 0)
    phrase_freq[i]++;
  else
    phrase_user[-i - 1][PHRASE_USER_FREQ]++;
#endif
#ifdef SQLITE
  int i = 0, count;
  utf[0] = 0;
  for (count = 0; count < phrase_length[index]; count++) {
    unsigned short value = phrase_word[index][count];
    if ((value & 0xFF80) == 0) {
      utf[i] = value & 0x7F;
      i += 1;
    } else if ((value & 0xF800) == 0) {
      utf[i + 1] = (value & 0x3F) | 0x80;
      utf[i + 0] = ((value >> 6) & 0x1F) | 0xc0;
      i += 2;
    } else {
      utf[i + 2] = (value         & 0x3F) | 0x80;
      utf[i + 1] = ((value >>  6) & 0x3F) | 0x80;
      utf[i + 0] = ((value >> 12) & 0x0F) | 0xe0;
      i += 3;
    }
  }
  utf[i] = 0;

  sql[0] = 0;
  // snprintf(sql, 1024, "update phrase set frequency = frequency + 1 where key = %d and phrase like '%s'", phrase_last_key, utf);
  snprintf(sql, 1024, "update phrase set frequency = frequency + 1 where rowid = %d", phrase_rowid[index]);
  /* LOGE("Update Frequency : %s", sql); */
  
  int rc = sqlite3_exec(db, sql, 0, 0, 0);
  if (rc != 0) {
    LOGE("Sql Error");
  }
#endif
}

void load_phrase(char *path)
{
  strncpy(phrase_path,         path, sizeof(phrase_path));
  strncat(phrase_path, "/phrase.dat", sizeof(phrase_path));

  strncpy(phrase_user_path,              path, sizeof(phrase_user_path));
  strncat(phrase_user_path, "/phrase_user.dat", sizeof(phrase_user_path));

#ifdef SQLITE
  sqlite3_open(phrase_path, &db);
#endif
#ifndef SQLITE
  int clear = 1;
  char key[8];
  char buf[8];

  memset(key, 0, 8);
  strcpy(key, "PHRAS0");

  FILE *file = fopen(phrase_path, "r");
  if (file != 0) {
    int read = fread(buf, 1, sizeof(buf), file);
    if (memcmp(buf, key, 8) == 0) {
      int read = fread(phrase_freq, 1, sizeof(phrase_freq), file);
      fclose(file);
      if (read == sizeof(phrase_freq)) clear = 0;
    }
  }

  file = fopen(phrase_user_path, "r");
  if (file != 0) {
    int read = fread(buf, 1, sizeof(buf), file);
    if (memcmp(buf, key, 8) == 0) {
      int read = fread(phrase_user, 1, sizeof(phrase_user), file);
      fclose(file);
      if (read == sizeof(phrase_user)) clear = 0;
    }
  }

  if (clear != 0) {
    memset(phrase_freq, 0, sizeof(phrase_freq));
    memset(phrase_user, 0, sizeof(phrase_user));
  }
#endif
}

void save_phrase()
{
#ifndef SQLITE
  char key[8];

  if (phrase_saved == 0) return;
  phrase_saved = 0;

  memset(key, 0, 8);
  strcpy(key, "PHRAS0");
  FILE *file = fopen(phrase_path, "w");
  if (file != NULL) {
    fwrite(key, 1, sizeof(key), file);
    fwrite(phrase_freq, 1, sizeof(phrase_freq), file);
    fclose(file);
  }
  file = fopen(phrase_user_path, "w");
  if (file != NULL) {
    fwrite(key, 1, sizeof(key), file);
    fwrite(phrase_user, 1, sizeof(phrase_user), file);
    fclose(file);
  }
#endif
#ifdef SQLITE
  sqlite3_close(&db);
  sqlite3_open(phrase_path, &db);
#endif
}

void clear_phrase()
{
#ifndef SQLITE
  /* LOGE("Clear Phrase"); */
  memset(phrase_freq, 0, sizeof(phrase_freq));
#endif
#ifdef SQLITE
  sqlite3_close(&db);
  unlink(phrase_path);
#endif
}

jint get_phrase_frequency(int index)
{
  /* LOGE("Phrase Frequency : %d %d %d", index, phrase_map[index], phrase_freq[phrase_map[index]]); */
#ifndef SQLITE
  int i = phrase_map[index - phrase_index];
  if (i >= 0)
    return phrase_freq[i];
  else
    return phrase_user[-i - 1][PHRASE_USER_FREQ];
#endif
#ifdef SQLITE
  return phrase_frequency[index];
#endif
}

#ifdef SQLITE
static int count_callback(void *NotUsed, int argc, char **argv, char **azColName)
{
  int value = atoi(argv[0]);
  if (argc == 1 && argv[0][0] == '0') phrase_exists = 1;
  /* LOGE("Count Callback %d %d %s", argc, value, argv[0]); */

  return 0;
}
#endif

void learn_phrase(jchar key, jchar value)
{
#ifdef SQLITE
  int i = 0, count;

  sql[0] = 0;
  utf[0] = 0;
  
  if ((value & 0xFF80) == 0) {
    utf[i] = value & 0x7F;
    i += 1;
  } else if ((value & 0xF800) == 0) {
    utf[i + 1] = (value & 0x3F) | 0x80;
    utf[i + 0] = ((value >> 6) & 0x1F) | 0xc0;
    i += 2;
  } else {
    utf[i + 2] = (value         & 0x3F) | 0x80;
    utf[i + 1] = ((value >>  6) & 0x3F) | 0x80;
    utf[i + 0] = ((value >> 12) & 0x0F) | 0xe0;
    i += 3;
  }
  utf[i] = 0;

  phrase_exists = 0;
  snprintf(sql, 1024, "select count(1) from phrase where key = %d and length = 1 and phrase = '%s'", key, utf);
  int rc = sqlite3_exec(db, sql, count_callback, 0, 0);
  if (rc != 0) return;
  
  sql[0] = 0;
  if (phrase_exists == 0) {
    snprintf(sql, 1024, "update phrase set frequency = frequency + 1 where key = %d and length = 1 and phrase = '%s'",
	     key, utf);
  } else {
    snprintf(sql, 1024, "insert into phrase (key, phrase, length, frequency) values (%d, '%s', 1, 1)",
	     key, utf);
  }
  
  sqlite3_exec(db, sql, 0, 0, 0);
#endif

#ifndef SQLITE
  int found = 0;
  int count = 0;
  int done  = 0;
  int min = 0, max = sizeof(phraseindex) / sizeof(struct PHRASE_INDEX), mid = 0;

  while (max > min) {
    if (phraseindex[min].c == key) {
      found = min;
      break;
    }
    if (phraseindex[max].c == key) {
      found = max;
      break;
    }
    if (min == max)
      break;
    mid = (min + max) / 2;
    if (phraseindex[mid].c == key) {
      found = mid;
      break;
    }
    if (phraseindex[mid].c > key) {
      max = mid - 1;
    } else {
      min = mid + 1;
    }
  }
  done = 0;
  if (found >= 0) {
    for (count = 0; count < phraseindex[found].size; count++) {
      if (phrase[found + count][0] >= 2) continue;
      if (phrase[found + count][1] != key) continue;
      phrase_freq[found + count]++;
      done = 1; 
    }
  }

  found = 0;
  for (count = 0; count < PHRASE_TOTAL_USER; count++) {
    if (phrase_user[count][PHRASE_USER_KEY] == key &&
        phrase_user[count][PHRASE_USER_VAL] == value) {
      found = 1;
      phrase_user[count][PHRASE_USER_FREQ]++;
      break;
    }
  }
  if (found == 0) {
    for (count = 0; count < PHRASE_TOTAL_USER; count++) {
      if (phrase_user[count][PHRASE_USER_KEY] == 0 && found == 0) {
        found = 1;
        phrase_user[count][PHRASE_USER_KEY]  = key;
        phrase_user[count][PHRASE_USER_VAL]  = value;
        phrase_user[count][PHRASE_USER_FREQ] = 1;
	break;
      }
    }
  }
  if (found == 0) {
    int min = 100000000;
    for (count = 0; count < PHRASE_TOTAL_USER; count++) {
      if (phrase_user[count][PHRASE_USER_FREQ] < min) min = phrase_user[count][PHRASE_USER_FREQ];
    }
    for (count = 0; count < PHRASE_TOTAL_USER; count++) {
      if (phrase_user[count][PHRASE_USER_FREQ] == min) {
        phrase_user[count][PHRASE_USER_KEY]  = key;
        phrase_user[count][PHRASE_USER_VAL]  = value;
        phrase_user[count][PHRASE_USER_FREQ] = 1;
	break;
      }
    }
  }
#endif
}
