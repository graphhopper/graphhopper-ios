class.list: dependencies/trove/trove4j-stripped.jar dependencies/class-exclude.grep
	@if [ "$(MAKECMDGOALS)" != "class.list" ]; then echo "error: You need to run \`make class.list\` first"; exit 1; fi
	@rm -f $@
	find graphhopper/core/src/main/java graphhopper/tools-lgpl/src/main/java -name '*.java' | grep -vf dependencies/class-exclude.grep >> $@
	unzip -v $< | grep class | grep -v ".*$$.*" | sed s/class/java/ | sed 's/\(.*\)\(gnu\)/dependencies\/trove\/src\/gnu/' >> $@
	find dependencies/fake_slf4j/src -name '*.java' >> $@

# j2objc

j2objc:
	@echo "warning: 'j2objc' doesn't exist and needs to be downloaded, \
this may take some time... To skip this, manually place the j2objc dist directory at /graphhopper-ios/j2objc."
	@curl -L -o j2objc-0.9.6.1.zip "https://github.com/google/j2objc/releases/download/0.9.6.1/j2objc-0.9.6.1.zip"; \
	unzip j2objc-0.9.6.1.zip; \
	mv j2objc-0.9.6.1 j2objc; \
	rm j2objc-0.9.6.1.zip

# Trove

# find out dependencies from class list via autojar tool
dependencies/trove/trove4j-stripped.jar : dependencies/trove/trove-class.list | dependencies/trove/target/classes
	@rm -f dependencies/trove/trove4j-stripped.jar
	java -jar dependencies/autojar-2.1/autojar.jar -o $@ -c dependencies/trove/target/classes @$<

# package trove if it wasn't packaged already
dependencies/trove/target/classes:
	cd dependencies/trove && mvn package
