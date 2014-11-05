#
# Include this file to enable the following debugging capabilities:
#
# - Print MACRO value:
#	`make print-MACRO` or `make target print-TARGETMACRO`
#
# - Trace rule execution:
#	`make --silent TRACE=1`
#
#
# What's the Value of Macro X?
#
# Allow printing of macros as `make print-MACRO1`.
# It also prints target-specific variables, as `make all print-MACRO1`.
#
# The expected output is:
# Makefile:12: *** X is dogs hate cats with definition $(YS) hate $(ZS) (from file). Stop.
#
# http://www.drdobbs.com/tools/debugging-makefiles/197003338?pgno=2
#

print-%: ; @$(error $* is $($*) with definition $(value $*) (from $(origin $*)))
$(filter-out print-% clean,$(MAKECMDGOALS)): $(filter print-%,$(MAKECMDGOALS))

#
# Why Did File foo Get Built?
# How Does Make Log File Output Relate? (Tracing Rule Execution)
#
# # Sample Makefile:
# .PHONY: all
# all: foo.o bar
# bar:
# 	@touch $@
# 
# # Expected output:
# make: Building foo.o (from foo.c) (foo.c newer)
# cc -c -o foo.o foo.c
# + cc -c -o foo.o foo.c
# Makefile:7: Building bar
# + touch bar
#
# http://www.drdobbs.com/tools/debugging-makefiles/197003338?pgno=4
# http://www.cmcrossroads.com/article/tracing-rule-execution-gnu-make?page=0%2C1
#

ifdef TRACE
OLD_SHELL := $(SHELL)
SHELL = $(warning $(if $@, Building $@)$(if $^, (from $^))$(if $?, ($? newer)))$(OLD_SHELL) -x
endif