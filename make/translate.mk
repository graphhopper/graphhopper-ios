TRANSLATE_TARGET := $(GEN_OBJC_DIR)/.translate_mark
TRANSLATE_CMD = j2objc/j2objc --prefixes dependencies/prefixes.properties\
 			-sourcepath $(TRANSLATE_SOURCEPATH) $(TRANSLATE_ARGS) -d $(GEN_OBJC_DIR)

translate: $(TRANSLATE_TARGET)
	@:

# Find any files that may have been added to the list since the last translation
TRANSLATE_LAST_FILES = $(shell if [ -e $(TRANSLATE_TARGET) ]; then cat $(TRANSLATE_TARGET); fi)
TRANSLATE_NEW_FILES = $(filter-out $(TRANSLATE_LAST_FILES),$^)

TRANSLATE_MAKE_LIST = $(filter $? $(TRANSLATE_NEW_FILES),$^)

$(TRANSLATE_TARGET): $(TRANSLATE_JAVA_FULL)
	@mkdir -p $(GEN_OBJC_DIR)
#	$(call long_list_to_file,$(TRANSLATE_LIST),$(TRANSLATE_MAKE_LIST))
	@if [ -s $(TRANSLATE_LIST) ]; then \
	  echo Translating sources.; \
	  $(TRANSLATE_CMD) @$(TRANSLATE_LIST); \
	fi
	$(call long_list_to_file,$@,$^)

.PHONY : translate
