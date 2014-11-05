.SUFFIXES:
.PHONY: all clean

include make/common.mk

all:
	@:

GEN_OBJC_DIR := src
TRANSLATE_LIST := class.list
TRANSLATE_JAVA_FULL = $(shell if [ -e $(TRANSLATE_LIST) ]; then cat $(TRANSLATE_LIST); fi)
TRANSLATE_JAVA_RELATIVE := $(shell sed -f dependencies/class.sed $(TRANSLATE_LIST))
TRANSLATE_SOURCEPATH = graphhopper/core/src/main/java:dependencies/trove/src/:dependencies/fake_slf4j/src/
TRANSLATE_ARGS := --doc-comments --mem-debug -encoding UTF-8 --extract-unsequenced --final-methods-as-functions --hide-private-members
include make/translate.mk

ifdef PRODUCT_NAME
FAT_LIB_NAME := $(PRODUCT_NAME)
else
FAT_LIB_NAME := graphhopper
endif
FAT_LIB_SOURCES_RELATIVE = $(TRANSLATE_JAVA_RELATIVE:%.java=%.m)
FAT_LIB_SOURCE_DIRS := $(GEN_OBJC_DIR)
FAT_LIB_COMPILE := $(CLANG) $(DEBUGFLAGS) -Ij2objc/include -I$(GEN_OBJC_DIR) $(OTHER_CFLAGS)
#FAT_LIB_PRECOMPILED_HEADER := prefix.h
include make/library.mk

TRANSLATIONS_DIR = $(ARCH_LIB_DIR)/translations
TRANSLATIONS_PATH = graphhopper/core/src/main/resources/com/graphhopper/util
TRANSLATIONS_EXISTING = $(shell if [ -d $(TRANSLATIONS_PATH) ] ; then find $(TRANSLATIONS_PATH) -name "*.txt"; fi)
TRANSLATIONS = $(TRANSLATIONS_EXISTING:$(TRANSLATIONS_PATH)%=$(TRANSLATIONS_DIR)%)

$(TRANSLATIONS_DIR)/%.txt : $(TRANSLATIONS_PATH)/%.txt
	cp $< $@

dirs: | $(ARCH_BUILD_DIR) $(TRANSLATIONS_DIR)
	@:

$(ARCH_BUILD_DIR) $(TRANSLATIONS_DIR):
	@mkdir -p $@

all: dirs translate $(FAT_LIB_LIBRARY) $(TRANSLATIONS)

clean:
	@echo Deleting $(GEN_OBJC_DIR) $(ARCH_BUILD_DIR) $(ARCH_LIB_DIR)
	@rm -rf $(GEN_OBJC_DIR) $(ARCH_BUILD_DIR) $(ARCH_LIB_DIR)
